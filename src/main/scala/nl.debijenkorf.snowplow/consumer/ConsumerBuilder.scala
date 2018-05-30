package nl.debijenkorf.snowplow.consumer

import com.spotify.google.cloud.pubsub.client.Pubsub
import com.spotify.google.cloud.pubsub.client.Puller.MessageHandler
import nl.debijenkorf.snowplow.CustomConfiguration

class ConsumerBuilder(config: CustomConfiguration) {
  def pubsub(client: Pubsub, handler: MessageHandler): Consumer = new PubSubConsumer(config, client, handler)
}
