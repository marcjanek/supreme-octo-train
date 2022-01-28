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
        val crossroad = Vector[TrafficLightState]()
        val neighbours = Map[Roads, Vector[TrafficLightState]]()
        val planner = new Planner(crossroad, neighbours)

        val expected = Map[ActorRef, Light]()
        assert(planner.plan === expected)
      }
    }

    it("returns valid traffic lights configuration") {
      val numberOfRoads = 4
      val actors: Vector[ActorRef] = Vector.fill(2 * numberOfRoads)(system.actorOf(Props.empty))

      var crossroad: Vector[TrafficLightState] = Vector[TrafficLightState]()
      val neighbours = Map[Roads, Vector[TrafficLightState]]()
      val historyData = Vector.fill(3)(Light.RED)
      val counters1 = Vector[Map[Lanes, Long]](Map(Lanes.P1 -> 0, Lanes.P2 -> 0), Map(Lanes.L -> 0))
      val counters2 = Vector[Map[Lanes, Long]](Map(Lanes.P1 -> 3, Lanes.P2 -> 3), Map(Lanes.L -> 4))

      for (i <- 0 until numberOfRoads) {
        for (j <- 0 to 1) {
          if (i == 0) {
            val trafficLightState = new TrafficLightState(
              actors(2 * i + j),
              Roads.getByIndex(i),
              historyData,
              counters2(j)
            )

            crossroad = trafficLightState +: crossroad
          } else {
            val trafficLightState = new TrafficLightState(
              actors(2 * i + j),
              Roads.getByIndex(i),
              historyData,
              counters1(j)
            )

            crossroad = trafficLightState +: crossroad
          }
        }
      }

      val planner = new Planner(crossroad, neighbours)
      val generatedPlan = planner.plan

      assert(generatedPlan.size === 8)

      for (i <- 0 to 1) {
        assert(generatedPlan(actors(i)) === Light.GREEN)
      }

      for (i <- 2 until 2 * numberOfRoads) {
        assert(generatedPlan(actors(i)) === Light.RED)
      }
    }
  }
}
