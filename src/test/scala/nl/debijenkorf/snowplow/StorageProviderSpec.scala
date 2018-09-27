package nl.debijenkorf.snowplow

import java.util.UUID

import nl.debijenkorf.snowplow.storage.GoogleStorage
import org.scalatest.{BeforeAndAfter, FlatSpec}


class StorageProviderSpec extends FlatSpec with BeforeAndAfter {

  private val bucket = GoogleStorage("secret.json", "test-bucket-which-does-not-exist")

  "Getting a file from a bucket" should "return none if the bucket/file don't exist" in {
    val bucketName = s"gss-${UUID.randomUUID()}"
    val storage = GoogleStorage("secret.json", bucketName)
    assert(storage.get("a").isEmpty)
  }

  it should "return none if the bucket exists but the file doesn't" in {
    val blob = bucket.get("wrong.gz")
    assert(blob.isEmpty)
  }

}
