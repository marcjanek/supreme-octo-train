package actors

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestActor, TestActorRef, TestKit, TestProbe}
import org.scalatest.{BeforeAndAfterAll, stats}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.specs2.execute.StandardResults.success
import pl.edu.pw.elka.akka.{Manager, ManagerRegistry, ManagerSystemErrorAlertException, TrafficLightEmergencyAlertException, TrafficLightState}
import pl.edu.pw.elka.akka.Manager.{ComputeNewState, TrafficLightDataResponse, neighboursStates, ErrorAlert => ManagerError}
import pl.edu.pw.elka.akka.ManagerRegistry.{getNeighbours, runManagers}
import pl.edu.pw.elka.akka.TrafficLight.{GetTrafficLightData, UpdateActiveLight, ErrorAlert => TrafficError}
import pl.edu.pw.elka.enums._

import scala.collection.immutable.{Map, Vector}
import scala.concurrent.duration.DurationInt

class ManagerTests extends TestKit(ActorSystem("TestKitUsageSpec"))
  with DefaultTimeout
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    shutdown()
  }

  "Manager" should {
    "Error alert throw ManagerSystemErrorAlertException" in {
      val managerRef = TestActorRef(Props(new Manager(JunctionType.X, "junctionId")))
      intercept[ManagerSystemErrorAlertException] {
        managerRef.receive(ManagerError)
      }
    }

    "ComputeNewState call GetTrafficLightData with max timeout 5 sec" in {
      val probe = TestProbe()
      val managerRef = TestActorRef(new Manager(JunctionType.X, "junctionId") {
        override def createFirstState(): Map[ActorRef, Light] = {
          val firstState = Map[ActorRef, Light] ((probe.ref, Light.RED))
          firstState
        }
      })
      managerRef ! ComputeNewState
      probe.expectMsg(5 seconds, GetTrafficLightData)
      success
    }

    "ComputeNewState error during computing" in {
      val probe = TestProbe()

      val managerRef = TestActorRef(new Manager(JunctionType.X, "junctionId") {
        override def createFirstState(): Map[ActorRef, Light] = {
          val firstState = Map[ActorRef, Light] ((probe.ref, Light.RED))
          firstState
        }
      })

      probe.setAutoPilot((sender: ActorRef, msg: Any) => msg match {
        case GetTrafficLightData => throw new TrafficLightEmergencyAlertException()
        case _ => TestActor.NoAutoPilot
      })

      intercept[ManagerSystemErrorAlertException] {
        managerRef.receive(ManagerError)
      }
    }

    "ComputeNewState call proper data" in {
      val trafficProbe = TestProbe()

      val state =  new TrafficLightState(
        trafficProbe.ref,
        Roads.A,
        Vector[Light](Light.RED),
        Map[Lanes, Long]((Lanes.L, 10))
      )

      val responseNeighboursStates = new neighboursStates(
        Map[Roads, Vector[TrafficLightState]]
          ((state.road, Vector[TrafficLightState](state)))
      )

      val responseTrafficStateData = TrafficLightDataResponse(state)

      val managerRef = TestActorRef(new Manager(JunctionType.X, "junctionId") {
        override def createFirstState(): Map[ActorRef, Light] = {
          val firstState = Map[ActorRef, Light] ((trafficProbe.ref, Light.RED))
          firstState
        }

        override def getNeighboursDataAsync(): neighboursStates = {
          responseNeighboursStates
        }
      })

      trafficProbe.setAutoPilot((sender: ActorRef, msg: Any) => msg match {
        case GetTrafficLightData => sender ! responseTrafficStateData; TestActor.KeepRunning;
        case _ => TestActor.NoAutoPilot
      })

      managerRef ! ComputeNewState
      trafficProbe.expectMsg(GetTrafficLightData)
      trafficProbe.expectMsg(UpdateActiveLight(Light.GREEN))
    }
  }
}
