package nl.debijenkorf.snowplow.config

import java.nio.file.{Files, Paths}

object ConfigurationParser {

  private val parser = new scopt.OptionParser[Configuration]("gss") {
    head("google-storage-sink")

    opt[String]('s', "sub").action((x, c) => c
      .copy(subscriptionId = x)).text("Google Pub/Sub subscription name")
      .required()

    opt[String]('b', "bucket").action((x, c) => c
      .copy(bucketName = x)).text("Google Storage bucket name")
      .required()

    opt[Int]('r', "rows").action((x, c) => c
      .copy(maxRecords = x)).text("Max records per generated file (default: 1000 rows)")
      .required().withFallback(() => 1000)

    opt[Int]('m', "minutes").action((x, c) => c
      .copy(maxMinutes = x)).text("Max minutes before sink empties (default: 60 minutes)")
      .required().withFallback(() => 60)

    opt[String]('f', "secret").action((x, c) => c
      .copy(secretLocation = x)).text("Location of Google Service account key (default: /etc/secret.json)")
      .validate(x =>
        if (Files.exists(Paths.get(x))) success
        else failure(s"given file location does not exist: $x"))
      .required().withFallback(() => "/etc/secret.json")
  }

  def parse(args: Array[String]): Option[Configuration] =
    parser.parse(args, Configuration())
}
