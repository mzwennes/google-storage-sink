package nl.debijenkorf.snowplow.serializers

import java.io._
import java.util.zip.GZIPOutputStream

import scala.concurrent.ExecutionContext

case class GzipFileSerializer(implicit val ec: ExecutionContext)
  extends Serializer {

  override def serialize(rows: List[String], filePath: String): Option[File] = {
    val output = new FileOutputStream(filePath)
    try {
      val writer = new OutputStreamWriter(new GZIPOutputStream(output), "UTF-8")
      try {
        rows.foreach(writer.write)
      } finally {
        writer.close()
      }
    } finally {
      output.close()
    }
    Some(new File(filePath))
  }
}

