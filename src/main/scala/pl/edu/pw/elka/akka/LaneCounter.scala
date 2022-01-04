package pl.edu.pw.elka.akka

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import org.apache.log4j.BasicConfigurator
import pl.edu.pw.elka.akka.LaneCounter.{CountCarsOnLane, NewDetectorsData}
import pl.edu.pw.elka.enums.{Lanes, Roads}

import scala.collection.immutable.Vector
import scala.concurrent.duration._

object LaneCounter {
  case class NewDetectorsData(newData: Int)
  case object CountCarsOnLane
  case object Stop
}

class LaneCounter(val trafficLightId: Lanes, val roadId: Roads) extends Actor {
  import LaneCounter._
  var log: LoggingAdapter = Logging(context.system, this)

  private val dataFromDetectors = Vector.empty

  def receive: Receive = onMessage(dataFromDetectors)

  private def onMessage(detectorsData: Vector[Int]): Receive = {
    case NewDetectorsData(data) =>
      context.become(onMessage(data +: detectorsData))
      log.info(detectorsData.toString())
    case CountCarsOnLane =>
      log.info(detectorsData.toString())
      // sender() ! detectorsData.sum
      context.become(onMessage(Vector.empty))
    case Stop =>
      context.stop(self)
    //    case _ =>
    //      throw Exception
  }
}
