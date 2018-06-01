package nl.debijenkorf.google.consumer

import com.spotify.google.cloud.pubsub.client.Puller.MessageHandler
import nl.debijenkorf.google.CustomConfig

class ConsumerBuilder(config: CustomConfig) {
  def pubsub(handler: MessageHandler): Consumer = new PubSubConsumer(config, handler)
}
