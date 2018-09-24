name := "google-storage-loader"
version := "0.1"
scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime,
  "com.typesafe" % "config" % "1.3.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "com.google.cloud" % "google-cloud-pubsub" % "1.45.0",
  "com.google.cloud" % "google-cloud-storage" % "1.31.0",
)

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"
)