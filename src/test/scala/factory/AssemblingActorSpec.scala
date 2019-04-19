package factory

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.WordSpecLike
import scala.concurrent.duration._

class AssemblingActorSpec extends ScalaTestWithActorTestKit with WordSpecLike {

  import AssemblingActor._

  "Factory" must {
    "num of created items equals 0 when does't receive message" in {
      val prob = createTestProbe[Int]
      val actor = spawn(AssemblingActor(), "test1")

      actor ! GetNumOfCreatedItems(prob.ref)
      prob.expectMessage(0)
    }

    "num of created items equals 0 when no material" in {
      val prob = createTestProbe[Int]
      val actor = spawn(AssemblingActor(), "test2")

      actor ! Request()
      actor ! GetNumOfCreatedItems(prob.ref)
      prob.expectMessage(0)
    }

    "num of created items equals 3 when there are material to need" in {
      val prob = createTestProbe[Int]
      val actor = spawn(AssemblingActor(), "test3")

      actor ! AddMaterial(31)
      Thread.sleep(100)
      actor ! GetNumOfCreatedItems(prob.ref)
      prob.expectMessage(3)
    }
  }

}
