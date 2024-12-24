import bencode.decodeBencode
import torrent.TorrentFile
import java.io.File
import kotlin.collections.joinToString

fun main(args: Array<String>) {
    val command = args[0]

    when (command) {
        "decode" -> handleDecode(args[1].toByteArray())
        "info" -> handleInfo(args[1])

        else -> println("Unknown command $command")
    }
}


private fun handleDecode(bencoded: ByteArray) = decodeBencode(bencoded).forEach { println(it) }

@OptIn(ExperimentalStdlibApi::class)
private fun handleInfo(torrentFileName: String) {
    val fileBytes = File(torrentFileName).readBytes()
    val torrent = TorrentFile.fromBytes(fileBytes)

    println("Tracker URL: ${torrent.announceUrl}")
    println("Length: ${torrent.info.length}")
    println("Info Hash: ${torrent.info.infoHash.toHexString()}")
    println("Piece Length: ${torrent.info.pieceLength}")
    println("Piece Hashes:").also {
        torrent.pieceHashes().map { it.toHexString() }.forEach { println(it) }
    }
}