package pl.edu.pw.elka.akka

import akka.actor.Actor
import akka.event.{Logging, LoggingAdapter}
import pl.edu.pw.elka.enums.{Lanes, Light, Roads}

import scala.collection.immutable.Vector

object TrafficLight {
  case class UpdateActiveLight(newLight: Light)
  case object CurrentLight
  case object Stop
  case object HistoryData
}

class TrafficLight(val laneId:  Lanes , val roadId: Roads) extends Actor {
  var log: LoggingAdapter = Logging(context.system, this)
  private val TrafficLightState = Light.RED
  private val TrafficLightHistory = Vector.empty
  import TrafficLight._

  def receive: Receive = onMessage(TrafficLightState, TrafficLightHistory)

  private def onMessage(activeLight: Light, historyData: Vector[Light]): Receive = {
    case UpdateActiveLight(newLight) =>
      context.become(onMessage(newLight, newLight +: historyData))
    case CurrentLight =>
      //sender() ! activeLight
      log.info(activeLight.toString)
    case HistoryData =>
      //sender() ! historyData
      log.info(historyData.toString())
    case Stop =>
      context.stop(self)
//    case _ =>
//      throw Exception
  }
}
