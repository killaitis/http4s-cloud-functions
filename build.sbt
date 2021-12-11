name := "http4s-cloud-functions"

organization := "de.killaitis"

version := "0.1"

scalaVersion := "2.13.7"
scalacOptions += "-target:jvm-11"

val Http4sVersion = "0.23.6"
val CloudFunctionsVersion = "1.0.4"
val ScalaTestVersion = "3.2.10"

libraryDependencies ++= Seq(

  // Google Cloud Platform
  "com.google.cloud.functions" % "functions-framework-api" % CloudFunctionsVersion,

  // Http4s
  "org.http4s" %% "http4s-dsl" % Http4sVersion,
  "org.http4s" %% "http4s-server" % Http4sVersion,

  // Testing
  "org.scalatest" %% "scalatest" % ScalaTestVersion % Test,

)
