package actors

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestActor, TestActorRef, TestKit, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import org.specs2.execute.StandardResults.success
import pl.edu.pw.elka.akka.{Manager, ManagerRegistry, ManagerSystemErrorAlertException, TrafficLightEmergencyAlertException, TrafficLightState}
import pl.edu.pw.elka.akka.Manager.{ComputeNewState, TrafficLightDataResponse, ErrorAlert => ManagerError}
import pl.edu.pw.elka.akka.ManagerRegistry.runManagers
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

    "ComputeNewState valid data" in {
      val probe = TestProbe()
      val managerRef = TestActorRef(new Manager(JunctionType.X, "junctionId") {
        override def createFirstState(): Map[ActorRef, Light] = {
          val firstState = Map[ActorRef, Light] ((probe.ref, Light.RED))
          firstState
        }
      })

      val registryRef = TestActorRef(new ManagerRegistry(Map(managerRef -> "junctionId")))

      val state =  new TrafficLightState(
        probe.ref,
        Roads.A,
        Vector[Light](Light.RED),
        Map[Lanes, Long]((Lanes.L, 10))
      )
      val responseTrafficStateData = TrafficLightDataResponse(state)

      probe.setAutoPilot((sender: ActorRef, msg: Any) => msg match {
        case GetTrafficLightData => sender ! responseTrafficStateData; TestActor.KeepRunning
        case _ => TestActor.NoAutoPilot
      })

      registryRef ! runManagers
      probe.expectMsg(GetTrafficLightData)
      probe.expectMsg(UpdateActiveLight(Light.GREEN))
    }
  }
}
