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

  override def put(key: String, content: String): UploadStatus = {
    createBlob(bucketName, key, content) match {
      case Right(_) => UploadSuccess
      case Left(a) => UploadFailed(a.getMessage)
    }
  }

  override def get(key: String): Option[File] = {
    val blobId = BlobId.of(bucketName, key)
    val blob = client.get(blobId)

    if (blob == null) return None
    else blob.downloadTo(Paths.get(key))

    Some(new File(key))
  }

  private def createBlob(bucket: String, key: String, content: String): Either[BucketNotFoundException, Blob] = {
    val blobId = BlobId.of(bucket, key)
    val blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build

    if (bucketExists(bucket)) Right(client.create(blobInfo, content.getBytes("UTF-8")))
    else Left(BucketNotFoundException(s"$bucket does not exist"))
  }

  private def bucketExists(bucket: String): Boolean =
    client.get(bucket, Storage.BucketGetOption.fields()) != null

}

