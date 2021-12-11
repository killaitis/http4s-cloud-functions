package de.killaitis.http4s

import cats.effect.IO
import cats.effect.unsafe.IORuntime
import com.google.cloud.functions.{HttpFunction, HttpRequest, HttpResponse}
import fs2.io.{readInputStream, writeOutputStream}
import fs2.Stream
import org.http4s.Status.InternalServerError
import org.http4s.{Header, Headers, HttpApp, Method, Request, Response, Uri}
import org.typelevel.ci.CIString

import scala.jdk.CollectionConverters._

class Http4sCloudFunction(
                           val httpApp: HttpApp[IO],
                           val errorHandler: Throwable => Response[IO] = e => Response[IO](status = InternalServerError).withEntity(e.getMessage),
                           val chunkSize: Int = 1024)
  extends HttpFunction {

  implicit val runtime: IORuntime = IORuntime.global

  override def service(httpRequest: HttpRequest, httpResponse: HttpResponse): Unit = {
    val httpHandler =
      for {
        request   <- fromRequest(httpRequest)
        response  <- httpApp.run(request).handleError(errorHandler)
        _         <- toResponse(httpResponse)(response)
      } yield ()

    httpHandler
      .unsafeRunSync()
  }


  private def fromRequest(request: HttpRequest): IO[Request[IO]] =
    for {
      method  <- IO.fromEither(Method.fromString(request.getMethod))
      uri     <- IO.fromEither(Uri.fromString(request.getUri))
      headers = requestHeaders(request)
      body    = requestBody(request)
    } yield Request(method = method, uri = uri, headers = headers, body = body)

  private def requestBody(request: HttpRequest): Stream[IO, Byte] =
    readInputStream(fis = IO.blocking(request.getInputStream), chunkSize = chunkSize, closeAfterUse = true)

  private def requestHeaders(request: HttpRequest): Headers =
    Headers(
      request.getHeaders.asScala.view
        .mapValues(_.asScala.mkString(","))
        .map { case (k, v) => Header.Raw(CIString(k), v) }
        .toList
    )

  private def toResponse(httpResponse: HttpResponse)(response: Response[IO]): IO[Unit] =
    for {
      _ <- IO.blocking {
        httpResponse.setStatusCode(response.status.code)
        response.headers.foreach(h => httpResponse.appendHeader(h.name.toString, h.value))
      }
      fos = IO.delay(httpResponse.getOutputStream)
      _ <- response.body.through(writeOutputStream(fos)).compile.drain
    } yield ()

}
