package de.sikupe.axml.helper

import de.sikupe.axml.BinaryHelper
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

fun ByteArray.compareWith(byteArray: ByteArray): Boolean {
    if (size != byteArray.size) {
        return false
    }

    forEachIndexed { index, byte ->
        if (byte != byteArray[index]) {
            return false
        }
    }

    return true
}

/**
 * Replaces the bytes starting from the start index with the passed bytes
 * If the passed array is to long nothing will be replaced
 * @param start start index where the replacing starts
 * @param byteArray replacement bytes
 * @throws IllegalArgumentException If the replacement byte array is too long
 * @throws IndexOutOfBoundsException If the start index is out of bounds
 */
fun ByteArray.replace(start: Int, byteArray: ByteArray) {
    if(start + byteArray.size >= size) {
        throw IllegalArgumentException("Passed replacement byte array length exceeds current length")
    }
    byteArray.forEachIndexed { index, byte ->
        this[start + index] = byte
    }
}

/**
 * Convertes the endianess of the int and then writes the bytes of the integer to the ByteArrayOutputStream
 * @param int word to convert and write
 */
fun ByteArrayOutputStream.writeInt(int: Int){
    val reversed = BinaryHelper.convertEndianess(int)
    val bytes = ByteBuffer.allocate(4).putInt(reversed).array()
    write(bytes)
}

/**
 * Convertes the endianess of the int and then writes the bytes of the integer to the ByteArrayOutputStream
 * @param int word to convert and write
 */
fun ByteArrayOutputStream.writeShort(short: Short){
    val bytes = BinaryHelper.toLEShort(short)
    write(bytes)
}

