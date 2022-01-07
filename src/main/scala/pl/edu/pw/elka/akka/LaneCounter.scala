package pl.edu.pw.elka.akka

import akka.actor.{Actor, OneForOneStrategy}
import akka.actor.SupervisorStrategy.{Escalate, Restart, Resume}
import pl.edu.pw.elka.akka.TrafficLight.{CarNumberResponse, CountCarsOnLane}
import pl.edu.pw.elka.enums.{Lanes, Roads}
import scala.concurrent.duration.DurationInt

object LaneCounter {
  case class NewDetectorsData(newData: Int)
  case object Stop
}

class LaneCounterEmergencyAlertException(private val message: String = "",
                                         private val cause: Throwable = None.orNull)
  extends Exception(message, cause)

class LaneCounter(val junctionID: String, val roadId: Roads, val lane: Lanes) extends Actor {
  import LaneCounter._

  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 1, withinTimeRange = 2 seconds) {
    case _: Exception                                => Escalate
  };

  def receive: Receive = onMessage()

  private def onMessage(): Receive = {
    case CountCarsOnLane =>
     /* if(!Healthy()) {
        throw new LaneCounterEmergencyAlertException("error")
      }*/
      val cars = pl.edu.pw.elka.Main.database.getCarsNumber(junctionID, roadId, lane)
      sender() ! CarNumberResponse(cars, lane)

    case Stop =>
      context.stop(self)
    case _ =>
      throw new RuntimeException("")
  }

  /*
  Get the healthy status of the lane counter sensors
   */
  private def Healthy(): Boolean = {
    val r = new scala.util.Random
    if(r.nextInt(100) > 70) {
      true;
    } else {
      false;
    }
  }
}
