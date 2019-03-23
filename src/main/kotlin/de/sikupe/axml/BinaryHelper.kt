package de.sikupe.axml

import de.sikupe.axml.helper.writeShort
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

object BinaryHelper {

    /**
     * Converts the endianess (bytewise, not bitwise). BE -> LE and LE -> BE
     * @param word Your BE/LE word/integer
     * @return Your word in the other endianess
     */
    fun convertEndianess(word: Int): Int {
        return Integer.reverseBytes(word)
    }

    fun convertEndianess(byteArray: ByteArray, offset: Int): Int {
        return convertEndianess(ByteBuffer.wrap(byteArray, offset, 4).int)
    }

    fun getLEShort(byteArray: ByteArray, offset: Int): Short {
        return (byteArray[offset + 1].toInt() shl 8 and 0xff00 or (byteArray[offset + 0].toInt() shl 0 and 0x00ff)).toShort()
    }

    /**
     * Creates half a word with a LE short value (2 byte)
     * @param short big endian value to convert
     * @return 2-byte byte array with a LE short representation
     */
    fun toLEShort(short: Short): ByteArray {
        val array = ByteArray(2)
        array[0] = short.toByte()
        array[1] = (short.toInt() shr 8).toByte()
        return array
    }

    /**
     * Converts a string to its binary representation in the data part of the string table
     * 0x00: length as LE short
     * 0x02: string as utf-16-le encoded byte array without first 2 bytes
     * 0xEND: 2 zero bytes
     * @param string String to convert
     * @return Kind of UTF-16 formatted string for the data part of the string table (see above)
     */
    fun toBinaryStringTableString(string: String): ByteArray {
        val bos = ByteArrayOutputStream()
        // String length
        bos.writeShort(string.length.toShort())

        // String data
        val stringData = string.toByteArray(Charsets.UTF_16LE)
        bos.write(stringData)

        // String end
        bos.write(byteArrayOf(0.toByte(), 0.toByte()))
        return bos.toByteArray()
    }
}