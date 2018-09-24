package nl.debijenkorf.snowplow.storage

import java.io.{File, FileInputStream}
import java.nio.file.Paths

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.{Blob, BlobId, BlobInfo, Storage, StorageOptions}
import nl.debijenkorf.snowplow.storage.Status.{UploadFailed, UploadStatus, UploadSuccess}


case class GoogleStorage(secretKeyPath: String, bucketName: String)
  extends StorageProvider {

  private val credentials = GoogleCredentials.fromStream(new FileInputStream(secretKeyPath))

  private val client = StorageOptions.newBuilder()
    .setCredentials(credentials)
    .build()
    .getService

  override def put(file: File): UploadStatus = {
    createBlob(bucketName, file) match {
      case Right(_) => UploadSuccess
      case Left(a) => UploadFailed(a.getMessage)
    }
  }

  override def get(key: String): Option[File] = {
    val blobId = BlobId.of(bucketName, key)
    val a: Blob = client.get(blobId)
    a.downloadTo(Paths.get(key))
    Some(new File(key))
  }

  private def createBlob(bucket: String, file: File): Either[BucketNotFoundException, Blob] = {
    val blobId = BlobId.of(bucket, file.getPath)
    val blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build
    if (bucketExists(bucket)) Right(client.create(blobInfo, new FileInputStream(file)))
    else Left(BucketNotFoundException(s"$bucket does not exist"))
  }

  private def bucketExists(bucket: String): Boolean =
    client.get(bucket, Storage.BucketGetOption.fields()) != null

}

