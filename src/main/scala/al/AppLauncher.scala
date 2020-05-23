package al

import al.geo.{WgsToECEF, WgsToFlatEarth}
import al.knn.{KdTreeLocator, NaiveLocator, RtreeLocator}
import com.typesafe.config.ConfigFactory
import grizzled.slf4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.serializer.KryoSerializer
import org.apache.spark.sql.{Dataset, SparkSession}
import org.datasyslab.geospark.serde.GeoSparkKryoRegistrator

object AppLauncher {

  val logger: Logger = Logger[this.type]

  def main(args: Array[String]): Unit = {

    val config = ConfigFactory.load

    val conf = new SparkConf().setAppName("Airport locator")
    conf.set("spark.serializer", classOf[KryoSerializer].getName)
    conf.set("spark.kryo.registrator", classOf[GeoSparkKryoRegistrator].getName)

    implicit val spark: SparkSession = SparkSession.builder().master("local[*]").config(conf).getOrCreate()
    val dataLoader = new DataLoader()

    val airports: Dataset[Airport] = dataLoader.loadAirports(config.getString("input.airports"))
    val events: Dataset[Event] = dataLoader.loadEvents(config.getString("input.events"))

    val locator = config.getString("locator") match {
      case "kdtree" => new KdTreeLocator(airports, WgsToECEF)
      case "rtree" => new RtreeLocator(airports, WgsToFlatEarth)
      case "naive" => new NaiveLocator(airports)
      case _ => throw new RuntimeException("Unknown locator type")
    }

    val results = locator.search(events)
    results.write.csv(config.getString("output"))
  }
}
