package nl.debijenkorf.google

import com.typesafe.config.ConfigFactory
import nl.debijenkorf.google.storage.{CloudStorage, GoogleStorage}

case class CustomConfig(
  projectId: String,
  subscriptionName: String,
  threadConcurrency: Int,
  storageOption: CloudStorage,
  bucketName: String,
  maxRecordsInFile: Int,
)

object CustomConfig {
  def apply(): CustomConfig = {
    val raw = ConfigFactory.load()
    CustomConfig(
      projectId = raw.getString("google.project.id"),
      subscriptionName = raw.getString("google.subscription.name"),
      threadConcurrency = raw.getInt("google.subscription.threads"),
      bucketName = raw.getString("google.storage.bucket"),
      maxRecordsInFile = raw.getInt("google.storage.max-records"),
      storageOption = storage(raw.getString("application.storage.type"))
    )
  }

  private def storage(name: String): CloudStorage = name match {
    case "google" => new GoogleStorage
    case _ => throw new NotImplementedError("Only Google Storage is implemented for now.")
  }
}