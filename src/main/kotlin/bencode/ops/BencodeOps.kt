package bencode.ops

import bencode.dto.BencodeElement
import bencode.dto.BencodeElement.BencodeByteString

/**
 * Unwraps underlying data of BencodeElement to raw type
 */
inline fun <reified T> BencodeElement<*>.unwrapDataTo(): T = when (this::class.java) {
    // String can be unwrapped in two ways: string representation or raw bytes
    BencodeByteString::class.java -> {
        when (T::class.java) {
            String::class.java -> (this as BencodeByteString).strValue as T
            ByteArray::class.java -> (this as BencodeByteString).data as T
            else -> error("Bencode string cannot be unwrapped to ${T::class.java}")
        }
    }

    // Other types doesn't require special unwrapping
    else -> this.data as T
}

const val STRING_DELIMITER_CHAR = ':'
const val INTEGER_TOKEN_START_CHAR = 'i'
const val LIST_TOKEN_START_CHAR = 'l'
const val MAP_TOKEN_START_CHAR = 'd'
const val END_TOKEN_CHAR = 'e'

const val STRING_DELIMITER_BYTE = STRING_DELIMITER_CHAR.code.toByte()
const val INTEGER_TOKEN_START_BYTE = INTEGER_TOKEN_START_CHAR.code.toByte()
const val LIST_TOKEN_START_BYTE = LIST_TOKEN_START_CHAR.code.toByte()
const val MAP_TOKEN_START_BYTE = MAP_TOKEN_START_CHAR.code.toByte()
const val END_TOKEN_BYTE = END_TOKEN_CHAR.code.toByte()