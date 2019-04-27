package factory.base

import akka.actor.typed.ActorRef

object Base {
  trait BaseCommand
  case class RequestAnyItem(num: Quantity, replyTo: ActorRef[BaseCommand]) extends BaseCommand
  case class RequestWantedItem(replyTo: ActorRef[BaseCommand]) extends BaseCommand
  case class AddItem(name: String, num: Quantity) extends BaseCommand
}

