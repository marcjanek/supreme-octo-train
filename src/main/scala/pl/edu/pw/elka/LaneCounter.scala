package pl.edu.pw.elka

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.{Logging, LoggingAdapter}
import org.apache.log4j.BasicConfigurator
import pl.edu.pw.elka.LaneCounter.{CountCarsOnLane, NewDetectorsData}

import scala.concurrent.duration._
import collection.immutable.Vector

object LaneCounter {
  case class NewDetectorsData(newData: Int)
  case object CountCarsOnLane
  case object Stop
}

object TrafficLightEnum extends Enumeration {
  type TrafficLaneId = Value

  val P1, P2, L = Value
}

class LaneCounter(val trafficLightId: TrafficLightEnum.TrafficLaneId, val roadId: TrafficLaneLaneEnum.RoadId) extends Actor {//laneId in [A|B|C|D][P1|P2|L]
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

object Main {
  def main(Args: Array[String]): Unit = {
    BasicConfigurator.configure()
    val system = ActorSystem("test")
    val testLaneCounter = system.actorOf(Props(new LaneCounter(TrafficLightEnum.P1, TrafficLaneLaneEnum.A)), "counter")

    import system.dispatcher

    val cancellable = system.scheduler.scheduleWithFixedDelay(Duration.Zero, 1.second, testLaneCounter, NewDetectorsData(4))

    testLaneCounter ! CountCarsOnLane

    //cancellable.cancel()
  }
}
