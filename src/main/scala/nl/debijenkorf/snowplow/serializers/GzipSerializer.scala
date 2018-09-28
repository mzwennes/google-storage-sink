package nl.debijenkorf.snowplow.serializers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import scala.util.Try

object GzipSerializer {

  def compress(rows: Seq[String]): Array[Byte] = {
    val bos = new ByteArrayOutputStream()
    val gzip = new GZIPOutputStream(bos)
    rows.foreach(record => gzip.write(record.getBytes))
    gzip.close()
    val compressed = bos.toByteArray
    bos.close()
    compressed
  }

  def decompress(compressed: Array[Byte]): Option[String] = Try {
    val inputStream = new GZIPInputStream(new ByteArrayInputStream(compressed))
    scala.io.Source.fromInputStream(inputStream).mkString
  }.toOption
}

