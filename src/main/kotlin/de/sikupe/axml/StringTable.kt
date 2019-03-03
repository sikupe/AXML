package de.sikupe.axml

import java.io.ByteArrayOutputStream
import kotlin.experimental.and

class StringTable(private val mStyleCount: Int , private val mStyleData: ByteArray ) : Table<String>() {
    private var mFlags: Int = 0

    private val mStringOffsetTableSize: Int
        get() = mSet.size * WORD_SIZE

    private val mChunkHeaderSize: Int
        get() = 7 * WORD_SIZE

    private val mStringByteCount: Int
        get() = mSet.sumBy { getRelevantStringSize(it) }

    override fun toByteArray(): ByteArray {
        val bos = ByteArrayOutputStream()

        // Start tag
        bos.write(BinaryHelper.convertEndianess(STRING_TABLE_START_TAG))
        // Chunk size
        bos.write(BinaryHelper.convertEndianess(size()))
        // String count
        bos.write(BinaryHelper.convertEndianess(mSize))
        // Style count
        bos.write(BinaryHelper.convertEndianess(mStyleCount))
        // Flags
        bos.write(BinaryHelper.convertEndianess(mFlags))
        // Offset to string data
        val offsetToStringData = mChunkHeaderSize + mStringOffsetTableSize
        bos.write(BinaryHelper.convertEndianess(offsetToStringData))
        // Offset to style data
        val offsetToStyleData = offsetToStringData + mStringByteCount
        bos.write(BinaryHelper.convertEndianess(offsetToStyleData))

        // Write String offsets
        var previosStringsSize = 0
        mSet.forEach {
            bos.write(offsetToStringData + previosStringsSize)
            previosStringsSize += getRelevantStringSize(it)
        }

        // Write String data
        mSet.forEach {
            // String length
            val stringLengthAsByteArray = ByteArray(2)
            val x = it.length.toShort()
            stringLengthAsByteArray[0] = (x and 0xff).toByte()
            stringLengthAsByteArray[1] = (x.toInt() shr 8 and 0xff).toByte()

            bos.write(BinaryHelper.convertEndianessShort(stringLengthAsByteArray))

            // String data
            bos.write(BinaryHelper.convertEndianessShort(it.toByteArray()))
        }

        // Write Style data
        bos.write(mStyleData)

        return bos.toByteArray()
    }

    override fun size(): Int {
        return mChunkHeaderSize + mStringOffsetTableSize + mStringByteCount + mStyleData.size
    }

    private fun getRelevantStringSize(string: String): Int {
        return string.toByteArray().size + 2
    }
}