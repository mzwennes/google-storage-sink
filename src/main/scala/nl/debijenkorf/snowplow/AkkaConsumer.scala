package nl.debijenkorf.snowplow

import java.io.FileInputStream

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.googlecloud.pubsub.{AcknowledgeRequest, ReceivedMessage}
import akka.stream.scaladsl.{Flow, Sink}
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.typesafe.scalalogging.LazyLogging
import nl.debijenkorf.snowplow.config.ConfigurationParser
import nl.debijenkorf.snowplow.storage.{GoogleStorage, StorageProvider}
import nl.debijenkorf.snowplow.streams.{CloudStorageSink, GooglePubSubSource}

import scala.concurrent.duration._


object AkkaConsumer extends App with LazyLogging {
  implicit val system: ActorSystem = ActorSystem("google-storage-sink")
  implicit val mat: ActorMaterializer = ActorMaterializer()

  ConfigurationParser.parse(args) match {
    case Some(cfg) =>
      val credentials = GoogleCredential.fromStream(new FileInputStream(cfg.secretLocation))

      val source = GooglePubSubSource(
        projectId = credentials.getServiceAccountProjectId,
        clientEmail = credentials.getServiceAccountId,
        primaryKey = credentials.getServiceAccountPrivateKey,
        subscriptionName = cfg.subscriptionId
      )

      val target = GoogleStorage(cfg.bucketName, cfg.secretLocation)
      execute(source, target, cfg.maxRecords, cfg.maxMinutes)
    case None => throw new Exception("incorrect configuration supplied.")
  }

  private def execute(source: GooglePubSubSource,
                      target: StorageProvider,
                      maxRecords: Int,
                      maxMinutes: Int): Unit = {

    val batchAckSink = Flow[ReceivedMessage]
      .map(_.ackId)
      .groupedWithin(maxRecords, maxMinutes.minute)
      .map(AcknowledgeRequest.apply)
      .to(source.acknowledge())

    val storageWriter = source.subscribe()
      .alsoTo(batchAckSink)
      .map(_.message)
      .groupedWithin(maxRecords, maxMinutes.minute)
      .to(Sink.fromGraph(new CloudStorageSink(target)))

    logger.info("starting Google PubSub consumer..")
    storageWriter.run()
  }
}
