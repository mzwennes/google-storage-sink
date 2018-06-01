package nl.debijenkorf.google.consumer

import java.io.Closeable

import com.spotify.google.cloud.pubsub.client.{Pubsub, Puller}
import com.spotify.google.cloud.pubsub.client.Puller.MessageHandler
import nl.debijenkorf.google.CustomConfig

private class PubSubConsumer(config: CustomConfig, handler: MessageHandler) extends Consumer {
  override def pull(): Closeable = {
    val client = Pubsub.builder().build()
    Puller.builder()
      .pubsub(client)
      .project(config.projectId)
      .subscription(config.subscriptionName)
      .concurrency(config.threadConcurrency)
      .messageHandler(handler)
      .build()
  }
}