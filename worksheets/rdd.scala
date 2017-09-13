//https://www.coursera.org/learn/scala-spark-big-data/lecture/Ztizq/rdds-sparks-distributed-collection

object RDD {
  //  val rdd = spark.textFile("hdfs://...")
  val rdd = List("test")
  val count = rdd
    .flatMap(line => line.split(" ")) // separate lines into words
    .map(word => (word , 1)) // include something to count
    .reduceByKey(_ + _) // sume the 1s into pairs
}


/**
  * transformations are lazy and actions are eager
  *
  * this is used advantageously so that Spark can defer computation of trasnformations until an action is called
  *
  * Spark has performance advantages because there are fewer read/write between iterations.
  *
  * Trying to keep computations in memory
  */

/**
* Logistic regression is an iterative algorithm typically used for classification
* */
object logisticRegression {
  val points = sc.textFile("./test").map(parsePoint)
}
