package al.knn

import com.thesamet.spatial.{DimensionalOrdering, Metric}

/**
  * aux stuff for KdTreeLocator
  */
object CartesianMetric {

  implicit val metric: Metric[Point, Double] = new Metric[Point, Double] with Serializable {
    override def distance(x: Point, y: Point): Double =
      Math.sqrt(planarDistance(0)(x, y) + planarDistance(1)(x, y) + planarDistance(2)(x, y))

    override def planarDistance(dimension: Int)(x: Point, y: Point): Double = dimension match {
      case 0 => Math.pow(x.crt.x - y.crt.x, 2)
      case 1 => Math.pow(x.crt.y - y.crt.y, 2)
      case 2 => Math.pow(x.crt.z - y.crt.z, 2)
    }
  }

  implicit val dimensionalOrdering: DimensionalOrdering[Point] = new DimensionalOrdering[Point] {
    override def dimensions: Int = 3

    override def compareProjection(dimension: Int)(x: Point, y: Point): Int = dimension match {
      case 0 => x.crt.x.compareTo(y.crt.x)
      case 1 => x.crt.y.compareTo(y.crt.y)
      case 2 => x.crt.z.compareTo(y.crt.z)
    }
  }

}
