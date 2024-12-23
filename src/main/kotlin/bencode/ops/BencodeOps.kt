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