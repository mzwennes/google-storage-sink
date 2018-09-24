package nl.debijenkorf.snowplow.consumers

import java.io.FileInputStream

import com.google.api.core.ApiService
import com.google.api.gax.core.{FixedCredentialsProvider, InstantiatingExecutorProvider}
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.pubsub.v1.{MessageReceiver, Subscriber}
import com.google.pubsub.v1.ProjectSubscriptionName
import com.typesafe.scalalogging.LazyLogging

case class GooglePubSubConsumer(gc: GoogleConfig, receiver: MessageReceiver)
  extends MessageConsumer with LazyLogging {

  private var subscriber: Subscriber = _

  private val executorProvider = InstantiatingExecutorProvider.newBuilder()
      .setExecutorThreadCount(1)
      .build()

  private val listener = new ApiService.Listener() {
    override def failed(from: ApiService.State, failure: Throwable): Unit = {
      logger.error(failure.getMessage)
    }
  }

  override def start(): Unit = try {
    logger.info("creating subscription or retrieving existing one..")
    val subscription = getOrCreateSubscription(gc.projectId, gc.topicId, gc.subscriptionId)

    logger.info("checking Google credentials and handling authentication..")
    val credentials = GoogleCredentials.fromStream(new FileInputStream(gc.secretKeyPath))

    logger.info("adding subscribers to the topic..")
    subscriber = Subscriber.newBuilder(subscription, receiver)
      .setExecutorProvider(executorProvider)
      .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
      .build()

    logger.info("starting Google PubSub consumer..")
    subscriber.startAsync().awaitTerminated()

  } catch {
    case e: Exception => logger.error(e.getMessage)
  }

  private def getOrCreateSubscription(projectId: String, topicId: String, subId: String): ProjectSubscriptionName = {
    val subscription = ProjectSubscriptionName.of(projectId, subId)

    // todo: does not work somehow..

    //val topic = ProjectTopicName.of(projectId, topicId)
    //val pushConfig = PushConfig.getDefaultInstance
    //val ackDeadlineSeconds = 0

    //    try {
    //      val subscriptionAdminClient = SubscriptionAdminClient.create()
    //      subscriptionAdminClient
    //        .createSubscription(subscription, topic, pushConfig, ackDeadlineSeconds)
    //    }
    subscription
  }
}
