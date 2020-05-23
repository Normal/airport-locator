package al.knn
import al.geo.CoordinatesConverter
import al.{Airport, Event, SearchResult}
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence
import com.vividsolutions.jts.geom.{Coordinate, Envelope, GeometryFactory}
import com.vividsolutions.jts.index.strtree.{GeometryItemDistance, STRtree}
import org.apache.spark.sql.Dataset

class StrPoint(val id: String, x: Double, y: Double)
  extends com.vividsolutions.jts.geom.Point(
    new CoordinateArraySequence(Array(new Coordinate(x, y))),
    new GeometryFactory()
  )

/**
  * R-tree based on Sort-Tile-Recursive.
  *
  * @see https://github.com/locationtech/jts/blob/master/modules/core/src/main/java/org/locationtech/jts/index/strtree/STRtree.java
  */
class RtreeLocator(airports: Dataset[Airport], converter: CoordinatesConverter)
  extends Locator with Serializable {

  val strTree = new STRtree(airports.count().intValue())

  /**
    * it supposes airports dataset is small enough to fit single machine memory
    */
  airports.collect().foreach(a => {
    val crt = converter.convert(a.coord)
    strTree.insert(
      new Envelope(new Coordinate(crt.x, crt.y)),
      new StrPoint(a.iata, crt.x, crt.y)
    )
  })

  override def search(events: Dataset[Event]): Dataset[SearchResult] = {
    import al.Model.resultEncoder

    events.map(event => {

      val crt = converter.convert(event.coord)

      val env = new Envelope(new Coordinate(crt.x, crt.y))
      val nearest = strTree
        .kNearestNeighbour(
          env,
          new StrPoint(event.id, crt.x, crt.y),
          new GeometryItemDistance(), 1
        )
        .head
        .asInstanceOf[StrPoint]
      SearchResult(event.id, nearest.id)
    })
  }
}
