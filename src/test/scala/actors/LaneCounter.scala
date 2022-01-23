package actors

import akka.actor.{ActorSystem, Props}
import akka.testkit.{DefaultTimeout, ImplicitSender, TestActorRef, TestKit}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import pl.edu.pw.elka.akka.TrafficLight.CarNumberResponse
import pl.edu.pw.elka.akka.{LaneCounter, LaneCounterEmergencyAlertException}
import pl.edu.pw.elka.akka.LaneCounter.{CountCarsOnLane, ErrorAlert, Stop}
import pl.edu.pw.elka.enums.{Lanes, Roads}


class LaneCounterTests extends TestKit(ActorSystem("TestKitUsageSpec"))
  with DefaultTimeout
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    shutdown()
  }

  "Lane counter" should {
    "Returns initial cars count" in {
        val expectedResponse = CarNumberResponse(0, Lanes.L)
        val laneCounterRef = TestActorRef(Props(new LaneCounter("junctionId", Roads.A, Lanes.L)))
        laneCounterRef ! CountCarsOnLane
        expectMsg(expectedResponse)
    }

    "Error alert no messages respond" in {
      val laneCounterRef = TestActorRef(Props(new LaneCounter("junctionId", Roads.A, Lanes.L)))
      laneCounterRef ! ErrorAlert
      expectNoMessage()
    }

    "Error alert throw LaneCounterEmergencyAlertException" in {
      val laneCounterRef = TestActorRef(Props(new LaneCounter("junctionId", Roads.A, Lanes.L)))
      intercept[LaneCounterEmergencyAlertException] {
        laneCounterRef.receive(ErrorAlert)
      }
    }

    "Stop no messages respond" in {
      val laneCounterRef = TestActorRef(Props(new LaneCounter("junctionId", Roads.A, Lanes.L)))
      laneCounterRef ! Stop
      expectNoMessage()
    }
  }
}
