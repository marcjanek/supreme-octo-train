package pl.edu.pw.elka.akka

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import org.apache.log4j.BasicConfigurator
import pl.edu.pw.elka.akka.Manager.{GetTrafficLightHistory, NewLight}
import pl.edu.pw.elka.akka.TrafficLight.{CurrentLight, HistoryData}
import pl.edu.pw.elka.enums.Light

import scala.collection.immutable.Vector

object Manager {
  case class NewLight()
  case class GetTrafficLightHistory()
  case class AskForLightStatus()
  case class LightStatusResponse(actorRef: ActorRef, light: Light)
  case class TrafficLightHistoryResponse(actorRef: ActorRef, light: Light)
}

class Manager extends Actor {
  import Manager._
  private val trafficLights =  Vector.empty[ActorRef]
  var log: LoggingAdapter = Logging(context.system, this)

  def receive: Receive = onMessage(trafficLights)

  private def onMessage(lights: Vector[ActorRef]): Receive = {
    case NewLight =>
      val child = context.actorOf(Props(new TrafficLight(TrafficLightLaneEnum.P, TrafficLaneLaneEnum.A)))
      context.become(onMessage(lights :+ child))
    case GetTrafficLightHistory =>
      for(child <- lights) {
        child ! HistoryData
      }
    case AskForLightStatus =>
      for(child <- lights) {
        child ! CurrentLight
      }
    case LightStatusResponse(actorRef, light) =>
      log.info(actorRef.toString() + light.toString)
    case TrafficLightHistoryResponse(actorRef, light) =>
      log.info(actorRef.toString() + light.toString)
//    case _ =>
//      throw Exception
  }
}

object Main {
  def main(): Unit = {
    BasicConfigurator.configure()
    val system = ActorSystem("test")
    val testManager = system.actorOf(Props[Manager](), "Manager")

    testManager ! NewLight
    testManager ! NewLight
    testManager ! GetTrafficLightHistory
  }
}
