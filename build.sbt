val Http4sVersion = "0.23.27"
val CloudFunctionsVersion = "1.1.0"
val ScalaTestVersion = "3.2.18"

organization := "de.killaitis"
organizationName := "Andreas Killaitis"
organizationHomepage := Some(url("http://www.github.com/killaitis/"))

name := "http4s-cloud-functions"
version := {
  val Tag = "refs/tags/(.*)".r
  sys.env.get("CI_VERSION")
    .collect { case Tag(tag) => tag.stripPrefix("v") }
    .getOrElse("0.0.1-SNAPSHOT")
}

versionScheme := Some("semver-spec")

scalaVersion := "2.13.14"
crossScalaVersions := Seq(scalaVersion.value, "3.3.4")

libraryDependencies ++= Seq(
  // Google Cloud Platform
  "com.google.cloud.functions" % "functions-framework-api" % CloudFunctionsVersion,

  // Http4s
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  "org.http4s" %% "http4s-server" % Http4sVersion,

  // Testing
  "org.scalatest" %% "scalatest" % ScalaTestVersion % Test
)

