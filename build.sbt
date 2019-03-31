import Dependencies._

ThisBuild / scalaVersion     := "2.12.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.atamaniv"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "Crime",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.5.21",
      "com.typesafe.akka" %% "akka-stream" % "2.5.21",
      "org.apache.spark" %% "spark-core" % "2.4.0",
      "org.apache.spark" %% "spark-sql" % "2.4.0",
      "com.typesafe.akka" %% "akka-testkit" % "2.5.21" % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.21" % Test,
      scalaTest % Test,
    )
  )
