package nl.debijenkorf.snowplow.consumers

trait MessageConsumer {
  def start(): Unit
}
