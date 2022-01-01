// Implements traffic lights aggent using Akka vis Scala object
// Comunicates with other agent instances to set the current traffic lights state

package pl.edu.pw.elka

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.LoggerOps
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }

object TrafficLights {
  sealed trait Command
  final case class SetState(state: TrafficLightState) extends Command
  final case class GetState(replyTo: ActorRef[TrafficLightState]) extends Command

  sealed trait TrafficLightState
  final case object Red extends TrafficLightState
  final case object Green extends TrafficLightState

  def apply(id: Int, trafficLights: ActorRef[TrafficLights.Command]): Behavior[Command] = {
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case SetState(state) =>
          context.log.info("TrafficLights {}: Setting state to {}", id, state)
          trafficLights ! TrafficLights.Command(id, state)
          Behaviors.same
        case GetState(replyTo) =>
          context.log.info("TrafficLights {}: Getting state", id)
          replyTo ! Red
          Behaviors.same
      }
    }
  }
}
