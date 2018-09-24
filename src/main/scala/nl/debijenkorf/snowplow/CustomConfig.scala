package nl.debijenkorf.snowplow

import com.typesafe.config.{Config, ConfigFactory}

case class CustomConfig(
  projectId: String,
  subscriptionId: String,
  topicId: String,
  secretKeyPath: String,
  bucketName: String,
  maxRecordsInFile: Int,
)

object CustomConfig {

  def apply(): CustomConfig =
    convert(ConfigFactory.load())

  def apply(customPath: String): CustomConfig =
    convert(ConfigFactory.parseFile(new java.io.File(customPath)))

  private def convert(raw: Config): CustomConfig =
    CustomConfig(
      projectId = raw.getString("google.project.id"),
      subscriptionId = raw.getString("google.subscription.name"),
      topicId = raw.getString("google.topic.name"),
      secretKeyPath = raw.getString("google.secret.key.path"),
      bucketName = raw.getString("google.storage.bucket"),
      maxRecordsInFile = raw.getInt("google.storage.max-records"),
    )
}