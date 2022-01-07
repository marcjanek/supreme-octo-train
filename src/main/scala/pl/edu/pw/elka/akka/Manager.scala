package pl.edu.pw.elka.akka

import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props}
import akka.actor.SupervisorStrategy._
import akka.event.{Logging, LoggingAdapter}
import akka.pattern.{BackoffOpts, BackoffSupervisor, ask}
import org.apache.log4j.BasicConfigurator
import pl.edu.pw.elka.akka.TrafficLight.{CurrentLight, HistoryData, UpdateActiveLight}
import pl.edu.pw.elka.enums.{JunctionType, Lanes, Light, Lights, Roads}

import scala.collection.immutable.{Map, Vector}
import scala.concurrent.{Await, TimeoutException}
import scala.concurrent.duration.{Duration, DurationInt}
import scala.language.postfixOps
import akka.util.Timeout
import pl.edu.pw.elka.akka.Manager.ComputeNewState
import pl.edu.pw.elka.algorithm.Planner

object Manager {
  case class GetTrafficLightHistory()
  case class AskForLightStatus()
  case class LightStatusResponse(actorRef: ActorRef, light: Light)
  case class TrafficLightHistoryResponse(actorRef: ActorRef, History: Vector[Light])
  case class NewLightState(state: Map[ActorRef, Light])
  case object ComputeNewState
  case class CountCarsOnLanesResponse(state: TrafficLightState)
  case object StartManager

  def props(junctionID: String, roads: Roads, lights: Lights): Props = Props(new TrafficLight(junctionID, roads, lights))
}

class Manager(val junctionType: JunctionType, val junctionID: String) extends Actor {
  import Manager._
  private val currentState = createFirstState()
  implicit val timeout: Timeout = Timeout(5 seconds)
  var log: LoggingAdapter = Logging(context.system, this)

  def receive: Receive = onMessage(currentState)

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
    case _: RuntimeException                         => Restart
    case _: TimeoutException                         => Restart
    case _: Exception                                => Escalate
  };

  private def onMessage(state: Map[ActorRef, Light]): Receive = {
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

    case ComputeNewState =>
      var data = Vector.empty[TrafficLightState]
      for((child, _) <- state) {
        val tl = Await.result(child ? ComputeNewState, 5 seconds).asInstanceOf[CountCarsOnLanesResponse]
        data = tl.state +: data
      }
      val newState = new Planner(data).plan
      for ((trafficLight, newLight) <- newState) {
        trafficLight ! UpdateActiveLight(newLight)
      }
      context.become(onMessage(newState))
    case _ =>
      throw new RuntimeException("")
  }

  private def createFirstState(): Map[ActorRef, Light] = {
    var firstState = Map.empty[ActorRef, Light]

    for (light <- Lights.values()) {
      if (junctionType.state.equals(JunctionType.X.state)) {
        for (road <- Roads.values()) {
          val child = context.actorOf(Manager.props(junctionID, road, light))

//          val backoffSupervisor = BackoffSupervisor.props(
//            BackoffOpts
//              .onFailure(
//                Manager.props(),
//                childName = "myEcho",
//                minBackoff = 3.seconds,
//                maxBackoff = 30.seconds,
//                randomFactor = 0.2 // adds 20% "noise" to vary the intervals slightly
//              )
//              .withAutoReset(10.seconds) // reset if the child does not throw any errors within 10 seconds
//              .withSupervisorStrategy(OneForOneStrategy() {
//                case _: MyException => SupervisorStrategy.Restart
//                case _              => SupervisorStrategy.Escalate
//              }))

          firstState = firstState + (child -> Light.RED)
        }
      } else {
        for (road <- List(Roads.A, Roads.B, Roads.C)){
          val child = context.actorOf(Manager.props(junctionID, road, light))
          firstState = firstState + (child -> Light.RED)
        }
      }
    }
    firstState
  }
}

class TrafficLightState (
                           val actorRef: ActorRef, //pointer
                           val road: Roads, //A/B/C/D
                           val historyData: Vector[Light], //[RED, GREEN, RED, GREEN...]
                           val counters: Map[Lanes, Long] //[P1 -> 10, P2 -> 30]/[L->15]
                         ) {
}

object Main {
  def main(): Unit = {
    BasicConfigurator.configure()
    val system = ActorSystem("test")
    val testManager = system.actorOf(Props(new Manager(JunctionType.X, "1")), "1")
    val testManager1 = system.actorOf(Props(new Manager(JunctionType.X, "2")), "2")
    import system.dispatcher

    val cancellable1 =
      system.scheduler.scheduleWithFixedDelay(Duration.Zero, 5.seconds, testManager, ComputeNewState)
    val cancellable2 =
      system.scheduler.scheduleWithFixedDelay(Duration.Zero, 5.seconds, testManager1, ComputeNewState)
    //This cancels further Ticks to be sent
    //cancellable1.cancel()
    //cancellable2.cancel()
  }
}