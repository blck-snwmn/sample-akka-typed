package factory

import org.scalatest.{Matchers, WordSpecLike}

import scala.concurrent.duration._

class RecipeSpec extends WordSpecLike with Matchers {

  "Recipe" must {
    "generate" in {
      noException should be thrownBy
        new Recipe(1.minute, 1, 1)
    }
    "does't set larger than 1 minute as productionTime" in {
      the[IllegalArgumentException] thrownBy
        new Recipe(1.1.minute, 1, 1)
    }

    "does't set 0 minute as materialNum" in {
      the[IllegalArgumentException] thrownBy
        new Recipe(1.minute, 0, 1)
    }

    "does't set 0 minute as productNum" in {
      the[IllegalArgumentException] thrownBy
        new Recipe(1.minute, 1, 0)
    }
  }
}
