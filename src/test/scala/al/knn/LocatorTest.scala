package al.knn

import al.geo.{WgsToECEF, WgsToFlatEarth}
import al.{Airport, DataLoader, Event, SearchResult}
import com.holdenkarau.spark.testing.{DatasetSuiteBase, SparkSessionProvider}
import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.scalatest.{BeforeAndAfter, FlatSpec}

class LocatorTest extends FlatSpec with DatasetSuiteBase with BeforeAndAfter {

  import org.apache.spark.sql.functions._

  override implicit lazy val spark: SparkSession = SparkSessionProvider.sparkSession

  override def conf: SparkConf = super.conf.
    set("spark.ui.showConsoleProgress", "false")
  var airports: Dataset[Airport] = _
  var events: Dataset[Event] = _

  before {
    val dataLoader = new DataLoader()

    airports = dataLoader.loadAirports("data/airports-test-data.csv")
    events = dataLoader.loadEvents("data/events-test-data.csv")
  }

  "STRtreeLocator" should "produce the same results as NaiveLocator for 2D spatial coords" in {
    val naiveLocator = new NaiveLocator(airports)
    val strTree = new RtreeLocator(airports, WgsToFlatEarth)

    val nr: Dataset[SearchResult] = naiveLocator.search(events)
    val str: Dataset[SearchResult] = strTree.search(events)
    val (total, error)  = showResult(nr, str)

    assert(error / total < 0.05)
  }

  "KdTReeLocator" should "produce the same results as NaiveLocator for 3D spatial coords" in {
    val naiveLocator = new NaiveLocator(airports)
    val kdTreeLocator = new KdTreeLocator(airports, WgsToECEF)

    val nr: Dataset[SearchResult] = naiveLocator.search(events)
    val str: Dataset[SearchResult] = kdTreeLocator.search(events)
    val (total, error)  = showResult(nr, str)

    assert(error / total < 0.10)
  }

  def showResult(r1: Dataset[SearchResult], r2: Dataset[SearchResult]): (Long, Long) = {
    val agg: DataFrame = r1
      .join(r2, Seq("id"))
      .select(r1("id"), r1("iata"), r2("iata"))
      .withColumn("error", when(r1("iata") === r2("iata"), 0).otherwise(1))

    val totalCount: Long = agg.count()
    val errorCount: Long = agg.filter(col("error") === 1).count()

    agg.filter(col("error") === 1).join(events, Seq("id")).show(false)

    println(s"$totalCount / $errorCount")
    (totalCount, errorCount)
  }
}
