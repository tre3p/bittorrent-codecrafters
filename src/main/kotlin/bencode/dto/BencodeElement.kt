package bencode.dto

sealed class BencodeElement<T> {
    abstract val data: T

    data class BencodeByteString(
        override val data: ByteArray
    ) : BencodeElement<ByteArray>() {
        val strValue = String(this.data)

        override fun toString() = "\"${strValue}\""
        override fun equals(other: Any?) = this.data.contentEquals((other as? BencodeByteString)?.data)
        override fun hashCode() = data.contentHashCode()
    }

    data class BencodeInteger(
        override val data: Long
    ) : BencodeElement<Long>() {
        override fun toString() = this.data.toString()
    }

    data class BencodeList(
        override val data: List<BencodeElement<*>>
    ) : BencodeElement<List<BencodeElement<*>>>() {
        override fun toString() = this.data.joinToString(prefix = "[", separator = ",", postfix = "]")
    }

    data class BencodeDictionary(
        override val data: Map<BencodeElement<*>, BencodeElement<*>>,
    ) : BencodeElement<Map<BencodeElement<*>, BencodeElement<*>>>() {
        override fun toString() =
            this.data.map { (k, v) -> "$k:$v" }.joinToString(prefix = "{", separator = ",", postfix = "}")

        /**
         * Helper function which wraps key to the corresponding bencoded type.
         * Function supposes that no other keys rather than strings and ints exists,
         * cause there is no use case for such complex keys at the moment.
         */
        operator fun get(key: Any) = when (key) {
            is String -> this.data[BencodeByteString(key.toByteArray())]
            is Long -> this.data[BencodeInteger(key)]
            else -> error("Unknown type ${key::class.java} of key provided for get function of bencoded dictionary")
        }
    }
}