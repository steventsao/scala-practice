import week4._

import scala.collection.immutable.Stream.Cons

//https://www.coursera.org/learn/progfun1/lecture/HH19P/lecture-3-3-polymorphism
object nth {
  def nth[T](n: Int, xs: List[T]): T =
    if (n == 0) xs.head
    else nth(n - 1, xs.tail)

  val list = new Cons(1, new Cons(3, new Nil)))
}