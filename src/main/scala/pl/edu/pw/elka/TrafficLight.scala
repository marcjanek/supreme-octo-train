package pl.edu.pw.elka

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import org.apache.log4j.BasicConfigurator

import collection.immutable.Vector
import pl.edu.pw.elka.TrafficLight.{CurrentLight, HistoryData, Stop, UpdateActiveLight}

object TrafficLight {
  case class UpdateActiveLight(newLight: Int)
  case object CurrentLight
  case object Stop
  case object HistoryData
}

class TrafficLight extends Actor {
  var log: LoggingAdapter = Logging(context.system, this)
  private val TrafficLightState = 0
  private val TrafficLightHistory = Vector.empty

  import TrafficLight._

  def receive: Receive = onMessage(TrafficLightState, TrafficLightHistory)

  private def onMessage(activeLight: Int, historyData: Vector[Int]): Receive = {
    case UpdateActiveLight(newLight: Int) =>
      context.become(onMessage(newLight, newLight +: historyData))
    case CurrentLight =>
      sender() ! activeLight
//      log.info(activeLight.toString)
    case HistoryData =>
      sender() ! historyData
//      log.info(historyData.toString())
    case Stop =>
      context.stop(self)
//    case _ =>
//      throw Exception
  }
}

//object Main {
//  def main(Args: Array[String]): Unit = {
//    BasicConfigurator.configure()
//    val system = ActorSystem("test")
//    val testActor = system.actorOf(Props[TrafficLight](), "test")
//    testActor ! UpdateActiveLight(8)
//    testActor ! HistoryData
//    testActor ! CurrentLight
//    testActor ! Stop
//  }
//}
