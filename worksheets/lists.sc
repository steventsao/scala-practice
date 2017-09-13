object Lists {
  def sum(xs: List[Int]): Int = {
    def getSum(list: List[Int], sum: Int): Int = {
      list match {
        case Nil => sum
        case _ => getSum(list.tail, list.head + sum)
      }
    }
    getSum(xs.tail, xs.head)
  }
  def max(xs: List[Int]): Int = {
    def getMax(list: List[Int], currentMax: Int): Int = {
      list match {
        case Nil => currentMax
        case _ =>
          val greaterInt = if (xs.head > currentMax) xs.head else currentMax
          getMax(xs.tail, greaterInt)

      }
    }
    getMax(xs.tail, xs.head)
  }
}
object Test extends App {
  val testSum = Lists.sum(List(1, 2, 3))
  val testMax = Lists.max(List(1, 2, 3))
}
