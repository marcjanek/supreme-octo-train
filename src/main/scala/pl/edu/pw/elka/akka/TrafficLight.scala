package pl.edu.pw.elka.akka

import akka.actor.SupervisorStrategy.Escalate
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props}
import akka.event.{Logging, LoggingAdapter}
import pl.edu.pw.elka.enums.{Lanes, Light, Lights, Roads}
import pl.edu.pw.elka.akka.Manager.TrafficLightDataResponse

import scala.collection.immutable.{Map, Vector}
import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout
import pl.edu.pw.elka.akka.LaneCounter.CountCarsOnLane

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object TrafficLight {
  case object GetTrafficLightData
  case class UpdateActiveLight(newLight: Light)
  case class CarNumberResponse(cars: Long, lane: Lanes)
  case object Stop
  case object ErrorAlert

  def props(junctionID: String, roads: Roads, lane: Lanes): Props = Props(new LaneCounter(junctionID, roads, lane))
}

class TrafficLightEmergencyAlertException(private val message: String = "",
                                 private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

class TrafficLight(val junctionID: String, val roadId: Roads, val lights: Lights) extends Actor {
  import TrafficLight._

  private val trafficLightHistory = Vector[Light](Light.RED)
  private val LaneCounters = createLaneCounters()
  implicit val timeout: Timeout = Timeout(5 seconds)
  var log: LoggingAdapter = Logging(context.system, this)

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy(loggingEnabled = false) {
    case _: Exception                                => Escalate
  }

  def receive: Receive = onMessage(trafficLightHistory)

  private def onMessage(historyData: Vector[Light]): Receive = {
    case UpdateActiveLight(newLight) =>
      if(historyData.size == 10){
        context.become(onMessage(newLight +: historyData.dropRight(0)))
      } else {
        context.become(onMessage(newLight +: historyData))
      }
      pl.edu.pw.elka.Main.database.setTrafficLight(junctionID, roadId, lights, newLight)

    case GetTrafficLightData =>
      var counts = Map.empty[Lanes, Long]
      for(counter <- LaneCounters){
        try {
          val count = Await.result(counter ? CountCarsOnLane, 5 seconds).asInstanceOf[CarNumberResponse]
          counts = counts + (count.lane -> count.cars)
        }
        catch {
          case e:Throwable =>
            log.error("Error occurred during getting data from lane counter")
            throw e
        }
      }

      if(counts.isEmpty) {
        self ! ErrorAlert
      }

      val state =  new TrafficLightState(
        self,
        roadId,
        historyData,
        counts
      )
      sender() ! TrafficLightDataResponse(state)

    case Stop =>
      context.stop(self)

    case ErrorAlert =>
      throw new TrafficLightEmergencyAlertException("error")

    case _ =>
      throw new RuntimeException("traffic light system error occurred")
  }

  def createLaneCounters(): Vector[ActorRef]= {
    var counters = Vector.empty[ActorRef]

    if (lights.state.equals(Lights.L.state)){
      counters = counters :+
        context.actorOf(TrafficLight.props(junctionID, roadId, Lanes.L))
    } else {
      counters = counters :+
        context.actorOf(TrafficLight.props(junctionID, roadId, Lanes.P1)) :+
        context.actorOf(TrafficLight.props(junctionID, roadId, Lanes.P2))
    }

    counters
  }
}
