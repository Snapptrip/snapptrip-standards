name := "slick-standards"
version := "0.1"
scalaVersion := "2.12.7"

// DATABASE
libraryDependencies += "org.postgresql" % "postgresql" % "42.2.5"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.2.3"

// SLICK EXTENSIONS
libraryDependencies += "com.github.tminglei" %% "slick-pg" % "0.16.3"
libraryDependencies += "com.github.tminglei" %% "slick-pg_joda-time" % "0.16.3"
libraryDependencies += "com.github.tminglei" %% "slick-pg_jts" % "0.16.3"
libraryDependencies += "com.github.tminglei" %% "slick-pg_circe-json" % "0.16.3"
libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1" // required by slick-pg for complex-types parsing

// LOGGING
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"

// TESTING
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.14.0" % Test
libraryDependencies += "org.scalamock" %% "scalamock" % "4.1.0" % Test