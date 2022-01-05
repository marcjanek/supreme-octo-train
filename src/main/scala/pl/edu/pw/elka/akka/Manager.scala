package pl.edu.pw.elka.akka

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import org.apache.log4j.BasicConfigurator
import pl.edu.pw.elka.akka.Manager.{AskForLightStatus, GetTrafficLightHistory, NewLight}
import pl.edu.pw.elka.akka.TrafficLight.{CurrentLight, HistoryData}
import pl.edu.pw.elka.enums.{JunctionType, Lanes, Light, Lights, Roads}

import scala.collection.immutable.{Map, Vector}

object Manager {
  case class NewLight(lane: Lanes, road: Roads)
  case class GetTrafficLightHistory()
  case class AskForLightStatus()
  case class LightStatusResponse(actorRef: ActorRef, light: Light)
  case class TrafficLightHistoryResponse(actorRef: ActorRef, History: Vector[Light])
  case class NewLightState(state: Map[ActorRef, Light])
}

class Manager(val junctionType: JunctionType) extends Actor {
  import Manager._
  private val currentState = Map.empty[ActorRef, Light]
  var log: LoggingAdapter = Logging(context.system, this)

  def receive: Receive = onMessage(currentState)

  private def onMessage(state: Map[ActorRef, Light]): Receive = {
    case NewLight(lane: Lanes, road: Roads) =>
      val child = context.actorOf(Props(new TrafficLight(lane, road)))
      context.become(onMessage(state = state + (child -> Light.RED)))

    case GetTrafficLightHistory =>
      for((child, _) <- state) {
        child ! HistoryData
      }

    case AskForLightStatus =>
      for((child, _) <- state) {
        child ! CurrentLight
      }

    case LightStatusResponse(actorRef, light) =>
      if (state(actorRef) != light) {
        context.become(onMessage(state = state + (actorRef -> light)))
      }
      log.info(state.toString())

    case TrafficLightHistoryResponse(actorRef, historyData) =>
      log.info(actorRef.toString() + historyData.toString)

    //  case _ =>
//      throw Exception
  }
}

class TrafficLightState (
                           val actorRef: ActorRef, //pointer
                           val currentLight: Light, //RED/GREEN
                           val road: Roads, //A/B/C/D
                           val historyData: Vector[Light], //[RED, GREEN, RED, GREEN...]
                           val counters: Map[Lights, Int] //[P1 -> 10, P2 -> 30]/[L->15]
                         ) {
}

//map{actorRef->State}

object Main {
  def main(Args: Array[String]): Unit = {
    BasicConfigurator.configure()
    val system = ActorSystem("test")
    val testManager = system.actorOf(Props(new Manager(JunctionType.X)), "Manager")

    testManager ! NewLight(Lanes.P1, Roads.A)
    testManager ! NewLight(Lanes.L, Roads.B)
    testManager ! NewLight(Lanes.P2, Roads.C)
    testManager ! AskForLightStatus
  }
}
