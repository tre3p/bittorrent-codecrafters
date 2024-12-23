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
        override fun toString(): String {
            return "\"$data\""
        }
    }

    class BencodeInteger(
        override val data: Long
    ) : BencodeElement<Long>()

    class BencodeList(
        override val data: List<BencodeElement<*>>
    ) : BencodeElement<List<BencodeElement<*>>>()
}