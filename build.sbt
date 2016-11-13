name := "akka-steps"

version := "1.0"

scalaVersion := "2.11.8"

val akkaV = "2.4.11"

lazy val commonDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.0",
  "org.scalactic" %% "scalactic" % "3.0.0" % "test",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.wix" %% "accord-core" % "0.6"
)

lazy val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaV,
  "com.typesafe.akka" %% "akka-remote" % akkaV,
  "com.typesafe.akka" %% "akka-contrib" % akkaV,
  "com.typesafe.akka" %% "akka-stream" % akkaV,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaV,
  "com.typesafe.akka" %% "akka-http-core" % akkaV,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaV,
  "com.typesafe.akka" %% "akka-http-jackson-experimental" % akkaV,
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaV,
  "com.typesafe.akka" %% "akka-slf4j" % akkaV,
  "com.typesafe.akka" %% "akka-testkit" % akkaV
)

libraryDependencies ++= (commonDependencies ++ akkaDependencies)