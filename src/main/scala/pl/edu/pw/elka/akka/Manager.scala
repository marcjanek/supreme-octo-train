package pl.edu.pw.elka.akka

import akka.actor.{Actor, ActorRef, ActorSystem, OneForOneStrategy, Props}
import akka.actor.SupervisorStrategy._
import akka.event.{Logging, LoggingAdapter}
import akka.pattern.ask
import org.apache.log4j.BasicConfigurator
import pl.edu.pw.elka.akka.TrafficLight.{GetTrafficLightData, UpdateActiveLight}
import pl.edu.pw.elka.enums.{JunctionType, Lanes, Light, Lights, Roads}

import scala.collection.immutable.{Map, Vector}
import scala.concurrent.{Await, TimeoutException}
import scala.concurrent.duration.{Duration, DurationInt}
import scala.language.postfixOps
import akka.util.Timeout
import pl.edu.pw.elka.akka.Manager.{AddNeighbour, ComputeNewState}
import pl.edu.pw.elka.algorithm.Planner

object Manager {
  case object ComputeNewState
  case class TrafficLightDataResponse(state: TrafficLightState)
  case object ErrorAlert
  case class AddNeighbour(neighbour: ActorRef, roads: Roads)
  case object GetNeighbourData
  case class GetNeighbourDataResponse(NeighbourData: Vector[TrafficLightState])

  def props(junctionID: String, roads: Roads, lights: Lights): Props = Props(new TrafficLight(junctionID, roads, lights))
}

class ManagerSystemErrorAlertException(private val message: String = "",
                                       private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

class Manager(val junctionType: JunctionType, val junctionID: String) extends Actor {
  import Manager._

  private val currentState = createFirstState()
  private val currentData = Vector.empty[TrafficLightState]
  private val neighbours = Map.empty[Roads, ActorRef]
  private val neighbourStates = Map.empty[Roads, Vector[TrafficLightState]]
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

  def receive: Receive = onMessage(currentState, currentData, neighbours, neighbourStates)

  private def onMessage(state: Map[ActorRef, Light], data: Vector[TrafficLightState], neighbours: Map[Roads, ActorRef], neighboursStates: Map[Roads, Vector[TrafficLightState]]): Receive = {
    case ComputeNewState =>
      var newData = Vector.empty[TrafficLightState]
      for((child, _) <- state) {
        try {
          val tl = Await.result(child ? GetTrafficLightData, 5 seconds).asInstanceOf[TrafficLightDataResponse]
          newData = tl.state +: newData
        }
        catch {
          case e: Throwable =>
            log.error("Error occurred during calculating new state")
            self ! ErrorAlert
        }
      }

      val states = getNeighboursStates(neighbours)

      val newState = new Planner(newData, states).plan
      for ((trafficLight, newLight) <- newState) {
        trafficLight ! UpdateActiveLight(newLight)
      }
      context.become(onMessage(newState, newData, neighbours, states))

    case AddNeighbour(neighbour: ActorRef, road: Roads) =>
      context.become(onMessage(state, data, neighbours + (road -> neighbour), neighboursStates))

    case GetNeighbourData =>
      sender() ! GetNeighbourDataResponse(data)

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

  def createFirstData(): Vector[TrafficLightState] = {
    var firstState = Vector.empty[TrafficLightState]

    firstState
  }

  private def getNeighboursStates(neighbours:  Map[Roads, ActorRef]): Map[Roads, Vector[TrafficLightState]] = {
    var neighboursData = Map.empty[Roads, Vector[TrafficLightState]]
    if (neighbours == null) {

    }
    for((road, neighbour) <- neighbours) {
      val nd = Await.result(neighbour ? GetNeighbourData, 5 seconds).asInstanceOf[GetNeighbourDataResponse]
      neighboursData = neighboursData + (road -> nd.NeighbourData)
    }
    neighboursData
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
    val testManager1 = system.actorOf(Props(new Manager(JunctionType.X, "1")), "1")
    val testManager2 = system.actorOf(Props(new Manager(JunctionType.X, "2")), "2")
    val testManager3 = system.actorOf(Props(new Manager(JunctionType.X, "3")), "3")
    val testManager4 = system.actorOf(Props(new Manager(JunctionType.X, "4")), "4")
    testManager1 ! AddNeighbour(testManager2, Roads.B)
    testManager1 ! AddNeighbour(testManager3, Roads.C)
    testManager2 ! AddNeighbour(testManager1, Roads.D)
    testManager2 ! AddNeighbour(testManager4, Roads.C)
    testManager3 ! AddNeighbour(testManager1, Roads.A)
    testManager3 ! AddNeighbour(testManager4, Roads.B)
    testManager4 ! AddNeighbour(testManager3, Roads.D)
    testManager4 ! AddNeighbour(testManager2, Roads.A)
    import system.dispatcher

    val cancellable1 =
      system.scheduler.scheduleWithFixedDelay(Duration.Zero, 5.seconds, testManager1, ComputeNewState)
    val cancellable2 =
      system.scheduler.scheduleWithFixedDelay(Duration.Zero, 5.seconds, testManager2, ComputeNewState)
    val cancellable3 =
      system.scheduler.scheduleWithFixedDelay(Duration.Zero, 5.seconds, testManager3, ComputeNewState)
    val cancellable4 =
      system.scheduler.scheduleWithFixedDelay(Duration.Zero, 5.seconds, testManager4, ComputeNewState)
    //This cancels further Ticks to be sent
//    cancellable1.cancel()
//    cancellable2.cancel()
//    cancellable3.cancel()
//    cancellable4.cancel()
  }
}
