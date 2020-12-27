import sbt.Keys.libraryDependencies

name := "ConnectFour"

val dottyVersion = "3.0.0-M3"

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotty-simple",
    version := "0.1.0",

    scalaVersion := dottyVersion,
    // local testing
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.3",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.3" % "test"

  )

