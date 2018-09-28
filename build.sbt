name := "google-storage-loader"
version := "0.2"
scalaVersion := "2.12.6"
assemblyJarName in assembly := s"${name.value}-${version.value}.jar"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime,
  "com.typesafe.akka" %% "akka-actor" % "2.5.16",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "com.github.scopt" %% "scopt" % "3.7.0",
  "com.google.cloud" % "google-cloud-storage" % "1.31.0",
  "com.lightbend.akka" %% "akka-stream-alpakka-google-cloud-pub-sub" % "0.20"
)

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"
)
