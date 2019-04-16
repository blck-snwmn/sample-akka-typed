package factory

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import factory.Factory._


object Factory {
  def apply(): Behavior[FactoryCommand] =
    Behaviors.setup(context => new Factory(context))

  sealed trait FactoryCommand

  final case class Request() extends FactoryCommand

}

class Factory(context: ActorContext[FactoryCommand]) extends AbstractBehavior[FactoryCommand] {
  context.log.info("Factory actor started")
  override def onMessage(msg: FactoryCommand): Behavior[FactoryCommand] =
    msg match {
      case Request() =>
        context.log.info("Factory actor received Request message")
        this
    }
}

object FactoryRoot extends App {
  val system = ActorSystem(Factory(), "factory")
  system ! Request()
}
