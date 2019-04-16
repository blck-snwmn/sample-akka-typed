package factory

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}


object Factory {
  def apply(): Behavior[FactoryCommand] =
    Behaviors.setup(context => new Factory(context))

  sealed trait FactoryCommand
  final case class Request() extends FactoryCommand
  final case class GetNumOfCreatedItems(replyTo: ActorRef[Int]) extends FactoryCommand
}

class Factory(context: ActorContext[Factory.FactoryCommand]) extends AbstractBehavior[Factory.FactoryCommand] {

  import factory.Factory._

  context.log.info("Factory actor started")

  var numOfCreatedItems = 0

  override def onMessage(msg: FactoryCommand): Behavior[FactoryCommand] =
    msg match {
      case Request() =>
        context.log.info("Factory actor received Request message")
        numOfCreatedItems += 1
        this

      case GetNumOfCreatedItems(replyTo) =>
        context.log.info("Factory actor received GetNumOfCreatedItems message")
        replyTo ! numOfCreatedItems
        this
    }
}

object FactoryRoot extends App {

  import factory.Factory._

  val system = ActorSystem(Factory(), "factory")
  system ! Request()
}
