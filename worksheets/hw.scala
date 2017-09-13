// $ sbt
// $ > ~ run // to monitor source change

object Coursera {
  def main(args: Array[String]) = {
    println(stringToNum(x => s"$x is my age")(19))
//    println(s"Simple product is ${simpleProduct((x, y) => x * y)(7, 2)}")
//    println(s"Product is ${product(x => x*x)(3, 4)}")
//    println(s"Factorial is ${factorial(5)}")
  }
  def simpleProduct(f: (Int, Int) => Int)(a: Int, b: Int): Int =
    // A trivial currying solution
    f(a, b)

  def product(f: Int => Int)(a: Int, b: Int): Int =
    if (a > b) 1
    else f(a) * product(f)(a + 1, b)

  def factorial(n: Int) = product(x => x)(1, n)
  def mapReduce(f: Int => Int, combine: (Int, Int) => Int, zero: Int)(a: Int, b: Int): Int =
    if (a > b) zero
    else combine(f(a), mapReduce(f, combine, zero)(a+1, b))

  // Currying evaluates from right to left
  def stringToNum(f: Int => String)(a: Int): String =
    if (a > 18) f(a) else "Not old enough"
}
