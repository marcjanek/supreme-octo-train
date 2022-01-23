package pl.edu.pw.elka.akka

import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props}
import akka.actor.SupervisorStrategy._
import akka.event.{Logging, LoggingAdapter}
import akka.pattern.{AskTimeoutException, ask}
import org.apache.log4j.BasicConfigurator
import pl.edu.pw.elka.akka.TrafficLight.{GetTrafficLightData, UpdateActiveLight}
import pl.edu.pw.elka.enums.{JunctionType, Lanes, Light, Lights, Roads}

import scala.collection.immutable.{Map, Vector}
import scala.concurrent.{Await, TimeoutException}
import scala.concurrent.duration.{Duration, DurationInt}
import scala.language.postfixOps
import akka.util.Timeout
import pl.edu.pw.elka.akka.Manager.ComputeNewState
import pl.edu.pw.elka.algorithm.Planner

object Manager {
  case object ComputeNewState
  case class TrafficLightDataResponse(state: TrafficLightState)
  case object ErrorAlert

  def props(junctionID: String, roads: Roads, lights: Lights): Props = Props(new TrafficLight(junctionID, roads, lights))
}

class ManagerSystemErrorAlertException(private val message: String = "",
                                       private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

class Manager(val junctionType: JunctionType, val junctionID: String) extends Actor {
  import Manager._

  private val currentState = createFirstState()
  implicit val timeout: Timeout = Timeout(5 seconds)
  var log: LoggingAdapter = Logging(context.system, this)

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy(
                                          maxNrOfRetries = 3,
                                          withinTimeRange = 5 seconds,
                                          loggingEnabled = false) {
    case _: LaneCounterEmergencyAlertException       =>
      log.info("Handled lane counter sensor error...")
      Restart
    case _: TrafficLightEmergencyAlertException      =>
      log.info("Handled traffic light error... ")
      Restart
    case _: ManagerSystemErrorAlertException       =>
      log.info("Handled manager system error...")
      Restart
    case _: RuntimeException                         => Restart
    case _: TimeoutException                         => Restart
    case _: Exception                                => Escalate
  }

  def receive: Receive = onMessage(currentState)

  private def onMessage(state: Map[ActorRef, Light]): Receive = {
    case ComputeNewState =>
      var data = Vector.empty[TrafficLightState]
      for((child, _) <- state) {
        try {
          val tl = Await.result(child ? GetTrafficLightData, 5 seconds).asInstanceOf[TrafficLightDataResponse]
          data = tl.state +: data
        }
        catch {
          case e:Throwable =>
            log.error("Error occurred during calculating new state")
            self ! ErrorAlert
        }
      }
      val newState = new Planner(data).plan
      for ((trafficLight, newLight) <- newState) {
        trafficLight ! UpdateActiveLight(newLight)
      }
      context.become(onMessage(newState))

    case ErrorAlert =>
      throw new ManagerSystemErrorAlertException("error")

    case _ =>
      throw new RuntimeException("manager system error occurred")
  }

  def createFirstState(): Map[ActorRef, Light] = {
    var firstState = Map.empty[ActorRef, Light]

    for (light <- Lights.values()) {
      if (junctionType.state.equals(JunctionType.X.state)) {
        for (road <- Roads.values()) {
          val childProps = Manager.props(junctionID, road, light)
          val child = context.actorOf(childProps)
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