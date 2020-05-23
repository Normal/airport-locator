package al

import org.apache.spark.sql.{Encoder, Encoders}

case class Coordinates(lat: Double, lon: Double)
case class Cartesian(x: Double, y: Double, z: Double)

case class Event(id: String, coord: Coordinates)
case class Airport(iata: String, coord: Coordinates)

case class SearchResult(id: String, iata: String)

object Model {
  implicit val resultEncoder: Encoder[SearchResult] = Encoders.product[SearchResult]
  implicit val eventEncoder: Encoder[Event] = Encoders.product[Event]
  implicit val airportEncoder: Encoder[Airport] = Encoders.product[Airport]
}