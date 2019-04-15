package factory

import akka.actor.typed.{ActorSystem, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}


object Factory {
  def apply(): Behavior[String] =
    Behaviors.setup(context => new Factory(context))
}

class Factory(context: ActorContext[String]) extends AbstractBehavior[String] {
  override def onMessage(msg: String): Behavior[String] =
    msg match {
      case "create" =>
        println("created")
        this
    }
}

object FactoryRoot extends App {
  val system = ActorSystem(Factory(), "factory")
  system ! "create"
}
