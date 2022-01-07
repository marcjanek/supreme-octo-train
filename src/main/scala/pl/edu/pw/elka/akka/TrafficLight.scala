package pl.edu.pw.elka.akka

import akka.actor.SupervisorStrategy.{Escalate, Restart}
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

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 1, withinTimeRange = 2 seconds) {
    case _: TrafficLightEmergencyAlertException                                =>
      log.info("dupa2")
      Restart
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
      if(!Healthy()){
        throw new TrafficLightEmergencyAlertException("error")
      }
      var counts = Map.empty[Lanes, Long]
      for(counter <- LaneCounters){
        val count = Await.result(counter ? CountCarsOnLane, 5 seconds).asInstanceOf[CarNumberResponse]
        counts = counts + (count.lane -> count.cars)
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
    case _ =>
      throw new RuntimeException("")
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
    val r = new scala.util.Random
    if(r.nextInt(100) > 70) {
      log.info("dupa1")
      true;
    } else {
      false;
    }
  }
}
