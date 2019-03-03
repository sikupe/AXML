package de.sikupe.axml

import java.lang.IllegalArgumentException

object BinaryHelper {

    /**
     * Converts the endianess (bytewise, not bitwise). BE -> LE and LE -> BE
     * @param word Your BE/LE word/integer
     * @return Your word in the other endianess
     */
    fun convertEndianess(word: Int): Int {
        return (word shl 24 and -0x1000000
                or (word shl 16 and 0x00ff0000)
                or (word shl 8 and 0x0000ff00)
                or (word shl 0 and 0x000000ff))
    }

    fun convertEndianess(byteArray: ByteArray, offset: Int): Int {
        return (byteArray[offset + 3].toInt() shl 24 and -0x1000000
                or (byteArray[offset + 2].toInt() shl 16 and 0x00ff0000)
                or (byteArray[offset + 1].toInt() shl 8 and 0x0000ff00)
                or (byteArray[offset + 0].toInt() shl 0 and 0x000000ff))
    }

    fun getLEShort(byteArray: ByteArray, offset: Int): Short {
        return (byteArray[offset + 1].toInt() shl 8 and 0xff00 or (byteArray[offset + 0].toInt() shl 0 and 0x00ff)).toShort()
    }

    fun toLEShort(short: Short): ByteArray {
        val array = ByteArray(4)
        array[0] = short.toByte()
        array[1] = (short.toInt() shr 8).toByte()
        return array
    }

    /**
     * Converts the passed byte array to the other endianess by two bytes (shorts/chars)
     */
    fun convertEndianessShort(byteArray: ByteArray): ByteArray {
        if (byteArray.size % 2 != 0) {
            throw IllegalArgumentException("The byte array has to have an even count of bytes")
        }
        val out = ByteArray(byteArray.size)
        for (i in 0..byteArray.size / 2) {
            out[i * 2] = byteArray[i * 2 + 1]
            out[i * 2 + 1] = byteArray[i * 2]
        }
        return out
    }

    fun getFirstIntConverted(byteArray: ByteArray): Int {
        return convertEndianess(byteArray, 0)
    }
}