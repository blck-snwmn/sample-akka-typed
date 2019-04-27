package factory.base

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

object Storage {
  def apply(): Behavior[Base.BaseCommand] =
    Behaviors.setup(context => new Storage(context))
}
class Storage(context: ActorContext[Base.BaseCommand]) extends AbstractBehavior[Base.BaseCommand] {

  import Base._

  private var innerStorage: Quantity = Quantity(0)

  //暫定対応
  //在庫がないため返信できなかったリクエストを保持
  private var delayResponseActor: Option[RequestAnyItem] = None

  override def onMessage(msg: Base.BaseCommand): Behavior[Base.BaseCommand] = msg match {
    case request@RequestAnyItem(num, replyTo) =>
      innerStorage match {
        case Quantity(0) => delayResponseActor = Some(request)
        case storage@_ =>
          val n = if (storage > num) num else storage
          innerStorage -= n
          replyTo ! AddItem("", n)
      }
      this
    case RequestWantedItem(ref) =>
      ref ! RequestAnyItem(Quantity(10), context.self)
      this
    case AddItem(_, num) =>
      innerStorage += num
      delayResponseActor.foreach(r => context.self ! r)
      delayResponseActor = None
      this
  }
}
