package nl.debijenkorf.snowplow

import com.typesafe.config.ConfigFactory
import nl.debijenkorf.snowplow.storage.{CloudStorage, GoogleStorage}

case class CustomConfiguration(
  projectId: String,
  subscriptionName: String,
  threadConcurrency: Int,
  storageOption: CloudStorage,
  bucketName: String,
  maxRecordsInFile: Int,
)

object CustomConfiguration {
  def apply(): CustomConfiguration = {
    val raw = ConfigFactory.load()
    CustomConfiguration(
      projectId = raw.getString("google.project.id"),
      subscriptionName = raw.getString("google.subscription.name"),
      threadConcurrency = raw.getInt("google.subscription.threads"),
      storageOption = new GoogleStorage, //todo: switch based on value in conf
      bucketName = raw.getString("google.storage.bucket"),
      maxRecordsInFile = raw.getInt("google.storage.max-records")
    )
  }
}