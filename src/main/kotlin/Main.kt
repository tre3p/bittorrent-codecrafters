fun main(args: Array<String>) {
    val command = args[0]
    when (command) {
        "decode" -> {
            val bencodedValue = args[1]
            val decoded = decodeBencode(bencodedValue)
            println(decoded)
            return
        }
        else -> println("Unknown command $command")
    }
}

fun decodeBencode(bencodedString: String): String {
    when {
        Character.isDigit(bencodedString[0]) -> {
            val firstColonIndex = bencodedString.indexOfFirst { it == ':' }
            val length = Integer.parseInt(bencodedString.substring(0, firstColonIndex))
            return "\"${bencodedString.substring(firstColonIndex + 1, firstColonIndex + 1 + length)}\""
        }

        bencodedString[0].toChar() == 'i' -> {
            val intEndIdx = bencodedString.indexOfFirst { it == 'e' }
            return bencodedString.substring(1, intEndIdx)
        }
        else -> TODO("Only strings are supported at the moment")
    }
}
