package nl.debijenkorf.snowplow.receivers

import java.nio.file.{Files, Paths}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import com.google.cloud.pubsub.v1.{AckReplyConsumer, MessageReceiver}
import com.google.pubsub.v1.PubsubMessage
import com.typesafe.scalalogging.LazyLogging
import nl.debijenkorf.snowplow.serializers.Serializer
import nl.debijenkorf.snowplow.storage.Status.{UploadFailed, UploadSuccess}
import nl.debijenkorf.snowplow.storage.StorageProvider


case class SendToGoogleStorage(storage: StorageProvider, serializer: Serializer, maxRows: Int)
  extends MessageReceiver with LazyLogging {

  private var messages = List.empty[String]

  override def receiveMessage(message: PubsubMessage, consumer: AckReplyConsumer): Unit = {
    messages = message.getData.toStringUtf8 + "\n" :: messages

    if (messages.length >= maxRows) {
      val filename = generateFileName
      logger.info(s"there are ${messages.length} rows in buffer, emptying to storage provider..")
      serializer.serialize(messages, filename) match {
        case Some(f) =>
          storage.put(f) match {
            case UploadSuccess =>
            case UploadFailed(m) => logger.error(m)
          }
          messages = List.empty
          Files.delete(Paths.get(filename))
        case _ => logger.error("cannot write to file")
      }
    }

    consumer.ack()
  }

  private def generateFileName: String = {
    val dtf = DateTimeFormatter.ofPattern("yyyy-mm-dd-HH-mm-ss")
    val current = LocalDateTime.now().format(dtf)
    s"$current-snowplow-enriched.gz"
  }
}
