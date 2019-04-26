package factory.base

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.WordSpecLike

class LoaderSpec extends ScalaTestWithActorTestKit with WordSpecLike {

  import factory.base.Base._

  "Loader" must {
    "send `RequestWantedItem` when it was generated" in {
      val destination = createTestProbe[BaseCommand]
      val actor = spawn(Loader(None, Some(destination.ref)))
      destination.expectMessage(RequestWantedItem(actor.ref))
    }

    "send received `AddItem`" in {
      val destination = createTestProbe[BaseCommand]
      val actor = spawn(Loader(None, Some(destination.ref)))
      destination.receiveMessage()
      val expectedMessage = AddItem("", Quantity(0))
      actor ! expectedMessage
      destination.expectMessage(expectedMessage)
    }
  }
}
