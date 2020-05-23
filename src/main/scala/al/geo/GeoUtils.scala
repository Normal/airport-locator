package al.geo

import Math._

object GeoUtils {

  val R = 6372800  //radius in km

  /**
    * Haversine formula for calculating distance between 2 points
    * defined by their lat/lon.
    *
    * Source: https://github.com/acmeism/RosettaCodeData/blob/master/Task/Haversine-formula/Scala/haversine-formula.scala
    */
  def haversine(lat1:Double, lon1:Double, lat2:Double, lon2:Double): Double ={
    val dLat=(lat2 - lat1).toRadians
    val dLon=(lon2 - lon1).toRadians

    val a = pow(sin(dLat/2),2) + pow(sin(dLon/2),2) * cos(lat1.toRadians) * cos(lat2.toRadians)
    val c = 2 * asin(sqrt(a))
    R * c
  }
}
