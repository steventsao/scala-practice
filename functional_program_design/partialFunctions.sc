import com.sun.corba.se.impl.orbutil.closure.Future

import scala.util.Try

trait Coin {}

case class Gold() extends Coin {}
case class Silver() extends Coin {}

val pf: PartialFunction[Coin, String] = {
  case Gold() => "A gold coin"
}

println(pf.isDefinedAt(Gold()))
println(pf.isDefinedAt(Silver()))
println(pf(Gold()))
//println(pf(Silver())) // to throw error

val myList: List[String] = List("A", "B")
val yourList: List[String] = List("C", "D")
val lists = myList ::: yourList

val resultValue = for {
  letters <- myList ::: yourList
  title = letters.charAt(0)
} yield {
  (title, letters)
}

println(resultValue)


trait Promise[T] {
  def future: Future[T]
  def complete(result: Try[T]): Unit // to call when Promise is complete
  def tryComplete(result: Try[T]): Boolean
}

trait Actor {
  def preStart(): Unit
  def preRestart(reason: Throwable, message: Option[Any]): Unit
  def postRestart(reason: Throwable): Unit
  def postStop(): Unit
}