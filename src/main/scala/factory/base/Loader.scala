package factory.base

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}
import akka.util.Timeout

import scala.concurrent.duration._


object Loader {
  def apply(
             source: Option[ActorRef[Base.BaseCommand]],
             destination: Option[ActorRef[Base.BaseCommand]]
           ): Behavior[Base.BaseCommand] =
    Behaviors.setup(context => new Loader(context, source, destination))
}

class Loader(context: ActorContext[Base.BaseCommand],
             source: Option[ActorRef[Base.BaseCommand]],
             destination: Option[ActorRef[Base.BaseCommand]]
            ) extends AbstractBehavior[Base.BaseCommand] {

  import factory.base.Base._

  override def onMessage(msg: Base.BaseCommand): Behavior[Base.BaseCommand] = msg match {
    case addItem@AddItem(_, _) =>
      destination.foreach(ref => ref ! addItem)
      this
  }
}
