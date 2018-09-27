package nl.debijenkorf.snowplow

import nl.debijenkorf.snowplow.serializers.GzipSerializer
import org.scalatest.FlatSpec


class GzipSerializerSpec extends FlatSpec {

  private val gzip = GzipSerializer

  "a list of str" should "be inflated and deflated to the same val" in {
    val deflated = gzip.deflate(List("a", "b", "c"))
    assert(deflated.isSuccess)
    assert(gzip.inflate(deflated.get).get == "abc")
  }

  "an empty str" should "not give any errors when inflating/deflating" in {
    val deflated = gzip.deflate(List[String]())
    assert(deflated.isSuccess)
    assert(gzip.inflate(deflated.get).get == "")
  }

  "strange characters" should "be correctly inflated/deflated" in {
    val deflated = gzip.deflate(List("r�^wefwefwef"))
    assert(deflated.isSuccess)
    assert(gzip.inflate(deflated.get).get == "r�^wefwefwef")
  }
}
