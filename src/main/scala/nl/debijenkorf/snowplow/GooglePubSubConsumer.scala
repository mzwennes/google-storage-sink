package nl.debijenkorf.snowplow

import java.io.FileInputStream

import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.pubsub.v1.{MessageReceiver, Subscriber}
import com.google.pubsub.v1.ProjectSubscriptionName
import com.typesafe.scalalogging.LazyLogging
import nl.debijenkorf.snowplow.config.Configuration

case class GooglePubSubConsumer(cfg: Configuration, receiver: MessageReceiver) extends LazyLogging {

  private var subscriber: Subscriber = _

  def startup(): Unit = {
    Runtime.getRuntime.addShutdownHook(new Thread(){
      override def run(): Unit = {
        logger.info("received SIGTERM message, stop handling messages")
        subscriber.stopAsync()
      }
    })

    logger.info("creating subscription or retrieving existing one..")
    val subscription = getOrCreateSubscription(cfg.projectId, cfg.topicId, cfg.subscriptionId)

    logger.info("checking Google credentials and handling authentication..")
    val credentials = GoogleCredentials.fromStream(new FileInputStream(cfg.auth))

    logger.info("adding subscribers to the topic..")
    subscriber = Subscriber.newBuilder(subscription, receiver)
      .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
      .build()

    logger.info("starting Google PubSub consumer..")
    subscriber.startAsync().awaitTerminated()
  }

  private def getOrCreateSubscription(projectId: String, topicId: String, subId: String): ProjectSubscriptionName = {
    val subscription = ProjectSubscriptionName.of(projectId, subId)
    subscription
  }

}
