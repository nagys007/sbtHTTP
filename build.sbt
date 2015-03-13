name := "sbtHTTP"

version := "1.0"

scalaVersion := "2.11.4"

val spray = "io.spray"

libraryDependencies += spray %%  "spray-json" % "1.3.1"

val sprayVersion = "1.3.2"

libraryDependencies += spray %% "spray-can" % sprayVersion

libraryDependencies += spray %% "spray-routing" % sprayVersion

libraryDependencies += spray %% "spray-caching" % sprayVersion

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.6"
