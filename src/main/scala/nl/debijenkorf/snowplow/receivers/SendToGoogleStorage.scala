package nl.debijenkorf.snowplow.receivers

import java.nio.file.{Files, Paths}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentLinkedQueue

import com.google.cloud.pubsub.v1.{AckReplyConsumer, MessageReceiver}
import com.google.pubsub.v1.PubsubMessage
import com.typesafe.scalalogging.LazyLogging
import nl.debijenkorf.snowplow.serializers.GzipSerializer
import nl.debijenkorf.snowplow.storage.Status.{UploadFailed, UploadSuccess}
import nl.debijenkorf.snowplow.storage.StorageProvider

import scala.collection.JavaConverters._
import scala.util.{Failure, Success}


case class SendToGoogleStorage(storage: StorageProvider, maxRows: Int)
  extends MessageReceiver with LazyLogging {

  Runtime.getRuntime.addShutdownHook(new Thread(){
    override def run(): Unit = {
      logger.info("received SIGTERM message, emptying buffer")
      emptyBuffer()
    }
  })

  private val messages = new ConcurrentLinkedQueue[String]()

  override def receiveMessage(message: PubsubMessage, consumer: AckReplyConsumer): Unit = {
    messages.offer(message.getData.toStringUtf8 + "\n")

    if (messages.size() >= maxRows) {
      val fileName = generateFileName
      logger.info(s"there are ${messages.size()} rows in buffer, emptying to storage provider..")
      emptyBuffer()
      Files.delete(Paths.get(fileName))
    }

    consumer.ack()
  }

  private def generateFileName: String = {
    val dtf = DateTimeFormatter.ofPattern("yyyy-mm-dd-HH-mm-ss")
    val current = LocalDateTime.now().format(dtf)
    s"$current-snowplow-enriched.gz"
  }

  private def emptyBuffer(): Unit = {
    val serializer = GzipSerializer

    serializer.deflate(messages.asScala.toList) match {
      case Success(value) => storage.put(generateFileName, value) match {
        case UploadSuccess =>
        case UploadFailed(error) => logger.error(error)
      }
      case Failure(error) => logger.error(error.getMessage)
    }
  }

}
