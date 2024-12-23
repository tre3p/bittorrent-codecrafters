package bencode

import bencode.dto.BencodeElement

private data class ParsedBencodeElement(
    val bencodeElement: BencodeElement<*>,
    val endElementIdx: Int
)

private const val STRING_DELIMITER_CHAR = ':'
private const val INTEGER_TOKEN_START_CHAR = 'i'
private const val LIST_TOKEN_START_CHAR = 'l'
private const val MAP_TOKEN_START_CHAR = 'd'
private const val END_TOKEN_CHAR = 'e'

private const val STRING_DELIMITER_BYTE = STRING_DELIMITER_CHAR.code.toByte()
private const val INTEGER_TOKEN_START_BYTE = INTEGER_TOKEN_START_CHAR.code.toByte()
private const val LIST_TOKEN_START_BYTE = LIST_TOKEN_START_CHAR.code.toByte()
private const val MAP_TOKEN_START_BYTE = MAP_TOKEN_START_CHAR.code.toByte()
private const val END_TOKEN_BYTE = END_TOKEN_CHAR.code.toByte()

tailrec fun decodeBencode(
    bencoded: ByteArray,
    parsedTokens: List<BencodeElement<*>> = emptyList(),
    nextTokenPosition: Int = 0
): List<BencodeElement<*>> {
    if (nextTokenPosition >= bencoded.size) {
        return parsedTokens
    }

    val parsedElement = decodeSingleBencodeElement(bencoded, nextTokenPosition)

    return decodeBencode(
        bencoded,
        parsedTokens = parsedTokens + parsedElement.bencodeElement,
        nextTokenPosition = nextTokenPosition + parsedElement.endElementIdx + 1
    )
}

private fun decodeSingleBencodeElement(bencode: ByteArray, startIdx: Int): ParsedBencodeElement {
    val firstByteChar = Char(bencode[startIdx].toInt())

    return when {
        Character.isDigit(firstByteChar) -> decodeBencodedString(bencode, startIdx)
        bencode[startIdx] == INTEGER_TOKEN_START_BYTE -> decodeBencodedInteger(bencode, startIdx)
        bencode[startIdx] == LIST_TOKEN_START_BYTE -> decodeBencodedList(bencode, startIdx)

        else -> error("Unknown bencode element encountered: $firstByteChar")
    }
}

private fun decodeBencodedInteger(bencoded: ByteArray, startIndex: Int): ParsedBencodeElement {
    val integerEndIdx = bencoded.firstIndexOf(END_TOKEN_BYTE, startIndex)
    val bencodedValue = String(bencoded.copyOfRange(startIndex + 1, integerEndIdx)).toLong()

    return ParsedBencodeElement(BencodeElement.BencodeInteger(bencodedValue), integerEndIdx)
}

private fun decodeBencodedList(
    bencoded: ByteArray,
    startIndex: Int
): ParsedBencodeElement {
    var currentElementPosition = startIndex + 1
    val decodedElements = mutableListOf<BencodeElement<*>>()

    while (bencoded[currentElementPosition] != END_TOKEN_BYTE) {
        val decodedElement = decodeSingleBencodeElement(bencoded, currentElementPosition)
        currentElementPosition = decodedElement.endElementIdx + 1

        decodedElements.add(decodedElement.bencodeElement)
    }

    return ParsedBencodeElement(BencodeElement.BencodeList(decodedElements), currentElementPosition)
}

private fun decodeBencodedString(bencodedArr: ByteArray, startIndex: Int): ParsedBencodeElement {
    val firstColonIndex = bencodedArr.firstIndexOf(STRING_DELIMITER_BYTE, startIndex)
    val stringSize = String(bencodedArr.copyOfRange(startIndex, firstColonIndex)).toInt()

    val stringStartIdx = firstColonIndex + 1
    val stringEndIdx = stringStartIdx + stringSize

    val bencodedValue = String(bencodedArr.copyOfRange(stringStartIdx, stringEndIdx))

    return ParsedBencodeElement(
        bencodeElement = BencodeElement.BencodeString(bencodedValue),
        endElementIdx = stringEndIdx - 1
    )
}

private fun ByteArray.firstIndexOf(target: Byte, startPos: Int): Int {
    for (i in startPos..this.size) {
        if (this[i] == target) {
            return i
        }
    }

    return -1
}