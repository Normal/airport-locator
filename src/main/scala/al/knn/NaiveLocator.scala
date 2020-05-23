package al.knn

import al.geo.GeoUtils
import al.{Airport, Event, SearchResult}
import org.apache.spark.sql.Dataset

/**
  * Brute-force algorithm.
  * If N - number of airports
  * search works in O (N)
  */
class NaiveLocator(airports: Dataset[Airport])
                   extends Locator with Serializable {
  /**
    * it supposes airports dataset is small enough to fit single machine memory
    */
  val airportList: Array[Airport] = airports.collect()

  override def search(events: Dataset[Event]): Dataset[SearchResult] = {
    import al.Model.resultEncoder

    events.map(event => {
      val airportToDistance = airportList
        .map(a => {
          val distance = GeoUtils.haversine(
            a.coord.lat,
            a.coord.lon,
            event.coord.lat,
            event.coord.lon)

          (event.id, a.iata, distance)
        })

      val (eventId, airportId, distance) = airportToDistance
        .minBy(_._3)

      SearchResult(eventId, airportId)
    })
  }
}
