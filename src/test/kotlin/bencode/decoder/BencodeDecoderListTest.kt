package bencode.decoder

import bencode.decodeBencode
import org.junit.Test
import kotlin.test.assertEquals

class BencodeDecoderListTest {

    @Test
    fun shouldCorrectlyParseList() {
        val bencodeString = "l11:bencodetesti42e4:hihie"
        val expectedElements = listOf("bencodetest", 42L, "hihi")

        val actualList = decodeBencode(bencodeString.encodeToByteArray())[0]
        assertEquals(expectedElements, actualList.data)
    }

    @Test
    fun shouldCorrectlyParseListWithNestedList() {
        val bencodeString = "l5:helloli42e2:hiee"
        val expectedElements = listOf("hello", listOf(42L, "hi"))

        val actualList = decodeBencode(bencodeString.encodeToByteArray())[0]
        assertEquals(expectedElements, actualList.data)
    }
}