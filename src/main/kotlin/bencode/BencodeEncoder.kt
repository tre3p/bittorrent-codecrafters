package bencode

import bencode.ops.END_TOKEN_BYTE
import bencode.ops.INTEGER_TOKEN_START_BYTE
import bencode.ops.LIST_TOKEN_START_BYTE
import bencode.ops.MAP_TOKEN_START_BYTE
import bencode.ops.STRING_DELIMITER_BYTE

fun encodeToBencode(obj: Any) = when (obj) {
    is String -> encodeString(obj.encodeToByteArray())
    is ByteArray -> encodeString(obj)
    is Long -> encodeInt(obj)
    is List<*> -> encodeList(obj)
    is Map<*, *> -> encodeMap(obj)
    else -> error("Unsupported object type ${obj::class.java} for bencode encoding")
}

private fun encodeString(str: ByteArray) =
    byteArrayOf()
        .plus(str.size.toString().encodeToByteArray())
        .plus(STRING_DELIMITER_BYTE)
        .plus(str)

private fun encodeInt(int: Long) =
    byteArrayOf(INTEGER_TOKEN_START_BYTE)
        .plus(int.toString().encodeToByteArray())
        .plus(END_TOKEN_BYTE)

private fun encodeList(list: List<*>): ByteArray =
    list.fold(byteArrayOf(LIST_TOKEN_START_BYTE)) { acc, elem ->
        elem?.let { acc + encodeToBencode(it) }
            ?: error("Detected attempt of encoding 'null' to bencode")
    }.plus(END_TOKEN_BYTE)

private fun encodeMap(map: Map<*, *>): ByteArray =
    map.entries.fold(byteArrayOf(MAP_TOKEN_START_BYTE)) { acc, (k, v) ->
        when {
            k == null -> error("Detected attempt of encoding nullable map key to bencode")
            v == null -> error("Detected attempt of encoding nullable map value to bencode")
            else -> acc + encodeToBencode(k) + encodeToBencode(v)
        }
    }.plus(END_TOKEN_BYTE)