name := "sample-akka-typed"

version := "0.1"

scalaVersion := "2.12.8"

val akkaVersion = "2.5.22"
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % Test