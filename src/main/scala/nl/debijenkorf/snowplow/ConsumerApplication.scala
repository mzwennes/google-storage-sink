package nl.debijenkorf.snowplow

import nl.debijenkorf.snowplow.config.ConfigurationParser
import nl.debijenkorf.snowplow.consumers.{GooglePubSubConsumer, MessageConsumer}
import nl.debijenkorf.snowplow.receivers.SendToGoogleStorage
import nl.debijenkorf.snowplow.serializers.GzipFileSerializer
import nl.debijenkorf.snowplow.storage.GoogleStorage

import scala.concurrent.ExecutionContext.Implicits.global

object ConsumerApplication {
  def main(args: Array[String]): Unit = {
    val parser = new ConfigurationParser()

    parser.parse(args) match {
      case Some(cfg) =>
        val storageReceiver = SendToGoogleStorage(
          storage = GoogleStorage(cfg.auth, cfg.bucketName),
          serializer = GzipFileSerializer(),
          maxRows = cfg.maxRecords
        )
        val consumer: MessageConsumer = GooglePubSubConsumer(
          cfg = cfg,
          receiver = storageReceiver
        )
        consumer.start()
      case None => throw new Exception("invalid parameter(s) supplied.")
    }

  }
}
