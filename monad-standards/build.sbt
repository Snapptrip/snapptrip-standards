name := "monad-standards"
version := "0.1"
scalaVersion := "2.12.7"

// MONAD
libraryDependencies += "org.scala-lang.modules" %% "scala-async" % "0.9.7"
libraryDependencies += "org.typelevel" %% "cats-core" % "1.4.0"

// LOGGING
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

// TESTING
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0" % Test
libraryDependencies += "org.scalamock" %% "scalamock" % "4.1.0" % Test