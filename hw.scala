object Coursera {
  def main(args: Array[String]) = {
    println(s"Simple product is ${simpleProduct((x, y) => x * y)(7, 2)}")
    println(s"Product is ${product(x => x*x)(4, 3)}")
    println(s"Factorial is ${factorial(5)}")
  }
  def simpleProduct(f: (Int, Int) => Int)(a: Int, b: Int): Int =
    // A trivial currying solution
    f(a, b)

  def product(f: Int => Int)(a: Int, b: Int): Int =
    if (a > b) 1
    else f(a) * product(f)(a + 1, b)

  def factorial(n: Int) = product(x => x)(1, n)
}
