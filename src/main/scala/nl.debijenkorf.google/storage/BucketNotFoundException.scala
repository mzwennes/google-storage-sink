package nl.debijenkorf.google.storage

final case class BucketNotFoundException(
  private val message: String = "",
  private val cause: Throwable = None.orNull) extends Exception(message, cause)