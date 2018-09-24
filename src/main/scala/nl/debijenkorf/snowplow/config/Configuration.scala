package nl.debijenkorf.snowplow.config

case class Configuration(
  projectId: String = "",
  topicId: String = "",
  subscriptionId: String = "",
  bucketName: String = "",
  maxRecords: Int = -1,
  auth: String = ""
)
