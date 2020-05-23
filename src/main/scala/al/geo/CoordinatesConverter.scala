package al.geo

import al.{Cartesian, Coordinates}
import com.vividsolutions.jts.geom.Coordinate
import org.geotools.geometry.jts.JTS
import org.geotools.referencing.crs.DefaultGeographicCRS

trait CoordinatesConverter extends Serializable {

  def convert(coordinates: Coordinates): Cartesian
}

/**
  * WGS84 to 3D cartesian coordinates.
  */
object WgsToECEF extends CoordinatesConverter {

  import org.geotools.referencing.CRS
  import org.geotools.referencing.crs.DefaultGeocentricCRS

  private val wgs84 = DefaultGeographicCRS.WGS84
  private val cartesian = DefaultGeocentricCRS.CARTESIAN
  private val mathTransform = CRS.findMathTransform(wgs84, cartesian, true)

  override def convert(coordinates: Coordinates): Cartesian = {
    val to = new Coordinate()
    JTS.transform(new Coordinate(coordinates.lon, coordinates.lat), to, mathTransform)
    Cartesian(to.x, to.y, to.z)
  }
}

/**
  * 2D Earth representation https://epsg.io/3857.
  */
object WgsToFlatEarth extends CoordinatesConverter {

  import org.geotools.referencing.CRS

  private val wgs84 = DefaultGeographicCRS.WGS84
  private val flatEarth = CRS.decode("EPSG:3857")
  private val mathTransform = CRS.findMathTransform(wgs84, flatEarth, true)

  override def convert(coordinates: Coordinates): Cartesian = {
    val to = new Coordinate()
    JTS.transform(new Coordinate(coordinates.lon, coordinates.lat), to, mathTransform)
    Cartesian(to.x, to.y, 0d)
  }
}