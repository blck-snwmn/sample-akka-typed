package factory.base

//数量
case class Quantity(num: Int) extends Ordered[Quantity] {
  require(num >= 0)
  def +(that: Quantity) = Quantity(num + that.num)
  def -(that: Quantity) = Quantity(num - that.num)
  override def compare(that: Quantity): Int = num.compareTo(that.num)
}


