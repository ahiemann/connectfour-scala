name := "ConnectFour"

version := "0.1"

scalaVersion := "2.13.3"

// local testing
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.2"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2"
libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.5.23",
    "com.typesafe.akka" %% "akka-testkit" % "2.5.23" % Test
  )
}





