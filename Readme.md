<a href="https://typelevel.org/cats/"><img src="https://raw.githubusercontent.com/typelevel/cats/c23130d2c2e4a320ba4cde9a7c7895c6f217d305/docs/src/main/resources/microsite/img/cats-badge.svg" height="40px" align="right" alt="Cats friendly" /></a>

# http4s-cloud-functions

Run your http4s Apps as Google Cloud Functions.

[![Test Workflow](https://github.com/killaitis/http4s-cloud-functions/workflows/test/badge.svg)](https://github.com/killaitis/http4s-cloud-functions/actions?query=workflow%3Atest)
[![Release Notes](https://img.shields.io/github/release/killaitis/http4s-cloud-functions.svg?maxAge=3600)](https://github.com/killaitis/http4s-cloud-functions/releases/latest)
[![Maven Central](https://img.shields.io/maven-central/v/de.killaitis/http4s-cloud-functions_2.13)](https://search.maven.org/artifact/de.killaitis/http4s-cloud-functions_2.13) 
[![Apache License 2.0](https://img.shields.io/github/license/killaitis/http4s-cloud-functions.svg?maxAge=3600)](https://www.apache.org/licenses/LICENSE-2.0)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)



## Get started
Add the following to your `build.sbt` file:
```sbt
libraryDependencies += "de.killaitis" %% "http4s-cloud-functions" % Http4sCloudFunctionsVersion
```

You will probably also have to include the [assembly sbt plugin](https://github.com/sbt/sbt-assembly) in your 
`project/plugins.sbt` to build an uber jar (`sbt assembly`): 

```sbt
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "1.1.0")
```

## Example
```scala
// Your HTTP App
object MyCloudFunction {
  val httpApp = Router("/" -> HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
  }).orNotFound
}

// Google Java Cloud Functions need a class without constructor parameters as an entry point.
import cats.effect.unsafe.implicits.global

class MyCloudFunction extends Http4sCloudFunction(MyCloudFunction.httpApp)
```

## Deployment
To deploy the Cloud Function jar, it has to be copied/moved in an otherwise empty directory. Then go to this directory
and run the following:
```bash
gcloud functions deploy my-cloudfunction \
  --entry-point MyCloudFunction \
  --runtime java11 \
  --trigger-http \
  --memory 128MB \
  --allow-unauthenticated \
  --project {your project name} \  
  --region {your region} \
  ...
```
