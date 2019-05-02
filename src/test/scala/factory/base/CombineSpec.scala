package factory.base

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.WordSpecLike

class CombineSpec extends ScalaTestWithActorTestKit with WordSpecLike {

  import Base._

  "`destination` of Loader" must {
    "received message that Loader received `RequestAnyItem` from destination" in {
      val prob = createTestProbe[BaseCommand]
      val source = spawn(Storage())
      source ! AddItem("", Quantity(10))

      val loader = spawn(Loader(Some(source), Some(prob.ref)))

      prob.receiveMessage() // receive RequestWantedItem
      loader ! RequestAnyItem(Quantity(3), prob.ref)
      prob.expectMessage(AddItem("", Quantity(3)))
    }

    "doesn't received message when Loader received `RequestAnyItem` from other" in {
      val prob = createTestProbe[BaseCommand]
      val probDummy = createTestProbe[BaseCommand]
      val source = spawn(Storage())
      source ! AddItem("", Quantity(10))

      val loader = spawn(Loader(Some(source), Some(prob.ref)))

      prob.receiveMessage() // receive RequestWantedItem
      loader ! RequestAnyItem(Quantity(3), probDummy.ref)
      prob.expectNoMessage()
    }

    "doesn't received message when Storage doesn't have item" in {
      val prob = createTestProbe[BaseCommand]
      val source = spawn(Storage())

      val loader = spawn(Loader(Some(source), Some(prob.ref)))

      prob.receiveMessage() // receive RequestWantedItem
      loader ! RequestAnyItem(Quantity(3), prob.ref)
      prob.expectNoMessage()
    }
  }

  "Loader Joined Storage " must {
    "move all item in Storage from source to destination" in {
      val prob = createTestProbe[BaseCommand]

      val source = spawn(Storage())
      val destination = spawn(Storage())
      source ! AddItem("", Quantity(20))

      spawn(Loader(Some(source), Some(destination)))

      Thread.sleep(3000) //Loaderの搬入待ち

      //destination にすべて移動しているかどうか
      destination ! RequestAnyItem(Quantity(10), prob.ref)
      destination ! RequestAnyItem(Quantity(10), prob.ref)
      prob.expectMessage(AddItem("", Quantity(10)))
      prob.expectMessage(AddItem("", Quantity(10)))

      source ! RequestAnyItem(Quantity(10), prob.ref)
      prob.expectNoMessage()
    }
  }

  "Storage -> Loader -> `Builder` -> Loader -> Storage" must {
    "do building process" in {
      val prob = createTestProbe[BaseCommand]

      val source = spawn(Storage())
      val builder = spawn(Builder())
      val destination = spawn(Storage())
      source ! AddItem("", Quantity(8))

      spawn(Loader(Some(source), Some(builder)))
      spawn(Loader(Some(builder), Some(destination)))

      Thread.sleep(3000) //Loaderの搬入待ち

      //destination に移動しているかどうか
      destination ! RequestAnyItem(Quantity(10), prob.ref)
      prob.expectMessage(AddItem("", Quantity(2)))

      source ! RequestAnyItem(Quantity(2), prob.ref)
      prob.expectNoMessage()
    }
  }
}
