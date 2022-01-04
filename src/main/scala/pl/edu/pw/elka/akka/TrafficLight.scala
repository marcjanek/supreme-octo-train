package pl.edu.pw.elka.akka

import akka.actor.Actor
import pl.edu.pw.elka.enums.{Light, Roads, Lanes}
import pl.edu.pw.elka.akka.Manager.{LightStatusResponse, TrafficLightHistoryResponse}

import scala.collection.immutable.Vector

object TrafficLight {
  case class UpdateActiveLight(newLight: Light)
  case object CurrentLight
  case object Stop
  case object HistoryData
}

class TrafficLight(val laneId: Lanes, val roadId: Roads) extends Actor {
  import TrafficLight._

  private val TrafficLightState = Light.RED
  private val TrafficLightHistory = Vector.empty

  def receive: Receive = onMessage(TrafficLightState, TrafficLightHistory)

  private def onMessage(activeLight: Light, historyData: Vector[Light]): Receive = {
    case UpdateActiveLight(newLight) =>
      context.become(onMessage(newLight, newLight +: historyData))
//      todo: addToDatabase
    case CurrentLight =>
      sender() ! LightStatusResponse(self, activeLight)

    case HistoryData =>
      sender() ! TrafficLightHistoryResponse(self, historyData)

    case Stop =>
      context.stop(self)

    //    case _ =>
    //      throw Exception
  }
}
