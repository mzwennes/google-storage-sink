package nl.debijenkorf.google.consumer

import java.io.Closeable

trait Consumer {
  def pull(): Closeable
}