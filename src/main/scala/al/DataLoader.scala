package al

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{Dataset, Encoder, Encoders, SparkSession}

case class FileRow(id: String, lat: Double, lon: Double)

class DataLoader(implicit spark: SparkSession) {

  val schema: StructType = Encoders.product[FileRow].schema
  import Model._

  def loadEvents(path: String): Dataset[Event] =
    load(path).map(row => Event(row.id, Coordinates(row.lat, row.lon)))

  def loadAirports(path: String): Dataset[Airport] =
    load(path).map(row => Airport(row.id, Coordinates(row.lat, row.lon)))

  def load(path: String): Dataset[FileRow] = {
    implicit val rowEncoder: Encoder[FileRow] = Encoders.product[FileRow]

    spark.read
      .option("header", "true")
      .schema(schema)
      .csv(path)
      .as[FileRow]
  }
}
