package nl.debijenkorf.snowplow

import nl.debijenkorf.snowplow.consumers.{GoogleConfig, GooglePubSubConsumer, MessageConsumer}
import nl.debijenkorf.snowplow.receivers.SendToGoogleStorage
import nl.debijenkorf.snowplow.serializers.GzipFileSerializer
import nl.debijenkorf.snowplow.storage.GoogleStorage

import scala.concurrent.ExecutionContext.Implicits.global

object ConsumerApplication {
  def main(args: Array[String]): Unit = {
    val config = parseArguments(args)

    val googleConfig = GoogleConfig(
      projectId = config.projectId,
      topicId = config.topicId,
      subscriptionId = config.subscriptionId,
      secretKeyPath = config.secretKeyPath
    )

    val consumer: MessageConsumer = GooglePubSubConsumer(
      gc = googleConfig,
      receiver = SendToGoogleStorage(
        storage = GoogleStorage(config.secretKeyPath, config.bucketName),
        serializer = GzipFileSerializer(),
        maxRows = config.maxRecordsInFile
      )
    )

    consumer.start()
  }

  def parseArguments(args: Array[String]): CustomConfig = {
    val arguments = args.toList
    if (arguments.isEmpty) CustomConfig()
    else CustomConfig(arguments.head)
  }
}
