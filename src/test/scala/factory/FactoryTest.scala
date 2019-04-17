package factory

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.WordSpecLike
import scala.concurrent.duration._

class FactorySpec extends ScalaTestWithActorTestKit with WordSpecLike {

  import Factory._

  "Factory" must {
    "num of created items equals 0 when does't receive message" in {
      val prob = createTestProbe[Int]
      val actor = spawn(Factory(), "test1")

      actor ! GetNumOfCreatedItems(prob.ref)
      prob.expectMessage(0)
    }

    "num of created items equals 0 when no material" in {
      val prob = createTestProbe[Int]
      val actor = spawn(Factory(), "test2")

      actor ! Request()
      actor ! GetNumOfCreatedItems(prob.ref)
      prob.expectMessage(0)
    }

    "num of created items equals 1 when there are material to need" in {
      val prob = createTestProbe[Int]
      val actor = spawn(Factory(), "test3")

      actor ! AddMaterial(10)
      Thread.sleep(100)
      actor ! GetNumOfCreatedItems(prob.ref)
      prob.expectMessage(1)
    }
  }

}
