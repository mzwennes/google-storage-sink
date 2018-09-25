package nl.debijenkorf.snowplow.config

class ConfigurationParser {

  private val parser = new scopt.OptionParser[Configuration]("gss") {
    head("google-storage-sink", "v0.1")

    opt[String]('p', "project").action((x, c) => c
      .copy(projectId = x)).text("Google Project ID")
      .required().withFallback(() => sys.env("GOOGLE_PROJECT_ID"))

    opt[String]('t', "topic").action((x, c) => c
      .copy(topicId = x)).text("Google Pub/Sub topic name")
      .required().withFallback(() => sys.env("GOOGLE_TOPIC_ID"))

    opt[String]('s', "sub").action((x, c) => c
      .copy(subscriptionId = x)).text("Google Pub/Sub subscription name")
      .required().withFallback(() => sys.env("GOOGLE_SUBSCRIPTION_ID"))

    opt[String]('b', "bucket").action((x, c) => c
      .copy(bucketName = x)).text("Google Storage bucket name")
      .required().withFallback(() => sys.env("GOOGLE_BUCKET_NAME"))

    opt[Int]('m', "max").action((x, c) => c
      .copy(maxRecords = x)).text("Max records per generated file")
      .required().withFallback(() => sys.env("MAX_RECORDS_IN_FILE") toInt)

    opt[String]('a', "auth").action((x, c) => c
      .copy(auth = x)).text("Location of the generated Google secret key")
      .required().withFallback(() => sys.env("SECRET_AUTH_LOCATION"))

    help("help").text("prints this usage text")
  }

  def parse(args: Array[String]): Option[Configuration] =
    parser.parse(args, Configuration())
}
