package nl.debijenkorf.snowplow

import java.io.FileInputStream

import akka.actor.ActorSystem
import akka.stream.alpakka.googlecloud.pubsub.{AcknowledgeRequest, ReceivedMessage}
import akka.stream.scaladsl.{Flow, Keep, Sink}
import akka.stream.{ActorMaterializer, KillSwitches, UniqueKillSwitch}
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.typesafe.scalalogging.LazyLogging
import nl.debijenkorf.snowplow.config.{Buffer, Configuration, ConfigurationParser}
import nl.debijenkorf.snowplow.storage.{GoogleStorage, StorageProvider}
import nl.debijenkorf.snowplow.streams.{CloudStorage, PubSub}

import scala.concurrent.duration._

object AkkaConsumer extends LazyLogging with App {

  implicit val system: ActorSystem = ActorSystem("google-storage-sink")
  implicit val mat: ActorMaterializer = ActorMaterializer()

  ConfigurationParser.parse(args) match {
    case Some(cfg) =>
      val credentials = GoogleCredential.fromStream(new FileInputStream(cfg.secretLocation))
      val (source, target) = init(cfg, credentials)
      val limits = Buffer(cfg.maxRecords, cfg.maxMinutes.minute)
      executeFlow(source, target, limits)
    case None =>
  }

  private def init(cfg: Configuration, creds: GoogleCredential): (PubSub, StorageProvider) = {
    val configString = s"""
      | bucket: ${cfg.bucketName}
      | subscription: ${cfg.subscriptionId}
      | max-rows: ${cfg.maxRecords}
      | max-minutes: ${cfg.maxMinutes}
      | secret-location: ${cfg.secretLocation}
    """.stripMargin.stripLineEnd
    logger.info(s"The following parameters are given to the application $configString")

    val source = PubSub(
      projectId = creds.getServiceAccountProjectId,
      clientEmail = creds.getServiceAccountId,
      primaryKey = creds.getServiceAccountPrivateKey,
      subscriptionName = cfg.subscriptionId
    )
    val target = GoogleStorage(cfg.bucketName, cfg.secretLocation)
    (source, target)
  }

  private def executeFlow(source: PubSub, target: StorageProvider, limit: Buffer): Unit = {
    import collection.JavaConverters._

    val batchAckSink = Flow[ReceivedMessage].map(_.ackId)
      .groupedWithin(limit.rows, limit.minutes)
      .map( acks => AcknowledgeRequest.of(acks.asJava) )
      .to(source.acknowledge())

    val storageSink = Flow[ReceivedMessage]
      .map(_.message)
      .groupedWithin(limit.rows, limit.minutes)
      .to(Sink.fromGraph(new CloudStorage(target)))

    val acknowledgements: UniqueKillSwitch = source.subscribe()
      .viaMat(KillSwitches.single)(Keep.right)
      .to(batchAckSink)
      .run()

    val storage: UniqueKillSwitch = source.subscribe()
      .viaMat(KillSwitches.single)(Keep.right)
      .to(storageSink)
      .run()

    scala.sys.addShutdownHook {
      logger.info("SIGTERM received; shutting down application")
      acknowledgements.shutdown()
      storage.shutdown()
    }

    logger.info("starting Google PubSub consumer..")
  }
}
