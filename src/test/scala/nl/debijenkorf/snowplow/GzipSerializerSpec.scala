package nl.debijenkorf.snowplow

import nl.debijenkorf.snowplow.serializers.GzipSerializer
import org.scalatest.FlatSpec


class GzipSerializerSpec extends FlatSpec {

  private val gzip = GzipSerializer

  "a list of str" should "be inflated and deflated to the same val" in {
    val compressed = gzip.compress(List("a", "b", "c"))
    val decompressed = gzip.decompress(compressed)
    assert(decompressed.isDefined)
    assert(decompressed.get == "abc")
  }

  "an empty str" should "not give any errors when inflating/deflating" in {
    val compressed = gzip.compress(List[String]())
    val decompressed = gzip.decompress(compressed)
    assert(decompressed.isDefined)
    assert(decompressed.get == "")
  }

  "strange characters" should "be correctly inflated/deflated" in {
    val compressed = gzip.compress(List("r�^wefwefwef"))
    val decompressed = gzip.decompress(compressed)
    assert(decompressed.isDefined)
    assert(decompressed.get == "r�^wefwefwef")
  }
}
