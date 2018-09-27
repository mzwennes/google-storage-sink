package nl.debijenkorf.snowplow.storage

import java.io.File

import nl.debijenkorf.snowplow.storage.Status.UploadStatus

trait StorageProvider {
  def put(key: String, content: String): UploadStatus
  def get(key: String): Option[File]
}
