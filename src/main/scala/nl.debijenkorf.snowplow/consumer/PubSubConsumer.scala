package nl.debijenkorf.snowplow.consumer

import java.io.Closeable

import com.spotify.google.cloud.pubsub.client.{Pubsub, Puller}
import com.spotify.google.cloud.pubsub.client.Puller.MessageHandler
import nl.debijenkorf.snowplow.CustomConfiguration

private class PubSubConsumer(config: CustomConfiguration, client: Pubsub, handler: MessageHandler) extends Consumer {
  override def pull(): Closeable = Puller.builder()
    .pubsub(client)
    .project(config.projectId)
    .subscription(config.subscriptionName)
    .concurrency(config.threadConcurrency)
    .messageHandler(handler)
    .build()
}