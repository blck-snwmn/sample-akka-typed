package factory

import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}


object Factory {
  def apply(): Behavior[FactoryCommand] =
    Behaviors.setup(context => new Factory(context))

  sealed trait FactoryCommand
  final case class Request() extends FactoryCommand
  final case class AddMaterial(num: Int) extends FactoryCommand
  //at this version, because removed `sender()`, include ActorRef in parameter.
  final case class GetNumOfCreatedItems(replyTo: ActorRef[Int]) extends FactoryCommand
}

class Factory(context: ActorContext[Factory.FactoryCommand]) extends AbstractBehavior[Factory.FactoryCommand] {

  import factory.Factory._

  context.log.info("Factory actor started")
  private val numOfNeedToCreate = 10
  private var numOfMaterial = 0
  private var numOfCreatedItems = 0

  override def onMessage(msg: FactoryCommand): Behavior[FactoryCommand] =
    msg match {
      case Request() =>
        context.log.info("Factory actor received Request message")
        if (numOfMaterial >= numOfNeedToCreate) {
          numOfMaterial -= numOfNeedToCreate
          numOfCreatedItems += 1 //created!!
        }
        this

      case GetNumOfCreatedItems(replyTo) =>
        context.log.info("Factory actor received GetNumOfCreatedItems message")
        replyTo ! numOfCreatedItems
        this

      case AddMaterial(num) =>
        context.log.info(s"Factory actor received AddMaterial($num) message")
        numOfMaterial += num
        if (numOfMaterial >= numOfNeedToCreate)
          context.self ! Request()
        this
    }
}

object FactoryRoot extends App {

  import factory.Factory._

  val system = ActorSystem(Factory(), "factory")
  system ! Request()
}
