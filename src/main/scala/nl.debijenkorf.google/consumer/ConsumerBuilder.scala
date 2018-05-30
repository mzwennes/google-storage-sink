package nl.debijenkorf.google.consumer

import com.spotify.google.cloud.pubsub.client.Pubsub
import com.spotify.google.cloud.pubsub.client.Puller.MessageHandler
import nl.debijenkorf.google.CustomConfiguration

class ConsumerBuilder(config: CustomConfiguration) {
  def pubsub(client: Pubsub, handler: MessageHandler): Consumer = new PubSubConsumer(config, client, handler)
}
