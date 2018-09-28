package nl.debijenkorf.snowplow.config

import java.nio.file.{Files, Paths}

object ConfigurationParser {

  private val parser = new scopt.OptionParser[Configuration]("gss") {
    head("google-storage-sink", s"v0.2")

    opt[String]('s', "sub").action((x, c) => c
      .copy(subscriptionId = x)).text("Google Pub/Sub subscription name")
      .required().withFallback(() => sys.env("GOOGLE_SUBSCRIPTION_ID"))

    opt[String]('b', "bucket").action((x, c) => c
      .copy(bucketName = x)).text("Google Storage bucket name")
      .required().withFallback(() => sys.env("GOOGLE_BUCKET_NAME"))

    opt[Int]('r', "rows").action((x, c) => c
      .copy(maxRecords = x)).text("Max records per generated file")
      .required().withFallback(() => sys.env("EMPTY_BUFFER_ROWS") toInt)

    opt[Int]('m', "minutes").action((x, c) => c
      .copy(maxMinutes = x)).text("Max minutes before sink empties")
      .required().withFallback(() => sys.env("EMPTY_BUFFER_MINUTES") toInt)

    opt[String]('f', "secret").action((x, c) => c
      .copy(secretLocation = x)).text("Location of Google Service account key")
      .validate(x =>
        if (Files.exists(Paths.get(x))) success
        else failure(s"given file location does not exist: $x"))
      .required().withFallback(() => sys.env("SECRET_KEY_LOCATION"))

    help("help").text("Prints this usage text")
  }

  def parse(args: Array[String]): Option[Configuration] =
    parser.parse(args, Configuration())
}
