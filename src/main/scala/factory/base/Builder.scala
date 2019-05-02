package factory.base

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

import scala.concurrent.duration._

object Builder {

  import factory.base.Base.BaseCommand

  def apply(): Behavior[Base.BaseCommand] =
    Behaviors.setup(context => new Builder(context))

  private case class Created(name: String, num: Quantity) extends BaseCommand
}
class Builder(context: ActorContext[Base.BaseCommand]) extends AbstractBehavior[Base.BaseCommand] {

  import Base._
  import Builder._

  private var materialsStorage: Quantity = Quantity(0)
  private var productStorage: Quantity = Quantity(0)

  private val requiredQuantity = Quantity(5)

  //暫定対応
  //在庫がないため返信できなかったリクエストを保持
  private var delayResponseActor: Option[RequestAnyItem] = None

  override def onMessage(msg: Base.BaseCommand): Behavior[Base.BaseCommand] = msg match {
    case AddItem(_, num) =>
      materialsStorage += num
      create()
      this
    case Created(_, num) =>
      productStorage += num
      delayResponseActor.foreach(r => context.self ! r)
      delayResponseActor = None
      create()
      this
    case RequestWantedItem(ref) =>
      //実際には欲しい素材のリクエストを投げる
      ref ! RequestAnyItem(Quantity(10), context.self)
      this
    case request@RequestAnyItem(num, replyTo) =>
      //暫定
      //このクラスのようにどのようなリクエストであっても一律なものを返すために
      //リクエストは継承構造にすること
      productStorage match {
        case Quantity(0) => delayResponseActor = Some(request)
        case storage@_ =>
          val n = if (storage > num) num else storage
          productStorage -= n
          replyTo ! AddItem("", n)
      }
      this
  }


  private def create(): Unit = {
    if (materialsStorage >= requiredQuantity) {
      materialsStorage -= requiredQuantity
      context.scheduleOnce(100.millisecond, context.self, Created("", Quantity(2)))
    }
  }
}
