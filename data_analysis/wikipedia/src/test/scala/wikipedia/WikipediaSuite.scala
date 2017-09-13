package wikipedia

import org.scalatest.{FunSuite, BeforeAndAfterAll}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._

@RunWith(classOf[JUnitRunner])
class WikipediaSuite extends FunSuite with BeforeAndAfterAll {

  def initializeWikipediaRanking(): Boolean =
    try {
      WikipediaRanking
      true
    } catch {
      case ex: Throwable =>
        println(ex.getMessage)
        ex.printStackTrace()
        false
    }

  override def afterAll(): Unit = {
    assert(initializeWikipediaRanking(), " -- did you fill in all the values in WikipediaRanking (conf, sc, wikiRdd)?")
    import WikipediaRanking._
    sc.stop()
  }

  // Conditions:
  // (1) the language stats contain the same elements
  // (2) they are ordered (and the order doesn't matter if there are several languages with the same count)
  def assertEquivalentAndOrdered(given: List[(String, Int)], expected: List[(String, Int)]): Unit = {
    // (1)
    assert(given.toSet == expected.toSet, "The given elements are not the same as the expected elements")
    // (2)
    assert(
      !(given zip given.tail).exists({ case ((_, occ1), (_, occ2)) => occ1 < occ2 }),
      "The given elements are not in descending order"
    )
  }

  test("'occurrencesOfLang' should work for (specific) RDD with one element") {
    assert(initializeWikipediaRanking(), " -- did you fill in all the values in WikipediaRanking (conf, sc, wikiRdd)?")
    import WikipediaRanking._
    val rdd = sc.parallelize(Seq(WikipediaArticle("title", "Java Jakarta")))
    val res = (occurrencesOfLang("Java", rdd) == 1)
    assert(res, "occurrencesOfLang given (specific) RDD with one element should equal to 1")
  }

  test("the distinct languages that are assigned to each language should add up to all languages that have a match") {
    assert(initializeWikipediaRanking(), " blah")
    import WikipediaRanking._
    val rdd = sc.parallelize(Seq(WikipediaArticle("title", "Java Jakarta"), WikipediaArticle("title", "Java JavaScript")))
    val res = occurrencesOfLang("Java", rdd) == 2
    val resJs = occurrencesOfLang("JavaScript", rdd) == 1
    assert(res && resJs, "occurrencesOfLang given (specific) RDD with one element should equal to 1")

  }

  test("titles with multiple matching names should be double counted") {
    assert(initializeWikipediaRanking(), " blah")
    import WikipediaRanking._
    val rdd = sc.parallelize(Seq(WikipediaArticle("Java Scala", "Java Scala"), WikipediaArticle("fjdk", "dsa")))
    val res = occurrencesOfLang("Java", rdd) == 1
    val resScala = occurrencesOfLang("Scala", rdd) == 1
    assert(res && resScala, "occurrencesOfLang given (specific) RDD with one element should equal to 1")

  }
  test("'rankLangs' should work for RDD with two elements") {
    assert(initializeWikipediaRanking(), " -- did you fill in all the values in WikipediaRanking (conf, sc, wikiRdd)?")
    import WikipediaRanking._
    val langs = List("Scala", "Java")
    val rdd = sc.parallelize(List(WikipediaArticle("1", "Scala is great"), WikipediaArticle("2", "Java is OK, but Scala is cooler")))
    val ranked = rankLangs(langs, rdd)
    val res = ranked.head._1 == "Scala"
    assertEquivalentAndOrdered(ranked, List(("Scala", 2), ("Java", 1)))
    assert(res)
    assert(ranked.size == 2, "Lange size should only equal to those matched")
  }

  test("rankLangs should return a List of languages in original cases") {
    assert(initializeWikipediaRanking(), " -- did you fill in all the values in WikipediaRanking (conf, sc, wikiRdd)?")
    import WikipediaRanking._
    val langs = List("Scala", "Java")
    val rdd = sc.parallelize(List(WikipediaArticle("1", "Scala is great"), WikipediaArticle("Java", "Java is OK, but Scala is cooler")))
    val ranked = rankLangs(langs, rdd)

    assert(ranked == List(("Scala", 2), ("Java", 1)))
  }

