name := "snapptrip-standards"
version := "0.1"
scalaVersion := "2.12.7"

lazy val slickProject =
  project
    .in(file("slick-standards"))

lazy val monadProject =
  project
    .in(file("monad-standards"))

lazy val root =
  project
    .in(file("."))
    .dependsOn(slickProject)
    .dependsOn(monadProject)