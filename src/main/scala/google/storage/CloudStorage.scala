package nl.debijenkorf.google.storage

import java.io.File

import com.google.cloud.storage.Bucket

trait CloudStorage {
  case class UploadStatus(success: Boolean, message: String)
  def createBucket(bucket: String): Bucket
  def deleteBucket(bucket: String): Unit
  def bucketExists(bucket: String): Boolean
  def blobExists(bucket:String, key: String): Boolean
  def put(bucket: String, key: String, deleteLocal: Boolean = false): UploadStatus
  def get(bucket: String, key: String): Option[File]
}
