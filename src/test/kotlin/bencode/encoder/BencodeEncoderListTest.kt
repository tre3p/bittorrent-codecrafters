package bencode.encoder

import bencode.encodeToBencode
import org.junit.Test
import kotlin.test.assertContentEquals

class BencodeEncoderListTest {

    @Test
    fun shouldCorrectlyParsePlainList() {
        val list = listOf("hello", 5L, "bencode")
        val expected = "l5:helloi5e7:bencodee".encodeToByteArray()
        val actual = encodeToBencode(list)

        assertContentEquals(expected, actual)
    }

    @Test
    fun shouldCorrectlyParseNestedList() {
        val list = listOf("bencode", listOf(42L, "test"), mapOf("mg" to 42L))
        val expected = "l7:bencodeli42e4:tested2:mgi42eee".encodeToByteArray()
        val actual = encodeToBencode(list)

        assertContentEquals(expected, actual)
    }
}