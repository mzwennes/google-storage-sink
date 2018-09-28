package nl.debijenkorf.snowplow.storage

import java.io.File

import nl.debijenkorf.snowplow.storage.Status.UploadStatus

trait StorageProvider {
  def put(key: String, data: Array[Byte]): UploadStatus
  def get(key: String): Option[File]
}
