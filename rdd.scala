//https://www.coursera.org/learn/scala-spark-big-data/lecture/Ztizq/rdds-sparks-distributed-collection
object RDD {
//  val rdd = spark.textFile("hdfs://...")
  val rdd = List("test")
  val count = rdd
    .flatMap(line => line.split(" ")) // separate lines into words
    .map(word => (word , 1)) // include something to count
    .reduceByKey(_ + _) // sume the 1s into pairs
}