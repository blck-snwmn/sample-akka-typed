package factory.base

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.WordSpecLike

class BuilderSpec extends ScalaTestWithActorTestKit with WordSpecLike {

  import Base._

  "Builder" must {
    "request " in {
      val prob = createTestProbe[BaseCommand]
      val builder = spawn(Builder())

      builder ! RequestWantedItem(prob.ref)
      prob.expectMessage(RequestAnyItem(Quantity(10), builder))
    }

    "build" in {
      val prob = createTestProbe[BaseCommand]
      val builder = spawn(Builder())

      builder ! AddItem("", Quantity(5))
      builder ! RequestAnyItem(Quantity(2), prob.ref)
      prob.expectMessage(AddItem("", Quantity(2)))
    }

    "doesn't build when there is deficient material" in {
      val prob = createTestProbe[BaseCommand]
      val builder = spawn(Builder())

      builder ! AddItem("", Quantity(4))
      builder ! RequestAnyItem(Quantity(2), prob.ref)
      prob.expectNoMessage()
    }

    "build when add material" in {
      val prob = createTestProbe[BaseCommand]
      val builder = spawn(Builder())

      builder ! RequestAnyItem(Quantity(2), prob.ref)
      builder ! AddItem("", Quantity(5))
      prob.expectMessage(AddItem("", Quantity(2)))
    }

    "keep building" in {
      val prob = createTestProbe[BaseCommand]
      val builder = spawn(Builder())

      builder ! AddItem("", Quantity(10))
      builder ! RequestAnyItem(Quantity(2), prob.ref)
      prob.expectMessage(AddItem("", Quantity(2)))
      builder ! RequestAnyItem(Quantity(2), prob.ref)
      prob.expectMessage(AddItem("", Quantity(2)))
    }

    "store keeping building item" in {
      val prob = createTestProbe[BaseCommand]
      val builder = spawn(Builder())

      builder ! AddItem("", Quantity(10))
      Thread.sleep(1000)
      builder ! RequestAnyItem(Quantity(4), prob.ref)
      prob.expectMessage(AddItem("", Quantity(4)))
    }
  }
}
