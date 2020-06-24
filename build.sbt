name := """merchant"""
organization := "com.merchant"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.2"

libraryDependencies += guice
libraryDependencies ++= Seq(
  "com.h2database" % "h2" % "1.4.185",
  //Compile-time configuration
  "com.github.pureconfig" %% "pureconfig" % "0.12.1"
)
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.1" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.merchant.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.merchant.binders._"
