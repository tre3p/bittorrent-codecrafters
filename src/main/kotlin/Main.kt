import bencode.decodeBencode

fun main(args: Array<String>) {
    val command = args[0]
    when (command) {
        "decode" -> {
            val bencodedValue = args[1]
            val decoded = decodeBencode(bencodedValue.toByteArray())
            decoded.forEach { println(it) }
            return
        }
        else -> println("Unknown command $command")
    }
}
