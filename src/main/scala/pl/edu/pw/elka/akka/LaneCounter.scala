package pl.edu.pw.elka.akka

import akka.actor.Actor
import akka.event.{Logging, LoggingAdapter}
import pl.edu.pw.elka.enums.{Lanes, Roads}

import scala.collection.immutable.Vector

object LaneCounter {
  case class NewDetectorsData(newData: Int)
  case object CountCarsOnLane
  case object Stop
}

class LaneCounter(val trafficLightId: Lanes, val roadId: Roads) extends Actor {//laneId in [A|B|C|D][P1|P2|L]
  import LaneCounter._
  var log: LoggingAdapter = Logging(context.system, this)

  private val dataFromDetectors = Vector.empty

  def receive: Receive = onMessage(dataFromDetectors)

  private def onMessage(detectorsData: Vector[Int]): Receive = {
    case NewDetectorsData(data) =>
      context.become(onMessage(data +: detectorsData))
    case CountCarsOnLane =>
      sender() ! detectorsData.sum
      context.become(onMessage(Vector.empty))
    case Stop =>
      context.stop(self)
//    case _ =>
//      throw Exception
  }
}
