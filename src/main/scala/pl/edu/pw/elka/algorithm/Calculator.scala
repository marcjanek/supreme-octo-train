package pl.edu.pw.elka.algorithm

import scala.math.sqrt

class Calculator() {
  def score(carsOnLane: Int, numberOfNeighbourLanes: Int, trafficLightsHistory: Array[Int]): Double = {
    var sum : Double = 0.0

    var recentGreenLightIndex : Int = trafficLightsHistory.indexOf(1)
    if (recentGreenLightIndex == -1) recentGreenLightIndex = 10


    for (i <- 0 until trafficLightsHistory.length) {
      sum = sum + ((10 - i) * trafficLightsHistory(i))
    }

    (numberOfNeighbourLanes + 1) * (recentGreenLightIndex + 1) * math.sqrt(carsOnLane) / math.sqrt(sum + 1)
  }
}
