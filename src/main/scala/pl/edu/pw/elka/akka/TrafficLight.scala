package pl.edu.pw.elka.akka

import akka.actor.{Actor, ActorRef, Props}
import pl.edu.pw.elka.enums.{Lanes, Light, Lights, Roads}
import pl.edu.pw.elka.akka.Manager.{CountCarsOnLanes, LightStatusResponse, TrafficLightHistoryResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.immutable.{Map, Vector}
import scala.concurrent.{Await, Future}
import akka.pattern.{ask, pipe}
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

class TrafficLight(val junctionID: String, val roadId: Roads, val lights: Lights) extends Actor {
  import TrafficLight._

  private val TrafficLightState = Light.RED
  private val TrafficLightHistory = Vector.empty
  private val LaneCounters = createLaneCounters()
  private val carNumbers = createCarNumbers()
  implicit val timeout: Timeout = Timeout(5 seconds)

  def receive: Receive = onMessage(TrafficLightState, TrafficLightHistory, carNumbers)

  private def onMessage(activeLight: Light, historyData: Vector[Light], numbers: Map[Lanes, Long]): Receive = {
    case UpdateActiveLight(newLight) =>
      context.become(onMessage(newLight, newLight +: historyData, numbers))
      pl.edu.pw.elka.Main.database.setTrafficLight(junctionID, roadId, lights, activeLight)

    case CurrentLight =>
      sender() ! LightStatusResponse(self, activeLight)

    case HistoryData =>
      sender() ! TrafficLightHistoryResponse(self, historyData)

    case CountCarsOnLanes =>
      var counts = Map.empty[Lanes, Long]
      for(counter <- LaneCounters){
        val count = Await.result(counter ? CountCarsOnLane, 5 seconds).asInstanceOf[CarNumberResponse]
        counts = counts + (count.lane -> count.cars)
      }
      context.become(onMessage(activeLight, historyData, counts))

    case Stop =>
      context.stop(self)

    //    case _ =>
    //      throw Exception
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

  private def createCarNumbers(): Map[Lanes, Long] = {
    var numbers = Map.empty[Lanes, Long]

    if (lights.state.equals(Lights.L.state)){
      numbers = numbers + (Lanes.L -> 0)
    } else {
      numbers = numbers +
        (Lanes.P1 -> 0, Lanes.P2 -> 0)
    }

    numbers
  }
}
