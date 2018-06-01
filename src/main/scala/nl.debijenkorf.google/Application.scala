package nl.debijenkorf.google

import nl.debijenkorf.google.consumer.ConsumerBuilder

object Application {

  def main(args: Array[String]): Unit = {
    val config = CustomConfig()

    val handler = DefaultHandler(
      storage = config.storageOption,
      maxRecords = config.maxRecordsInFile,
      bucketName = config.bucketName
    )

    val consumer = new ConsumerBuilder(config)
      .pubsub(handler.default)

    consumer.pull()
  }

}