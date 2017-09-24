object Stream {
  def cons[T](hd: T, tl: => Stream[T]) = new Stream[T] {
    override def isEmpty = false
    override def head = hd
    override def tail = tl
    def tailDefined = false
  }
  val empty = new Stream[Nothing] {
    override def isEmpty = true
    override def head = throw new NoSuchElementException("empty.head")
    override def tail = throw new NoSuchElementException("empty.tail")
    def tailDefined = false
  }
}