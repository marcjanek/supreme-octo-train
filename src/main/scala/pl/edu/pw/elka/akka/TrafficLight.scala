package pl.edu.pw.elka.akka

import akka.actor.SupervisorStrategy.Escalate
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props}
import akka.event.{Logging, LoggingAdapter}
import pl.edu.pw.elka.enums.{Lanes, Light, Lights, Roads}
import pl.edu.pw.elka.akka.Manager.{ComputeNewState, CountCarsOnLanesResponse, LightStatusResponse, TrafficLightHistoryResponse}

import scala.collection.immutable.{Map, Vector}
import scala.concurrent.Await
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object TrafficLight {
  case class UpdateActiveLight(newLight: Light)
  case object CurrentLight
  case object Stop
  case object ErrorAlert
  case object HistoryData
  case object CountCarsOnLane
  case class CarNumberResponse(cars: Long, lane: Lanes)

  def props(junctionID: String, roads: Roads, lane: Lanes): Props = Props(new LaneCounter(junctionID, roads, lane))
}

class TrafficLightEmergencyAlertException(private val message: String = "",
                                 private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

class TrafficLight(val junctionID: String, val roadId: Roads, val lights: Lights) extends Actor {
  import TrafficLight._

  private val trafficLightState = Light.RED
  private val trafficLightHistory = Vector[Light](Light.RED)
  private val LaneCounters = createLaneCounters()
  implicit val timeout: Timeout = Timeout(5 seconds)
  var log: LoggingAdapter = Logging(context.system, this)

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
    case _: Exception                                => Escalate
  };

  def receive: Receive = onMessage(trafficLightState, trafficLightHistory)

  private def onMessage(activeLight: Light, historyData: Vector[Light]): Receive = {
    case UpdateActiveLight(newLight) =>
      if(historyData.size == 10){
        context.become(onMessage(newLight, newLight +: historyData.dropRight(0)))
      } else {
        context.become(onMessage(newLight, newLight +: historyData))
      }
      pl.edu.pw.elka.Main.database.setTrafficLight(junctionID, roadId, lights, newLight)

    case CurrentLight =>
      sender() ! LightStatusResponse(self, activeLight)

    case HistoryData =>
      sender() ! TrafficLightHistoryResponse(self, historyData)

    case ComputeNewState =>
      var counts = Map.empty[Lanes, Long]
      for(counter <- LaneCounters){
        try {
          val count = Await.result(counter ? CountCarsOnLane, 5 seconds).asInstanceOf[CarNumberResponse]
          counts = counts + (count.lane -> count.cars)
        }
        catch {
          case e:Throwable =>
            log.error("Error occurred during getting data from lane counter")
            self ! ErrorAlert
        }
      }
      val state =  new TrafficLightState(
        self,
        roadId,
        historyData,
        counts
      )
      sender() ! CountCarsOnLanesResponse(state)
    case Stop =>
      context.stop(self)
    case ErrorAlert =>
      throw new TrafficLightEmergencyAlertException("error")
    case _ =>
      throw new RuntimeException("traffic light system error occurred")
  }

  private def createLaneCounters(): Vector[ActorRef]= {
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

  /*
  Get the healthy status of the traffic light
   */
  private def Healthy(): Boolean = {
    val r = new scala.util.Random()
    if( r.nextInt(100) < 98) {
      true;
    } else {
      false;
    }
  }
}
