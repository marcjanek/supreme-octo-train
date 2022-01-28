package pl.edu.pw.elka.akka

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import org.apache.log4j.BasicConfigurator
import pl.edu.pw.elka.akka.Manager.{ComputeNewState, neighboursStates}
import pl.edu.pw.elka.akka.ManagerRegistry.{NewManager, addNeighbour, getNeighbours, runManagers, updateState}
import pl.edu.pw.elka.enums.{JunctionType, Roads}

import scala.concurrent.duration.{Duration, DurationInt}

object ManagerRegistry {
  case class NewManager(junctionType: JunctionType, junctionID: String)
  case class addNeighbour(junctionID: String, neighbours: Map[Roads,String])
  case class getNeighbours(ref: ActorRef)
  case class updateState(ref: ActorRef, state: Vector[TrafficLightState])
  case object runManagers
  def props(junctionType: JunctionType, junctionID: String): Props = Props(new Manager(junctionType, junctionID))
}

class ManagerRegistry(Managers: Map[ActorRef, String]) extends Actor {
    private val managers = createManagers(Managers)
    private val neighbours = createNeighbours(Managers)
    private val junctions = createJunctions(Managers)

    def receive: Receive = onMessage(managers, neighbours, junctions)

    private def onMessage(states: Map[ActorRef, Vector[TrafficLightState]], neighbours: Map[ActorRef, Map[ActorRef, Roads]], junctions: Map[String, ActorRef]) : Receive = {
      case NewManager(junctionType: JunctionType, junctionID: String) =>
        val manager = context.actorOf(ManagerRegistry.props(junctionType, junctionID), junctionID)

        val newStates = states + (manager -> Vector.empty[TrafficLightState])
        val newNeighbours = neighbours + (manager -> Map.empty[ActorRef, Roads])
        val newJunctions = junctions + (junctionID -> manager)

        context.become(onMessage(newStates, newNeighbours, newJunctions))

      case addNeighbour(junctionID: String, newNeighbours: Map[Roads, String]) =>
        var toAdd = Map.empty[ActorRef, Roads]
        for ((r,n) <- newNeighbours) {
          val junction = junctions(n)
          toAdd = toAdd + (junction -> r)
        }
        val manager= junctions(junctionID)

        context.become(onMessage(states, neighbours + (manager -> toAdd), junctions))

      case getNeighbours(ref: ActorRef) =>
        var toReturn = Map.empty[Roads, Vector[TrafficLightState]]
        val ns = neighbours(ref)
        for ((n, r) <- ns) {
          val neighbourState = states(n)
          toReturn = toReturn + (r -> neighbourState)
        }
        sender() ! neighboursStates(toReturn)

      case updateState(ref: ActorRef, state: Vector[TrafficLightState]) =>
        context.become(onMessage(states + (ref -> state), neighbours, junctions))

      case runManagers =>
        for ((m, _) <- states) {
          m ! ComputeNewState
        }
    }

    def createManagers(managers: Map[ActorRef, String]): Map[ActorRef, Vector[TrafficLightState]]= {
      var ms = Map.empty[ActorRef, Vector[TrafficLightState]]
      for ((m, _) <- managers) {
        ms = ms + (m -> Vector.empty[TrafficLightState])
      }
      ms
    }

    def createNeighbours(managers: Map[ActorRef, String]): Map[ActorRef, Map[ActorRef, Roads]]= {
      var ns = Map.empty[ActorRef, Map[ActorRef, Roads]]
      for ((m, _) <- managers) {
        ns = ns + (m -> Map.empty[ActorRef, Roads])
      }
      ns
    }

    def createJunctions(managers: Map[ActorRef, String]): Map[String, ActorRef]= {
      var js = Map.empty[String, ActorRef]
      for ((m, j) <- managers) {
        js = js + (j -> m)
      }
      js
    }
}

object Main {
  def main(): Unit = {
    BasicConfigurator.configure()
    val system = ActorSystem("test")
    val managerRegistry = system.actorOf(Props(new ManagerRegistry(Map.empty[ActorRef, String])), "registry")

    managerRegistry ! NewManager(JunctionType.X, "1")
    managerRegistry ! NewManager(JunctionType.X, "2")
    managerRegistry ! NewManager(JunctionType.X, "3")
    managerRegistry ! NewManager(JunctionType.X, "4")

    managerRegistry ! addNeighbour("1", Map(Roads.B -> "2", Roads.C -> "3"))
    managerRegistry ! addNeighbour("2", Map(Roads.D -> "1", Roads.C-> "4"))
    managerRegistry ! addNeighbour("3", Map(Roads.A -> "1", Roads.B-> "4"))
    managerRegistry ! addNeighbour("4", Map(Roads.A -> "2", Roads.D-> "3"))

    import system.dispatcher

    val cancellable =
      system.scheduler.scheduleWithFixedDelay(Duration.Zero, 5.seconds, managerRegistry, runManagers)

    //cancellable.cancel()
  }
}