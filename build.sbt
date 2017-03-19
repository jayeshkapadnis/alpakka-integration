name := "alpakka-akka-kafka-integration"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.13",
  "com.typesafe.akka" %% "akka-stream" % "2.4.17",
  "com.typesafe.akka" %% "akka-stream-contrib-xmlparser" % "0.6",
  "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.0",
  "commons-io" % "commons-io" % "2.3",
  "de.odysseus.staxon" % "staxon" % "1.3"
)
    