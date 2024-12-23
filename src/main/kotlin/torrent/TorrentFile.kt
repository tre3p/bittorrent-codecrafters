package torrent

import bencode.decodeBencode
import bencode.dto.BencodeElement
import bencode.ops.unwrapDataTo

data class TorrentFile(
    val announceUrl: String,
    val info: Info
) {
    class Info(
        val length: Long,
        val name: String,
        val pieceLength: Long,
        val pieces: ByteArray
    )

    companion object {
        fun fromBytes(bencodedBytes: ByteArray): TorrentFile {
            val metaDict = (decodeBencode(bencodedBytes).first() as BencodeElement.BencodeDictionary)
            val announceUrl = metaDict["announce"]?.unwrapDataTo<String>() ?: error("Expected key 'announce' is not found")
            val infoDict = metaDict["info"] as BencodeElement.BencodeDictionary

            return TorrentFile(
                announceUrl,
                Info(
                    // TODO: add proper handling of nullable types
                    infoDict["length"]?.unwrapDataTo<Long>() ?: error("Expected key info/length is not found"),
                    infoDict["name"]?.unwrapDataTo<String>() ?: error("Expected key info/name is not found"),
                    infoDict["piece length"]?.unwrapDataTo<Long>() ?: error("Expected key info/piece_length is not found"),
                    infoDict["pieces"]?.unwrapDataTo<ByteArray>() ?: error("Expected key info/pieces is not found")
                )
            )
        }
    }
}