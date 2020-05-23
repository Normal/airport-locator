name := "airport-locator"

version := "0.1"

scalaVersion := "2.11.12"

val versions = new {
  def spark = "2.3.0"
}

libraryDependencies ++= Seq(
  //spark binaries
  "org.apache.spark" %% "spark-sql" % versions.spark % "provided",
  "org.apache.spark" %% "spark-hive" % versions.spark % "provided",

  // geospark
  "org.datasyslab" % "geospark" % "1.2.0",

  // kdtree scala implementation
  "com.thesamet" %% "kdtree" % "1.0.4",

  //utils
  "org.clapper" %% "grizzled-slf4j" % "1.0.2",
  "com.typesafe" % "config" % "1.2.1",

  //tests
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.holdenkarau" %% "spark-testing-base" % s"${versions.spark}_0.9.0" % "test"
    excludeAll ExclusionRule(organization = "org.mortbay.jetty")
)

assemblyJarName in assembly := "airport-locator.jar"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF",  xs @ _*) =>
    xs map {_.toLowerCase} match {
      case "services" :: _ :: Nil =>
        MergeStrategy.concat
      case _ => MergeStrategy.discard
    }
  case "application.conf" =>
    MergeStrategy.concat
  case _ =>
    MergeStrategy.first
}

// SparkContext is shared between all tests via SharedSingletonContext
parallelExecution in Test := false

test in assembly := {}