package nl.debijenkorf.snowplow.serializers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.Base64
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import scala.io.Source
import scala.util.Try

object GzipSerializer {
  def deflate(rows: List[String]): Try[String] = Try {
    val output = new ByteArrayOutputStream()
    val gzip = new GZIPOutputStream(output)
    rows.foreach(record => gzip.write(record.getBytes))
    gzip.close()
    Base64.getEncoder.encodeToString(output.toByteArray)
  }

  def inflate(deflatedTxt: String): Try[String] = Try {
    val bytes = Base64.getDecoder.decode(deflatedTxt)
    val gzip = new GZIPInputStream(new ByteArrayInputStream(bytes))
    Source.fromInputStream(gzip).mkString
  }
}