  test("'makeIndex' creates a simple index with two entries") {
    assert(initializeWikipediaRanking(), " -- did you fill in all the values in WikipediaRanking (conf, sc, wikiRdd)?")
    import WikipediaRanking._
    val langs = List("Scala", "Java")
    val articles = List(
        WikipediaArticle("1","Groovy is pretty interesting, and so is Erlang"),
        WikipediaArticle("2","Scala and Java run on the JVM"),
        WikipediaArticle("3","Scala is not purely functional")
      )
    val rdd = sc.parallelize(articles)
    val index = makeIndex(langs, rdd)
    val res = index.count() == 2

    assert(res)
    assert(index.map(_._1).collect().toSet == Set("Scala", "Java"))
  }
  test("makeIndex should include the same article to multiple languages") {
    assert(initializeWikipediaRanking(), " -- did you fill in all the values in WikipediaRanking (conf, sc, wikiRdd)?")
    import WikipediaRanking._
    val articles = List(
      WikipediaArticle("k","javascript java")
    )
    val index = makeIndex(List("JavaScript", "Java"), sc.parallelize(articles))
    val js = index.filter(_._1 == "JavaScript")
    val java = index.filter(_._1 == "Java")
    assert(js.count() == 0 && js.values.count() == 0)
    assert(java.count() == 0 && java.values.count() == 0)

  }

  test("'rankLangsUsingIndex' should work for a simple RDD with three elements") {
    assert(initializeWikipediaRanking(), " -- did you fill in all the values in WikipediaRanking (conf, sc, wikiRdd)?")
    import WikipediaRanking._
    val langs = List("Scala", "Java")
    val articles = List(
        WikipediaArticle("1","Groovy is pretty interesting, and so is Erlang"),
        WikipediaArticle("2","Scala and Java run on the JVM"),
        WikipediaArticle("3","Scala is not purely functional")
      )
    val rdd = sc.parallelize(articles)
    val index = makeIndex(langs, rdd)
    val ranked = rankLangsUsingIndex(index)
    val res = ranked.head._1 == "Scala"
    assert(res)
  }

  test("should include lowercase words") {
    assert(initializeWikipediaRanking(), " -- did you fill in all the values in WikipediaRanking (conf, sc, wikiRdd)?")
    import WikipediaRanking._
    val langs = List("Scala", "Java", "Groovy", "Haskell", "Erlang")
    val articles = List(
      WikipediaArticle("1","groovy is pretty interesting, and so is Erlang"),
      WikipediaArticle("2","scala and Java run on the JVM"),
      WikipediaArticle("3","scala is not purely functional"),
      WikipediaArticle("4","The cool kids like haskell more than java"),
      WikipediaArticle("5","Java is for enterprise developers")
    )
    val rdd = sc.parallelize(articles)
    val ranked = rankLangsReduceByKey(langs, rdd)
    val res = ranked.head._1 == "Java"
    assert(res)
  }

  test("'rankLangsReduceByKey' should work for a simple RDD with four elements") {
    assert(initializeWikipediaRanking(), " -- did you fill in all the values in WikipediaRanking (conf, sc, wikiRdd)?")
    import WikipediaRanking._
    val langs = List("Scala", "Java", "Groovy", "Haskell", "Erlang")
    val articles = List(
        WikipediaArticle("1","Groovy is pretty interesting, and so is Erlang"),
        WikipediaArticle("2","Scala and Java run on the JVM"),
        WikipediaArticle("3","Scala is not purely functional"),
        WikipediaArticle("4","The cool kids like Haskell more than Java"),
        WikipediaArticle("5","Java is for enterprise developers")
      )
    val rdd = sc.parallelize(articles)
    val ranked = rankLangsReduceByKey(langs, rdd)
    val res = ranked.head._1 == "Java"
    assert(res)
  }


}
