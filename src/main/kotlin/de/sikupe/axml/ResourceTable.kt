package de.sikupe.axml

class ResourceTable constructor(private val mData: ByteArray) : Table<Int>() {
    override fun size(): Int {
        return mData.size
    }

    override fun toByteArray(): ByteArray {
        return mData
    }
}