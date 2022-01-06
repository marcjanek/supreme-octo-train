package pl.edu.pw.elka.akka

import akka.actor.Actor
import pl.edu.pw.elka.akka.TrafficLight.{CarNumberResponse, CountCarsOnLane}
import pl.edu.pw.elka.enums.{Lanes, Roads}

object LaneCounter {
  case class NewDetectorsData(newData: Int)
  case object Stop
}

class LaneCounter(val junctionID: String, val roadId: Roads, val lane: Lanes) extends Actor {
  import LaneCounter._

  def receive: Receive = onMessage()

  private def onMessage(): Receive = {
    case CountCarsOnLane =>
      val cars = pl.edu.pw.elka.Main.database.getCarsNumber(junctionID, roadId, lane)
      sender() ! CarNumberResponse(cars, lane)

    case Stop =>
      context.stop(self)

    //    case _ =>
    //      throw Exception
  }
}
