package factory.base

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.WordSpecLike

class StorageSpec extends ScalaTestWithActorTestKit with WordSpecLike {

  import Base._

  "Storage that have sufficient item" must {
    "send requested item" in {
      val prob = createTestProbe[BaseCommand]
      val actor = spawn(Storage())
      actor ! AddItem("", Quantity(11))
      actor ! RequestAnyItem(Quantity(10), prob.ref)
      prob.expectMessage(AddItem("", Quantity(10)))
    }
    "doesn't send item when get all item" in {
      val prob = createTestProbe[BaseCommand]
      val actor = spawn(Storage())
      actor ! AddItem("", Quantity(11))
      actor ! RequestAnyItem(Quantity(10), prob.ref)
      prob.expectMessage(AddItem("", Quantity(10)))
      actor ! RequestAnyItem(Quantity(1), prob.ref)
      prob.expectMessage(AddItem("", Quantity(1)))
      actor ! RequestAnyItem(Quantity(1), prob.ref)
      prob.expectNoMessage()
    }
  }
  "Storage that have deficient item" must {
    "send all item in storage item" in {
      val prob = createTestProbe[BaseCommand]
      val actor = spawn(Storage())
      actor ! AddItem("", Quantity(5))
      actor ! RequestAnyItem(Quantity(10), prob.ref)
      prob.expectMessage(AddItem("", Quantity(5)))
    }
  }
  "Storage that doesn't have item" must {
    "does't send item" in {
      val prob = createTestProbe[BaseCommand]
      val actor = spawn(Storage())
      actor ! RequestAnyItem(Quantity(10), prob.ref)
      prob.expectNoMessage
    }

    "send requested item after add sufficient item" in {
      val prob = createTestProbe[BaseCommand]
      val actor = spawn(Storage())
      actor ! RequestAnyItem(Quantity(10), prob.ref)
      prob.expectNoMessage
      actor ! AddItem("", Quantity(5))
      prob.expectMessage(AddItem("", Quantity(5)))
    }

    "send requested item after add deficient item" in {
      val prob = createTestProbe[BaseCommand]
      val actor = spawn(Storage())
      actor ! RequestAnyItem(Quantity(10), prob.ref)
      prob.expectNoMessage
      actor ! AddItem("", Quantity(20))
      prob.expectMessage(AddItem("", Quantity(10)))
    }

    "send requested item only once after add sufficient item" in {
      val prob = createTestProbe[BaseCommand]
      val actor = spawn(Storage())
      actor ! RequestAnyItem(Quantity(10), prob.ref)
      prob.expectNoMessage
      actor ! AddItem("", Quantity(10))
      prob.expectMessage(AddItem("", Quantity(10)))
      actor ! AddItem("", Quantity(2))
      prob.expectNoMessage
    }
  }
}
