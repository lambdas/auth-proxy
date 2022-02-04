ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "lambdas"

val AkkaVersion = "2.6.18"
val AkkaHttpVersion = "10.2.7"

lazy val root = (project in file("."))
  .settings(
    name := "endpoint-auth",
    scalacOptions ++= List(),
    libraryDependencies ++= List(
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe" % "config" % "1.4.1",
      "ch.qos.logback" % "logback-classic" % "1.2.3",
      "org.scalatest" %% "scalatest" % "3.2.10" % Test,
    ),
  )
