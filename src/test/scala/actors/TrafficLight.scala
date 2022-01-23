package actors

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.testkit.{DefaultTimeout, ImplicitSender, TestActor, TestActorRef, TestKit, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime
import org.scalatest.wordspec.AnyWordSpecLike
import org.specs2.execute.StandardResults.success
import pl.edu.pw.elka.akka.LaneCounter.CountCarsOnLane
import pl.edu.pw.elka.akka.Manager.TrafficLightDataResponse
import pl.edu.pw.elka.akka.TrafficLight.{CarNumberResponse, ErrorAlert, GetTrafficLightData, Stop}
import pl.edu.pw.elka.akka.{TrafficLight, TrafficLightEmergencyAlertException}
import pl.edu.pw.elka.enums.{Lanes, Light, Lights, Roads}

import scala.collection.immutable.Vector
import scala.concurrent.Await

class TrafficLightTests extends TestKit(ActorSystem("TestKitUsageSpec"))
  with DefaultTimeout
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    shutdown()
  }

  "Traffic Light" should {
    "Error alert throw TrafficLightEmergencyAlertException" in {
      val trafficLightRef = TestActorRef(Props(new TrafficLight("junctionId", Roads.A, Lights.L)))
      intercept[TrafficLightEmergencyAlertException] {
        trafficLightRef.receive(ErrorAlert)
      }
    }

    "Stop no messages" in {
      val trafficLightRef = TestActorRef(Props(new TrafficLight("junctionId", Roads.A, Lights.L)))
      trafficLightRef ! Stop
      expectNoMessage()
    }

    "GetTrafficLightData call count cars on lane with max timeout 5 sec" in {
      val probe = TestProbe()
      val trafficLightRef = TestActorRef(new TrafficLight("junctionId", Roads.A, Lights.L) {
        override def createLaneCounters(): Vector[ActorRef] = {
          var counters = Vector.empty[ActorRef]
          counters = counters :+  probe.ref
          counters
        }
      })
      trafficLightRef ! GetTrafficLightData
      probe.expectMsg(5 seconds, CountCarsOnLane)
      success
    }

    "GetTrafficLightData return valid TrafficLightState" in {
      val probe = TestProbe()
      val responseCarsOnLane = CarNumberResponse(10, Lanes.L)
      val trafficLightRef = TestActorRef(new TrafficLight("junctionId", Roads.A, Lights.L) {
        override def createLaneCounters(): Vector[ActorRef] = {
          var counters = Vector.empty[ActorRef]
          counters = counters :+  probe.ref
          counters
        }
      })

      val history:Vector[Light] = Vector[Light](Light.RED)
      val counts:Map[Lanes, Long] = Map[Lanes, Long]((Lanes.L, 10))

      probe.setAutoPilot(new TestActor.AutoPilot {
        def run(sender: ActorRef, msg: Any): TestActor.AutoPilot =
          msg match {
            case CountCarsOnLane => sender ! responseCarsOnLane; TestActor.KeepRunning
            case _ => TestActor.NoAutoPilot
          }
      })

      val result = Await.result(trafficLightRef ? GetTrafficLightData, 5 seconds)
        .asInstanceOf[TrafficLightDataResponse]

      assert(result.state.counters == counts)
      assert(result.state.historyData == history)
      assert(result.state.road == Roads.A)
    }
  }
}
