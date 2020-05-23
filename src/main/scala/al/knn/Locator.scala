package al.knn

import al.{Event, SearchResult}
import org.apache.spark.sql.Dataset

trait Locator {

  def search(events: Dataset[Event]): Dataset[SearchResult]
}
