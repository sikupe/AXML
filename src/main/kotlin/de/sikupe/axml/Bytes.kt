package de.sikupe.axml

abstract class Bytes(private val mData: ByteArray) : Bytable {
    override fun toByteArray(): ByteArray {
        return mData
    }

    override fun size(): Int {
        return mData.size
    }
}