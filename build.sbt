lazy val Http4sVersion = "0.23.7"
lazy val CloudFunctionsVersion = "1.0.4"
lazy val ScalaTestVersion = "3.2.10"

ThisBuild / organization := "de.killaitis"
ThisBuild / organizationName := "Andreas Killaitis"
ThisBuild / organizationHomepage := Some(url("http://www.github.com/killaitis/"))

ThisBuild / version := "0.4"
ThisBuild / versionScheme := Some("semver-spec")

lazy val root = (project in file("."))
  .settings(
    name := "http4s-cloud-functions",

    scalaVersion := "2.13.7",
//    scalacOptions ++= Seq("-target:11"),
    crossScalaVersions := Seq("2.13.7", "3.1.0"),

    libraryDependencies ++= Seq(
      // Google Cloud Platform
      "com.google.cloud.functions" % "functions-framework-api" % CloudFunctionsVersion,

      // Http4s
      "org.http4s" %% "http4s-dsl" % Http4sVersion,
      "org.http4s" %% "http4s-server" % Http4sVersion,

      // Testing
      "org.scalatest" %% "scalatest" % ScalaTestVersion % Test,
    )
  )
