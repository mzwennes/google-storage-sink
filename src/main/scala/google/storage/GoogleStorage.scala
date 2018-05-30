package nl.debijenkorf.google.storage
import java.io.{File, FileInputStream}
import java.nio.file.{Files, Path, Paths}

import com.google.cloud.storage.{Blob, BlobId, BlobInfo, Bucket, BucketInfo, Storage, StorageException, StorageOptions}
import org.slf4j.LoggerFactory

class GoogleStorage extends CloudStorage {

  private val logger = LoggerFactory.getLogger(this.getClass.getName)
  private val storage = StorageOptions.getDefaultInstance.getService

  private def createBlob(bucket: String, localFilePath: String): Either[BucketNotFoundException, Blob] = {
    val blobId = BlobId.of(bucket, localFilePath)
    val blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build
    val fileContent = new FileInputStream(new File(localFilePath))

    if (bucketExists(bucket)) Right(storage.create(blobInfo, fileContent))
    else Left(BucketNotFoundException(s"$bucket does not exist"))

  }

  override def createBucket(bucket: String): Bucket = {
    try {
      storage.create(BucketInfo.of(bucket))
    } catch {
      case _: StorageException => storage.get(bucket)
    }
  }

  override def deleteBucket(bucket: String): Unit = {
    try {
      storage.list(bucket).iterateAll()
        .forEach(blob => blob.delete())
      storage.delete(bucket)
    } catch {
      case _: StorageException => logger.info("cannot delete bucket because it already exists")
    }
  }

  override def bucketExists(bucket: String): Boolean =
    storage.get(bucket, Storage.BucketGetOption.fields()) != null

  override def blobExists(bucket:String, key: String): Boolean =
    storage.get(BlobId.of(bucket, key)) != null

  override def put(bucket: String, localFilePath: String, deleteLocal: Boolean): UploadStatus = {

    val result = createBlob(bucket, localFilePath) match {
      case Right(_) => UploadStatus(success = true, s"file with path $localFilePath written to $bucket")
      case Left(exception) => UploadStatus(success = false, exception.getMessage)
    }

    if (deleteLocal) Files.delete(Paths.get(localFilePath))
    result
  }

  override def get(bucket: String, key: String): Option[File] = {
    val blobId = BlobId.of(bucket, key)
    val a: Blob = storage.get(blobId)
    a.downloadTo(Paths.get(key))
    Some(new File(key))
  }
}
