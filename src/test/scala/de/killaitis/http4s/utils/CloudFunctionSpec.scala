package de.otto.brain.twain.http4s.utils

import com.google.cloud.functions.{HttpRequest, HttpResponse}
import org.http4s.Uri

import java.io._
import java.util
import java.util.Optional
import scala.jdk.CollectionConverters._
import scala.jdk.OptionConverters._

trait CloudFunctionSpec {

  case class TestHttpRequest(
                         method: String = "GET",
                         uri: Uri,
                         headers: Map[String, String] = Map(),
                         contentType: Option[String] = None,
                         contentLength: Int = 0,
                         characterEncoding: Option[String] = None,
                         body: String = ""
                       ) extends HttpRequest {

    val javaHeaders: util.Map[String, util.List[String]] =
      headers
        .toList
        .groupBy(_._1)
        .view
        .mapValues(group => group.map(p => p._2).asJava)
        .toMap
        .asJava

    val javaQueryParameters: util.Map[String, util.List[String]] =
      uri.query.multiParams
        .view
        .mapValues(values => values.asJava)
        .toMap
        .asJava

    // TODO: not supported yet
    val javaParts: util.Map[String, HttpRequest.HttpPart] = new util.HashMap

    override def getMethod: String = method
    override def getUri: String = uri.toString
    override def getPath: String = uri.path.renderString
    override def getQuery: Optional[String] = if (uri.query.isEmpty) Optional.empty() else Optional.of(uri.query.renderString)
    override def getQueryParameters: util.Map[String, util.List[String]] = javaQueryParameters
    override def getParts: util.Map[String, HttpRequest.HttpPart] = javaParts
    override def getContentType: Optional[String] = contentType.toJava
    override def getContentLength: Long = contentLength
    override def getCharacterEncoding: Optional[String] = characterEncoding.toJava
    override def getInputStream: InputStream = new ByteArrayInputStream(body.getBytes)
    override def getReader: BufferedReader = new BufferedReader(new StringReader(body))
    override def getHeaders: util.Map[String, util.List[String]] = javaHeaders
  }

  class TestHttpResponse extends HttpResponse {

    var statusCode: Option[Int] = None
    var statusMessage: Option[String] = None
    var headers: Map[String, List[String]] = Map.empty[String, List[String]]

    private val out = new ByteArrayOutputStream()
    private val writer = new BufferedWriter(new OutputStreamWriter(out))

    override def setStatusCode(code: Int): Unit = statusCode = Some(code)
    override def setStatusCode(code: Int, message: String): Unit = {
      statusCode = Some(code)
      statusMessage = Some(message)
    }

    override def setContentType(contentType: String): Unit = appendHeader("Content-Type", contentType)
    override def getContentType: Optional[String] = {
      val l = headers.get("Content-Type")
      if (l != null && l.isDefined) Optional.of(l.get.head)
      else Optional.empty()
    }

    override def appendHeader(header: String, value: String): Unit =
      headers = headers + (header -> (value :: headers.getOrElse(header, Nil)))

    override def getHeaders: util.Map[String, util.List[String]] =
      headers
        .view
        .mapValues(values => values.asJava)
        .toMap
        .asJava

    override def getOutputStream: OutputStream = out
    override def getWriter: BufferedWriter = writer

    def body: String = {
      // TODO: this is kind of dirty. :/
      writer.close()
      out.close()

      new String(out.toByteArray)
    }
  }

}


