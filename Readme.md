# Run your http4s App as a Google Cloud Function

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
