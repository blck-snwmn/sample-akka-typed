package factory

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

class Recipe(val productionTime: FiniteDuration, val materialNum: Int, val productNum: Int) {
  require(productionTime <= 1.minute)
  require(materialNum > 0)
  require(productNum > 0)
}
