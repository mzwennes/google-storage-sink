package nl.debijenkorf.snowplow


import com.typesafe.scalalogging.LazyLogging
import nl.debijenkorf.snowplow.config.ConfigurationParser
import nl.debijenkorf.snowplow.receivers.SendToGoogleStorage
import nl.debijenkorf.snowplow.storage.GoogleStorage

object ConsumerApplication extends LazyLogging {
  def main(args: Array[String]): Unit = {
    val parser = new ConfigurationParser()

    parser.parse(args) match {
      case Some(cfg) =>
        val storageReceiver = SendToGoogleStorage(
          storage = GoogleStorage(cfg.auth, cfg.bucketName),
          maxRows = cfg.maxRecords
        )

        GooglePubSubConsumer(cfg = cfg, receiver = storageReceiver)
          .startup()

      case None => logger.info("not enough parameters supplied, using env variables for non-configured ones..")
    }

  }
}
