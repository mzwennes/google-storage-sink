package nl.debijenkorf.google

import java.io.FileWriter

import google.storage.CloudStorage
import nl.debijenkorf.google.storage.GoogleStorage
import org.scalatest.{BeforeAndAfter, FlatSpec}

class CloudStorageSpec extends FlatSpec with BeforeAndAfter {

  var storage: CloudStorage = _

  before {
    storage = new GoogleStorage()
  }

  after {
    try {
      storage.deleteBucket(bucket)
    }
  }

  private val key = "test.txt"
  private val bucket = "debijenkorf-test-bucket"

  "creating/deleting a bucket" should "completely remove the resource" in {
    assert(!storage.bucketExists(bucket))
    storage.createBucket(bucket)
    assert(storage.bucketExists(bucket))

    storage.deleteBucket(bucket)
    assert(!storage.bucketExists(bucket))
  }

  "putting a blob in storage" should "create an object in the defined bucket" in {

    // should return false because bucket does not exist
    assert(!storage.blobExists(bucket, key))
    storage.createBucket(bucket)

    // should return false because key does not exist
    assert(!storage.blobExists(bucket, key))
    createFile(key, "hello, world!")
    storage.put(bucket, key, deleteLocal = true)

    // should return true because key and bucket both exist
    assert(storage.blobExists(bucket, key))
    storage.deleteBucket(bucket)
  }

  private def createFile(path: String, content: String): Unit = {
    val fw = new FileWriter(path, true)
    try {
      fw.write(content)
    }
    finally fw.close()
  }
}
