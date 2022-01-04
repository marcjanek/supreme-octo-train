package pl.edu.pw.elka.akka

import akka.actor.Actor
import pl.edu.pw.elka.enums.Light
import pl.edu.pw.elka.akka.Manager.LightStatusResponse

import scala.collection.immutable.Vector

object TrafficLight {
  case class UpdateActiveLight(newLight: Light)
  case object CurrentLight
  case object Stop
  case object HistoryData
}

object TrafficLightLaneEnum extends Enumeration {
  type TrafficLightId = Value

  val P, L = Value
}

object TrafficLaneLaneEnum extends Enumeration {
  type RoadId = Value

  val A, B, C, D = Value
}

class TrafficLight(val laneId: TrafficLightLaneEnum.TrafficLightId, val roadId: TrafficLaneLaneEnum.RoadId) extends Actor {
  import TrafficLight._

  private val TrafficLightState = Light.RED
  private val TrafficLightHistory = Vector.empty

  def receive: Receive = onMessage(TrafficLightState, TrafficLightHistory)

  private def onMessage(activeLight: Light, historyData: Vector[Light]): Receive = {
    case UpdateActiveLight(newLight) =>
      context.become(onMessage(newLight, newLight +: historyData))
    case CurrentLight =>
      sender() ! LightStatusResponse(self, activeLight)
    case HistoryData =>
      sender() ! LightStatusResponse(self, activeLight)
    case Stop =>
      context.stop(self)
    //    case _ =>
    //      throw Exception
  }
}