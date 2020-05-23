package al.knn
import al.geo.CoordinatesConverter
import al.{Airport, Cartesian, Event, SearchResult}
import com.thesamet.spatial.KDTree
import org.apache.spark.sql.Dataset

case class Point(id: String, crt: Cartesian)

/**
  * Kd-tree algorithm.
  *
  * @see https://github.com/thesamet/kdtree-scala
  */
class KdTreeLocator(airports: Dataset[Airport], converter: CoordinatesConverter)
  extends Locator with Serializable {

  /**
    * it supposes airports dataset is small enough to fit single machine memory
    */
  val airportList: Array[Airport] = airports.collect()

  val kdTree: KDTree[Point] = buildIndex(airportList)

  import CartesianMetric.metric

  private def buildIndex(airportList: Array[Airport]): KDTree[Point] = {

    val seq = airportList.map(a => {
      val crt = converter.convert(a.coord)
      Point(a.iata, crt)
    })

    import CartesianMetric.dimensionalOrdering

    KDTree.fromSeq[Point](seq)
  }

  override def search(events: Dataset[Event]): Dataset[SearchResult] = {
    import al.Model.resultEncoder

    events.map(event => {

      val crt = converter.convert(event.coord)
      val point = Point(event.id, crt)

      val nearest = kdTree.findNearest(point, 1).head
      SearchResult(event.id, nearest.id)
    })
  }
}
