package factory

import akka.actor.Cancellable
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import akka.actor.typed.scaladsl.{AbstractBehavior, ActorContext, Behaviors}

import scala.concurrent.duration._

object AssemblingActor {
  def apply(): Behavior[FactoryCommand] =
    Behaviors.setup(context => new AssemblingActor(context))

  sealed trait FactoryCommand
  final case class Request() extends FactoryCommand
  final case class AddMaterial(num: Int) extends FactoryCommand
  //at this version, because removed `sender()`, include ActorRef in parameter.
  final case class GetNumOfCreatedItems(replyTo: ActorRef[Int]) extends FactoryCommand
  private final case class Created(num: Int) extends FactoryCommand
}

class AssemblingActor(context: ActorContext[AssemblingActor.FactoryCommand]) extends AbstractBehavior[AssemblingActor.FactoryCommand] {

  import factory.AssemblingActor._

  context.log.info("Factory actor started")
  private var numOfMaterial = 0
  private var numOfCreatedItems = 0
  private var worker: Option[Cancellable] = None
  private val recipe = new Recipe(1 millisecond, 10, 1)
  private def canCreate = worker == None
  private def isEnoughMaterial = numOfMaterial >= recipe.materialNum

  override def onMessage(msg: FactoryCommand): Behavior[FactoryCommand] =
    msg match {
      case Request() =>
        context.log.info("Factory actor received Request message")
        //if there is material to need, and no create
        if (isEnoughMaterial && canCreate) {
          numOfMaterial -= recipe.materialNum // resume material
          //take time to create
          worker = Some(
            //after 10 seconds, create 1 item
            context.scheduleOnce(recipe.productionTime, context.self, Created(recipe.productNum))
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

  import factory.AssemblingActor._

  val system = ActorSystem(AssemblingActor(), "factory")
  system ! Request()
}
