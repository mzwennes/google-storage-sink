package nl.debijenkorf.snowplow.serializers

import java.io._

trait Serializer {
  def serialize(rows: List[String], filePath: String): Option[File]
  def base64decode(message: String): String = try {
    new String(java.util.Base64.getDecoder.decode(message))
  } catch {
    case _: IllegalArgumentException => ""
  }
}

