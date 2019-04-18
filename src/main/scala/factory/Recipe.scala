package factory

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

class Recipe(productionTime: FiniteDuration, materialNum: Int, productNum: Int) {
  require(productionTime <= 1.minute)
  require(materialNum > 0)
  require(productNum > 0)
}
