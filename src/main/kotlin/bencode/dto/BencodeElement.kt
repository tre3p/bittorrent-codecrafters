package bencode.dto

sealed class BencodeElement<T> {
    abstract val data: T

    /**
     * Default implementation of toString() for all of the bencode elements
     */
    override fun toString(): String = data.toString()

    class BencodeString(
        override val data: String
    ) : BencodeElement<String>() {
        override fun toString() = "\"$data\""
    }

    class BencodeInteger(
        override val data: Long
    ) : BencodeElement<Long>()

    class BencodeList(
        override val data: List<BencodeElement<*>>
    ) : BencodeElement<List<BencodeElement<*>>>() {
        override fun toString() = this.data.joinToString(prefix = "[", separator = ",", postfix = "]")
    }

    class BencodeDictionary(
        override val data: Map<BencodeElement<*>, BencodeElement<*>>,
    ) : BencodeElement<Map<BencodeElement<*>, BencodeElement<*>>>() {
        override fun toString() =
            this.data.map { (k, v) -> "$k:$v" }.joinToString(prefix = "{", separator = ",", postfix = "}")
    }
}