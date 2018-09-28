package nl.debijenkorf.snowplow.streams

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64

import akka.stream.alpakka.googlecloud.pubsub.PubSubMessage
import akka.stream.stage.{GraphStage, GraphStageLogic, InHandler}
import akka.stream.{Attributes, Inlet, SinkShape}
import com.typesafe.scalalogging.LazyLogging
import nl.debijenkorf.snowplow.serializers.GzipSerializer
import nl.debijenkorf.snowplow.storage.Status.{UploadFailed, UploadSuccess}
import nl.debijenkorf.snowplow.storage.StorageProvider

class CloudStorageSink(storage: StorageProvider)
  extends GraphStage[SinkShape[Seq[PubSubMessage]]] with LazyLogging {

  val in: Inlet[Seq[PubSubMessage]] = Inlet("GoogleStorageWriter")
  override val shape: SinkShape[Seq[PubSubMessage]] = SinkShape(in)

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      override def preStart(): Unit = pull(in)

      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          val messages = grab(in)
            .map(msg => new String(Base64.getDecoder.decode(msg.data)) + "\n")
          writeToStorage(messages)
          pull(in)
        }
      })

    }

  private def writeToStorage(messages: Seq[String]): Unit = {
    logger.info(s"writing ${messages.length} rows to storage")
    val compressed = GzipSerializer.compress(messages)
    storage.put(generateFileName, compressed) match {
      case UploadSuccess =>
      case UploadFailed(error) => logger.error(error)
    }
  }

  private def generateFileName: String = {
    val dtf = DateTimeFormatter.ofPattern("yyyy-mm-dd-HH-mm-ss")
    val current = LocalDateTime.now().format(dtf)
    s"$current-snowplow-enriched.gz"
  }

}