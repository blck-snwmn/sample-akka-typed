package factory

import akka.actor.Cancellable
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

import scala.concurrent.duration._

object Factory {
  def apply(): Behavior[FactoryCommand] =
    Behaviors.setup(context => new Factory(context))

  sealed trait FactoryCommand
  final case class Request() extends FactoryCommand
  final case class AddMaterial(num: Int) extends FactoryCommand
  //at this version, because removed `sender()`, include ActorRef in parameter.
  final case class GetNumOfCreatedItems(replyTo: ActorRef[Int]) extends FactoryCommand
  private final case class Created(num: Int) extends FactoryCommand
}

class Factory(context: ActorContext[Factory.FactoryCommand]) extends AbstractBehavior[Factory.FactoryCommand] {

  import factory.Factory._

  context.log.info("Factory actor started")
  private val numOfNeedToCreate = 10
  private var numOfMaterial = 0
  private var numOfCreatedItems = 0
  private var worker: Option[Cancellable] = None

  private def canCreate = worker == None
  private def isEnoughMaterial = numOfMaterial >= numOfNeedToCreate

  override def onMessage(msg: FactoryCommand): Behavior[FactoryCommand] =
    msg match {
      case Request() =>
        context.log.info("Factory actor received Request message")
        //if there is material to need, and no create
        if (isEnoughMaterial && canCreate) {
          numOfMaterial -= numOfNeedToCreate // resume material
          //take time to create
          worker = Some(
            //after 10 seconds, create 1 item
            context.scheduleOnce(1 milliseconds, context.self, Created(1))
          )
        }
        this
      case Created(num) =>
        context.log.info(s"Factory actor received Created($num) message")
        numOfCreatedItems += num //created!!
        worker = None
        context.self ! Request()
        this
      case GetNumOfCreatedItems(replyTo) =>
        context.log.info("Factory actor received GetNumOfCreatedItems message")
        replyTo ! numOfCreatedItems
        this

      case AddMaterial(num) =>
        context.log.info(s"Factory actor received AddMaterial($num) message")
        numOfMaterial += num
        //if there is material to need, send request message
        if (isEnoughMaterial)
          context.self ! Request()
        this
    }
}

object FactoryRoot extends App {

  import factory.Factory._

  val system = ActorSystem(Factory(), "factory")
  system ! Request()
}
