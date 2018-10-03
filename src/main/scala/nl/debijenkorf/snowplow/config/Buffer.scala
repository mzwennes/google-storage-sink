package nl.debijenkorf.snowplow.config

import scala.concurrent.duration.FiniteDuration

final case class Buffer(rows: Int, minutes: FiniteDuration)
