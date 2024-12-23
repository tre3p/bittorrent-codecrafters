package torrent

import bencode.decodeBencode
import bencode.dto.BencodeElement
import bencode.encodeToBencode
import bencode.ops.unwrapDataTo
import java.security.MessageDigest

data class TorrentFile(
    val announceUrl: String,
    val info: Info
) {
    class Info(
        val length: Long,
        val name: String,
        val pieceLength: Long,
        val pieces: ByteArray,
        val infoHash: ByteArray
    )

    fun pieceHashes() = this.info.pieces.asIterable().chunked(PIECE_HASH_SIZE).map { it.toByteArray() }

    companion object {
        fun fromBytes(bencodedBytes: ByteArray): TorrentFile =
            (decodeBencode(bencodedBytes).first() as BencodeElement.BencodeDictionary).let { metaDict ->
                (metaDict["info"] as BencodeElement.BencodeDictionary).let { infoDict ->
                    TorrentFile(
                        announceUrl = metaDict["announce"]?.unwrapDataTo<String>() ?: keyNotFoundErr("announce"),
                        info =
                            Info(
                                length = infoDict["length"]?.unwrapDataTo<Long>() ?: keyNotFoundErr("info/length"),
                                name = infoDict["name"]?.unwrapDataTo<String>() ?: keyNotFoundErr("info/name"),
                                pieceLength = infoDict["piece length"]?.unwrapDataTo<Long>()
                                    ?: keyNotFoundErr("info/piece_length"),
                                pieces = infoDict["pieces"]?.unwrapDataTo<ByteArray>() ?: keyNotFoundErr("info/pieces"),
                                infoHash = sha1Hash(encodeToBencode(infoDict.getRawData()))
                            )
                    )
                }
            }

        private fun keyNotFoundErr(keyName: String): Nothing = error("Expected key '$keyName' is not found")
        private fun sha1Hash(data: ByteArray) = MessageDigest.getInstance("SHA-1").digest(data)

        private const val PIECE_HASH_SIZE = 20
    }
}