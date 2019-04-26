package factory.base

import org.scalatest.{Matchers, WordSpecLike}

class QuantitySpec extends WordSpecLike with Matchers {
  "Quantity" must {
    "created" in {
      noException should be thrownBy Quantity(0)
      noException should be thrownBy Quantity(10)
    }

    "throw Exception when args is less than 0" in {
      the[IllegalArgumentException] thrownBy Quantity(-1)
    }
    "plus other `Quantity`" in {
      Quantity(10) should be(Quantity(6) + Quantity(4))
    }

    "sub other `Quantity`" in {
      Quantity(2) should be(Quantity(6) - Quantity(4))
      Quantity(0) should be(Quantity(4) - Quantity(4))
    }

    "throw Exception when sub other `Quantity` greater than own" in {
      the[IllegalArgumentException] thrownBy {
        Quantity(1) - Quantity(2)
      }
    }

    "compare other `Quantity``" in {
      Quantity(10) should be > Quantity(6)
      Quantity(6) should be >= Quantity(6)
      Quantity(6) should be < Quantity(7)
    }
  }
}
