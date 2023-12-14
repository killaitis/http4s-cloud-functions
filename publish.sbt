ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/killaitis/http4s-cloud-functions"),
    "https://github.com/killaitis/http4s-cloud-functions.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id    = "killaitis",
    name  = "Andreas Killaitis",
    email = "andreas@killaitis.de",
    url   = url("http://www.github.com/killaitis/")
  )
)

ThisBuild / description := "Run http4s Scala apps as a Google Cloud Function"
ThisBuild / licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/killaitis/http4s-cloud-functions"))

publishTo := sonatypePublishToBundle.value
publishMavenStyle := true

pomIncludeRepository := { _ => false }

sonatypeCredentialHost := "s01.oss.sonatype.org"
sonatypeRepository := "https://s01.oss.sonatype.org/service/local"

credentials ++= (for {
  username <- sys.env.get("SONATYPE_USERNAME")
  password <- sys.env.get("SONATYPE_PASSWORD")
} yield Credentials(
  "Sonatype Nexus Repository Manager",
  "s01.oss.sonatype.org",
  username,
  password
)).toList
