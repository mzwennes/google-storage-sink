package nl.debijenkorf.snowplow.storage

import java.io.File

import nl.debijenkorf.snowplow.storage.Status.UploadStatus


trait StorageProvider {
  def put(f: File): UploadStatus
  def get(key: String): Option[File]
}
