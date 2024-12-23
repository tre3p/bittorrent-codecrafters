package bencode.encoder

import bencode.encodeToBencode
import org.junit.Test
import kotlin.test.assertContentEquals

class BencodeEncoderIntegerTest {

    @Test
    fun shouldCorrectlyEncodeSingleInteger() {
        val int: Long = 5L
        val expected = "i5e".encodeToByteArray()
        val actual = encodeToBencode(int)

        assertContentEquals(expected, actual)
    }
}