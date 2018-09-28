package nl.debijenkorf.snowplow.config

case class Configuration(
  subscriptionId: String = "",
  bucketName: String = "",
  maxRecords: Int = -1,
  maxMinutes: Int = -1,
  secretLocation: String = ""
)