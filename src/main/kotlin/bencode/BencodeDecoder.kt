package bencode

import bencode.dto.BencodeElement
import bencode.ops.END_TOKEN_BYTE
import bencode.ops.INTEGER_TOKEN_START_BYTE
import bencode.ops.LIST_TOKEN_START_BYTE
import bencode.ops.MAP_TOKEN_START_BYTE
import bencode.ops.STRING_DELIMITER_BYTE

private typealias ParsedBencodeElement = Pair<BencodeElement<*>, Int>

tailrec fun decodeBencode(
    bencoded: ByteArray,
    parsedTokens: List<BencodeElement<*>> = emptyList(),
    nextTokenPosition: Int = 0
): List<BencodeElement<*>> {
    if (nextTokenPosition >= bencoded.size) {
        return parsedTokens
    }

    val (parsedElement, endIdx) = decodeSingleBencodeElement(bencoded, nextTokenPosition)

    return decodeBencode(
        bencoded,
        parsedTokens = parsedTokens + parsedElement,
        nextTokenPosition = nextTokenPosition + endIdx + 1
    )
}

private fun decodeSingleBencodeElement(bencode: ByteArray, startIdx: Int): ParsedBencodeElement {
    val firstByteChar = Char(bencode[startIdx].toInt())

    return when {
        Character.isDigit(firstByteChar) -> decodeBencodedString(bencode, startIdx)
        bencode[startIdx] == INTEGER_TOKEN_START_BYTE -> decodeBencodedInteger(bencode, startIdx)
        bencode[startIdx] == LIST_TOKEN_START_BYTE -> decodeBencodedList(bencode, startIdx)
        bencode[startIdx] == MAP_TOKEN_START_BYTE -> decodeBencodedDictionary(bencode, startIdx)

        else -> error("Unknown bencode element encountered: '$firstByteChar'")
    }
}

private fun decodeBencodedDictionary(bencoded: ByteArray, startIndex: Int): ParsedBencodeElement {
    val parsedDictionary = mutableMapOf<BencodeElement<*>, BencodeElement<*>>()
    var currentIdx = startIndex + 1

    while (bencoded[currentIdx] != END_TOKEN_BYTE) {
        val (dictKey, dictKeyEndIdx) = decodeSingleBencodeElement(bencoded, currentIdx)
        val (dictValue, dictValueEndIdx) = decodeSingleBencodeElement(bencoded, dictKeyEndIdx + 1)
        currentIdx = dictValueEndIdx + 1

        parsedDictionary.put(dictKey, dictValue)
    }

    return ParsedBencodeElement(BencodeElement.BencodeDictionary(parsedDictionary), currentIdx)
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
        val (element, elementEndIdx) = decodeSingleBencodeElement(bencoded, currentElementPosition)
        currentElementPosition = elementEndIdx + 1

        decodedElements.add(element)
    }

    return ParsedBencodeElement(BencodeElement.BencodeList(decodedElements), currentElementPosition)
}

private fun decodeBencodedString(bencodedArr: ByteArray, startIndex: Int): ParsedBencodeElement {
    val firstColonIndex = bencodedArr.firstIndexOf(STRING_DELIMITER_BYTE, startIndex)
    val stringSize = String(bencodedArr.copyOfRange(startIndex, firstColonIndex)).toInt()

    val stringStartIdx = firstColonIndex + 1
    val stringEndIdx = stringStartIdx + stringSize

    val bencodedValue = bencodedArr.copyOfRange(stringStartIdx, stringEndIdx)

    return ParsedBencodeElement(BencodeElement.BencodeByteString(bencodedValue), stringEndIdx - 1)
}

private fun ByteArray.firstIndexOf(target: Byte, startPos: Int): Int {
    for (i in startPos..this.size) {
        if (this[i] == target) {
            return i
        }
    }

    return -1
}