package factory.base

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.WordSpecLike

class LoaderSpec extends ScalaTestWithActorTestKit with WordSpecLike {

  import factory.base.Base._

  "Loader" must {
    "send received `AddItem`" in {
      val destination = createTestProbe[BaseCommand]
      val actor = spawn(Loader(None, Some(destination.ref)))
      val expectedMessage = AddItem("", Quantity(0))
      actor ! expectedMessage
      destination.expectMessage(expectedMessage)
    }
  }
}
