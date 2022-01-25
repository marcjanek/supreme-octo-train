package pl.edu.pw.elka.algorithm

import akka.actor.ActorRef
import scala.util.control.Breaks._
import pl.edu.pw.elka.akka.TrafficLightState
import pl.edu.pw.elka.enums._

class Planner(val crossroad: Vector[TrafficLightState], val neighbours: Map[Roads, Vector[TrafficLightState]]) {
  def plan: Map[ActorRef, Light] = {
    if (crossroad.isEmpty) {
      return Map[ActorRef, Light]()
    }

    var scores = Vector.fill[ScoreObject](crossroad.length)(null)

    for (i <- crossroad.indices) {
      val carsOnLane = crossroad(i).counters.values.sum
      val planningBoost = crossroad(i).counters.size
      val currentNeighbour = neighbours(Roads.getByIndex(3 - crossroad(i).road.getIndex))

      scores = scores.updated(
        i,
        new ScoreObject(
          score(
            carsOnLane,
            crossroad(i),
            planningBoost,
            currentNeighbour
          ),
          crossroad(i)
        )
      )
    }

    scores = scores.sortWith(_.score > _.score)

    val greenLights = searchForWinningOffer(scores, 0, Vector[ScoreObject]())

    val redLights = scores.diff(greenLights)
    var resultLights: Map[ActorRef, Light] = Map()

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

  private def searchForWinningOffer(offers: Vector[ScoreObject], index: Int, selectedOffers: Vector[ScoreObject]): Vector[ScoreObject] = {
    if (offers.length == index) return selectedOffers

    var availableOffers = Vector[Vector[ScoreObject]](selectedOffers)

    for (i <- index until offers.length) {
      var collisionFound = false

      breakable {
        for (offerMember <- selectedOffers) {
          if (checkCollision(offerMember.state, offers(i).state)) {
            collisionFound = true

            break
          }
        }
      }

      if (!collisionFound) {
        availableOffers = availableOffers :+ searchForWinningOffer(offers, i + 1, selectedOffers :+ offers(i))
      }
    }

    availableOffers.maxBy { x => calculateOfferScore(x) }
  }

  private def calculateOfferScore(offer: Vector[ScoreObject]): Double = offer.map { x => x.score }.sum

  private def calculateNeighbourFactor(currentTrafficLights: TrafficLightState, neighbour: Vector[TrafficLightState]): Long = {
    val currentTrafficLightsIndex = currentTrafficLights.road.getOrderedIndex
    var numberOfCars : Long = 0

    for (trafficLight <- neighbour) {
      val neighbourIndex = trafficLight.road.getOrderedIndex
      val counters = trafficLight.counters.withDefaultValue(0)

      if (neighbourIndex == currentTrafficLightsIndex) { // straight
        numberOfCars = numberOfCars.asInstanceOf[Long] + counters(Lanes.P1).asInstanceOf[Long] + counters(Lanes.P2).asInstanceOf[Long]
      } else if (neighbourIndex == ((currentTrafficLightsIndex + 1) % 4)) { // left
        numberOfCars = numberOfCars.asInstanceOf[Long] + counters(Lanes.L).asInstanceOf[Long]
      } else if (neighbourIndex == ((currentTrafficLightsIndex + 3) % 4)) { // right
        numberOfCars = numberOfCars.asInstanceOf[Long] + counters(Lanes.P2).asInstanceOf[Long]
      }
    }

    numberOfCars
  }

  private def score(
                     carsOnLane: Long,
                     trafficLightState: TrafficLightState,
                     planningBoost: Int,
                     neighbour: Vector[TrafficLightState]
                   ): Double = {
    val trafficLightsHistory = trafficLightState.historyData
    val trafficLightsHistoryValues = trafficLightsHistory.map(x => x.getValue)
    var sum: Double = 0.0
    val neighbourFactor = calculateNeighbourFactor(trafficLightState, neighbour)

    var recentGreenLightIndex: Int = trafficLightsHistory.indexOf(Light.GREEN)
    if (recentGreenLightIndex == -1) recentGreenLightIndex = trafficLightsHistory.length

    for (i <- trafficLightsHistoryValues.indices) {
      sum = sum + ((trafficLightsHistory.length - i) * trafficLightsHistoryValues(i))
    }

    ((recentGreenLightIndex + 1) * math.sqrt(5 * carsOnLane + neighbourFactor) / math.sqrt(sum + 1)) + planningBoost
  }

  private def checkCollision(winner: TrafficLightState, candidate: TrafficLightState): Boolean = {
    !(
      (winner.road.getIndex + candidate.road.getIndex == 3) &&
      (winner.counters.keys.toList.contains(Lanes.L) == candidate.counters.keys.toList.contains(Lanes.L))
     || (winner.road == candidate.road))
  }
}

class ScoreObject(val score: Double, val state: TrafficLightState) {}
