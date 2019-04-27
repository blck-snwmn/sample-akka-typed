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
  //起動時にメッセージ送信
  destination.foreach(ref => ref ! RequestWantedItem(context.self))

  override def onMessage(msg: Base.BaseCommand): Behavior[Base.BaseCommand] = msg match {
    case addItem@AddItem(_, _) =>
      destination.foreach(ref => ref ! addItem)
      //destinationへ追加したら、sourceから再取得
      //暫定対応：実際には具体的なアイテムを取得する
      source.foreach(ref =>
        context.scheduleOnce(1.second, ref, RequestAnyItem(Quantity(10), context.self))
      )
      this
    case RequestAnyItem(num, ref) =>
      destination.fold(Behaviors.unhandled[BaseCommand]) {
        _ match {
          case `ref` =>
            //常にdestinationからの依頼を許可し、自分を返信先に書きえsourceへ依頼
            source.foreach(ref => ref ! RequestAnyItem(num, context.self))
            this
          case _ =>
            Behaviors.unhandled
        }
      }
  }
}
