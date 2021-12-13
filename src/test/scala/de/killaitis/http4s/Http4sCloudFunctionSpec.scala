package de.killaitis.http4s


import cats.effect.IO
import de.killaitis.http4s.utils.CloudFunctionUtils
import org.http4s._
import org.http4s.dsl.io.{QueryParamDecoderMatcher, _}
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.server.Router
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Http4sCloudFunctionSpec extends AnyFlatSpec with Matchers with CloudFunctionUtils {

  import Http4sCloudFunctionSpec._

  behavior of "The Http4sCloudFunction"

  it should "transfer the response body of a GET endpoint" in {
    val request = TestHttpRequest(uri = uri"/hello?name=Scala")
    val response = new TestHttpResponse

    cloudFunction.service(request, response)

    response.statusCode shouldBe Some(Status.Ok.code)
    response.body shouldBe "Hello, Scala."
  }

  it should "transfer the request body of a POST endpoint" in {
    val request = TestHttpRequest(method = "POST", uri = uri"/length", body = "This String contains 35 characters.")
    val response = new TestHttpResponse

    cloudFunction.service(request, response)

    response.statusCode shouldBe Some(Status.Ok.code)
    response.body shouldBe "35"
  }

  it should "transfer incoming and outgoing headers" in {
    val request = TestHttpRequest(uri = uri"/headers", headers = Map("HeaderA" -> "A", "HeaderB" -> "B"))
    val response = new TestHttpResponse

    cloudFunction.service(request, response)

    response.statusCode shouldBe Some(Status.Ok.code)
    response.headers should contain allElementsOf Map("HeaderA" -> List("A"), "HeaderB" -> List("B"))
  }

  it should "handle a 404 error" in {
    val request = TestHttpRequest(uri = uri"/something/else")
    val response = new TestHttpResponse

    cloudFunction.service(request, response)

    response.statusCode shouldBe Some(Status.NotFound.code)
  }

  it should "handle uncaught exceptions and return an InternalServerError" in {
    val request = TestHttpRequest(uri = uri"/hal/openPodBayDoors")
    val response = new TestHttpResponse

    cloudFunction.service(request, response)

    response.statusCode shouldBe Some(Status.InternalServerError.code)
  }
}


object Http4sCloudFunctionSpec {
  object Name extends QueryParamDecoderMatcher[String]("name")

  private val service = HttpRoutes.of[IO] {
    case GET -> Root / "hello" :? Name(name) =>
      Ok(s"Hello, $name.")

    case req @ GET -> Root / "headers" =>
      Ok(s"Here comes the list of headers", req.headers)

    case GET -> Root / "hal" / "openPodBayDoors" =>
      throw new RuntimeException("I'm sorry Dave, I'm afraid I can't do that.")

    case req @ POST -> Root / "length" =>
      Ok(req.bodyText.compile.string.map(s => s.length.toString))
  }

  private val httpApp = Router("/" -> service).orNotFound
  private val cloudFunction = new Http4sCloudFunction(httpApp)
}


