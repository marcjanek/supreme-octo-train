package pl.edu.pw.elka.akka

import akka.actor.{Actor, OneForOneStrategy}
import akka.actor.SupervisorStrategy.Escalate
import akka.event.{Logging, LoggingAdapter}
import pl.edu.pw.elka.akka.TrafficLight.CarNumberResponse
import pl.edu.pw.elka.enums.{Lanes, Roads}

object LaneCounter {
  case object CountCarsOnLane
  case object Stop
  case object ErrorAlert
}

class LaneCounterEmergencyAlertException(private val message: String = "",
                                         private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

class LaneCounter(val junctionID: String, val roadId: Roads, val lane: Lanes) extends Actor {
  import LaneCounter._

  var log: LoggingAdapter = Logging(context.system, this)

  override val supervisorStrategy: OneForOneStrategy = OneForOneStrategy(loggingEnabled = false) {
    case _: Exception                                => Escalate
  }

  def receive: Receive = onMessage()

  private def onMessage(): Receive = {
    case CountCarsOnLane =>
      var cars: Long = 0
      try {
        cars = pl.edu.pw.elka.Main.database.getCarsNumber(junctionID, roadId, lane)
        Validate(cars)
      }
      catch {
        case e:Throwable =>
          log.error("Error occurred during getting data from database")
          self ! ErrorAlert
      }

      sender() ! CarNumberResponse(cars, lane)

    case Stop =>
      context.stop(self)

    case ErrorAlert =>
      throw new LaneCounterEmergencyAlertException("error")

    case _ =>
      throw new RuntimeException("lane counter system error occurred")
  }

  /*
  Get the healthy status of the lane counter sensors
   */
  private def Validate(cars: Long): Unit = {
    if(cars < 0) {
      self ! ErrorAlert
    }
  }
}
