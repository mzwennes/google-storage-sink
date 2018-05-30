package nl.debijenkorf.snowplow.consumer

import java.io.Closeable

trait Consumer {
  def pull(): Closeable
}