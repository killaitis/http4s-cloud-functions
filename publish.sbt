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

ThisBuild / description := "Some description about your project."
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/killaitis/http4s-cloud-functions"))

// Remove all additional repository other than Maven Central from POM
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := {
  val nexus = "https://s01.oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true

ThisBuild / publishTo := sonatypePublishToBundle.value

ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

ThisBuild / publishConfiguration := publishConfiguration.value.withOverwrite(true)
ThisBuild / publishLocalConfiguration := publishLocalConfiguration.value.withOverwrite(true)
