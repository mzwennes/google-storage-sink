package nl.debijenkorf.google

import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.CompletableFuture

import com.spotify.google.cloud.pubsub.client.Puller.MessageHandler
import com.spotify.google.cloud.pubsub.client.{Message, Puller}
import nl.debijenkorf.google.storage.CloudStorage

case class DefaultHandler(storage: CloudStorage, maxRecords: Int = 10000, bucketName: String) {

  private var records = 0

  /**
    * Anonymous message handler function which is given the the consumer application. This function
    * is executed for each record that is sent to the pipe.
    */
  def default: MessageHandler = (puller: Puller, subscription: String, message: Message, ackId: String) => {
    val data = new String(
      java.util.Base64.getDecoder.decode(message.data())
    )
    firehose(data)
    CompletableFuture.completedFuture(ackId)
  }

  /**
    * Collect all the received records until the given threshold. If threshold
    * is reached then write the events to the given storage location
    */
  private def firehose(record: String): Unit = {
    val filePath = s"$filePrefix-snowplow-enriched.dat"
    val fw = new FileWriter(filePath, true)
    try {
      fw.write(record.concat("\n"))
      records += 1
    }
    finally fw.close()
    if (records >= maxRecords) {
      storage.put(bucketName, filePath, deleteLocal = true)
    }
  }

  private def filePrefix: String = {
    val format = new SimpleDateFormat("yyyy-MM-dd-hh-mm")
    format.format(Calendar.getInstance().getTime)
  }

}