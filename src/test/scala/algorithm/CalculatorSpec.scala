package pl.edu.pw.elka.algorithm

import org.scalatest._
import funspec._

class CalculatorSpec extends AnyFunSpec {
  describe("#score") {
    val carsOnLane = 16
    val numberOfNeighbourLanes = 2

    var calculator = new Calculator()

    describe("when green light turned on in last 10 cycles") {
      val trafficLightsHistory = Array(0, 0, 1, 0, 0, 0, 0, 0, 0, 0)

      it("returns the score for the given crossing") {
        val score = calculator.score(carsOnLane, numberOfNeighbourLanes, trafficLightsHistory)

        assert(score === 12.0)
      }
    }

    describe("when green light was not turned on in last 10 cycles") {
      val trafficLightsHistory = Array(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

      it("returns the score for the given crossing") {
        val score = calculator.score(carsOnLane, numberOfNeighbourLanes, trafficLightsHistory)

        assert(score === 132.0)
      }
    }
  }
}
