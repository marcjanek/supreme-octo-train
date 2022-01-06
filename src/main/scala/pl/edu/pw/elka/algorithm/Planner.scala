package pl.edu.pw.elka.algorithm

import akka.actor.ActorRef
import scala.util.control.Breaks._
import pl.edu.pw.elka.akka.TrafficLightState
import pl.edu.pw.elka.enums.Light

class Planner(val crossroad: Vector[TrafficLightState]) {
  def plan : Map[ActorRef, Light] = {
    if (crossroad.isEmpty) {
      return Map[ActorRef, Light]()
    }

    var scores = Array.fill[ScoreObject](crossroad.length)(null)

    for (i <- crossroad.indices) {
      val carsOnLane = crossroad(i).counters.values.sum

      scores(i) = new ScoreObject(
        score(carsOnLane, crossroad(i).historyData),
        crossroad(i)
      )
    }

    scores = scores.sortWith(_.score > _.score)

    var greenLights = Vector[ScoreObject](scores.head)

    breakable {
      for (i <- 1 until scores.length) {
        if (!checkCollision(greenLights.head.state, scores(i).state)) {
          greenLights = greenLights :+ scores(i)

          break
        }
      }
    }

    val redLights = scores.diff(greenLights)
    var resultLights : Map[ActorRef, Light] = Map()

    // Save green lights
    for (light <- greenLights) {
      resultLights += (light.state.actorRef -> Light.GREEN)
    }

    // Save red lights
    for (light <- redLights) {
      resultLights += (light.state.actorRef -> Light.RED)
    }

    resultLights
  }

  private def score(carsOnLane: Long, trafficLightsHistory: Vector[Light]): Double = {
    val trafficLightsHistoryValues = trafficLightsHistory.map(x => x.getValue)
    var sum : Double = 0.0

    var recentGreenLightIndex : Int = trafficLightsHistory.indexOf(Light.GREEN)
    if (recentGreenLightIndex == -1) recentGreenLightIndex = trafficLightsHistory.length


    for (i <- trafficLightsHistoryValues.indices) {
      sum = sum + ((trafficLightsHistory.length - i) * trafficLightsHistoryValues(i))
    }

    (recentGreenLightIndex + 1) * math.sqrt(carsOnLane) / math.sqrt(sum + 1)
  }

  private def checkCollision(winner: TrafficLightState, candidate: TrafficLightState): Boolean = {
    (winner.road.getIndex + candidate.road.getIndex == 3) || (winner.road == candidate.road)
  }
}

class ScoreObject(val score: Double, val state: TrafficLightState) {}
