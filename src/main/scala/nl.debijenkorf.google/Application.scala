package nl.debijenkorf.google

import com.spotify.google.cloud.pubsub.client.Pubsub
import nl.debijenkorf.google.consumer.ConsumerBuilder

object Application {

  def main(args: Array[String]): Unit = {
    val client: Pubsub = Pubsub.builder().build()
    val config = CustomConfiguration()
    val handler = DefaultHandler(config)

    val consumer = new ConsumerBuilder(config)
      .pubsub(client, handler.default)
    consumer.pull()
  }

}