val common = Seq(
  name := "google-storage-loader",
  version := "0.7",
  organization := "debijenkorf.nl",
  scalaVersion := "2.12.6",
  assemblyJarName in assembly := s"${name.value}-${version.value}.jar"
)

val dependencies = Seq(
  "ch.qos.logback" % "logback-classic" % "1.1.3" % Runtime,
  "com.typesafe.akka" %% "akka-actor" % "2.5.16",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "com.github.scopt" %% "scopt" % "3.7.0",
  "com.google.cloud" % "google-cloud-storage" % "1.31.0",
  "com.lightbend.akka" %% "akka-stream-alpakka-google-cloud-pub-sub" % "0.20",
  "org.scalatest" % "scalatest_2.12" % "3.0.5" % "test"
)

val customScalacOptions = Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xfuture",
  "-Xlint",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused-import"
)

val customJavaOptions = Seq(
  "-Xms256m",
  "-Xmx256m"
)

lazy val root = (project in file("."))
  .settings(common)
  .settings(scalacOptions ++= customScalacOptions)
  .settings(javaOptions ++= customJavaOptions)
  .settings(libraryDependencies ++= dependencies)
  .settings(fork in run := true)

