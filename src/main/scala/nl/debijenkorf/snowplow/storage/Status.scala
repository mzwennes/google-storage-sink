package nl.debijenkorf.snowplow.storage

object Status {
  sealed trait UploadStatus
  case class UploadFailed(error: String) extends UploadStatus
  case object UploadSuccess extends UploadStatus
}
