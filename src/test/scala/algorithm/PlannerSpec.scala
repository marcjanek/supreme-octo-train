package algorithm

import akka.actor.{ActorRef, ActorSystem, Props}
import org.scalatest.funspec._
import pl.edu.pw.elka.akka.TrafficLightState
import pl.edu.pw.elka.algorithm.Planner
import pl.edu.pw.elka.enums._
import java.lang.System.identityHashCode

class PlannerSpec extends AnyFunSpec {
  implicit val system: ActorSystem = ActorSystem()

  describe("#plan") {
    describe("when given vector is empty") {
      it("returns empty crossroad vector") {
        val crossroad: Vector[TrafficLightState] = Vector[TrafficLightState]()
        val planner = new Planner(crossroad)

        val expected = Map[ActorRef, Light]()
        assert(planner.plan === expected)
      }
    }

    describe("when given vector describes 3-roads crossroad") {
      describe("when there are less than 10 traffic lights states in the history") {
        it("returns valid traffic lights configuration") {
          val numberOfRoads = 3
          val actors : Vector[ActorRef] = Vector.fill(2 * numberOfRoads)(system.actorOf(Props.empty))

          var crossroad: Vector[TrafficLightState] = Vector[TrafficLightState]()
          val historyData = Vector.fill(3)(Light.RED)
          val counters = Vector[Map[Lanes, Long]](Map(Lanes.P1 -> 3, Lanes.P2 -> 3), Map(Lanes.L -> 4))

          for(i <- 0 until numberOfRoads) {
            for(j <- 0 to 1) {
              val trafficLightState = new TrafficLightState(
                actors(2 * i + j),
                Roads.getByIndex(i),
                historyData,
                counters(j)
              )

              crossroad = trafficLightState +: crossroad
            }
          }

          val planner = new Planner(crossroad)
          val generatedPlan = planner.plan

          assert(generatedPlan.size === 6)
        }
      }

      describe("when green light didn't use to be turned on") {

      }

      describe("when green light used to be turned on") {

      }
    }

    describe("when given vector describes 4-roads crossroad") {
      describe("when there are less than 10 traffic lights states in the history") {

      }

      describe("when green light didn't use to be turned on") {

      }

      describe("when green light used to be turned on") {

      }
    }
  }
}
