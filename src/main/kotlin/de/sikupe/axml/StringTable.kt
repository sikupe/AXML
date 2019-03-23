package de.sikupe.axml

import de.sikupe.axml.helper.replace
import de.sikupe.axml.helper.writeInt
import de.sikupe.axml.helper.writeShort
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
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
        bos.writeInt(STRING_TABLE_START_TAG)
        // Chunk size placeholder
        bos.writeInt(0)
        // String count
        bos.writeInt(mSize)
        // Style count
        bos.writeInt(mStyleCount)
        // Flags
        bos.writeInt(mFlags)
        // Offset to string data
        val offsetToStringData = mChunkHeaderSize + mStringOffsetTableSize
        bos.writeInt(offsetToStringData)
        // Offset to style data
        val offsetToStyleData = if(mStyleCount > 0) {
            offsetToStringData + mStringByteCount
        } else {
            0
        }
        bos.writeInt(offsetToStyleData)

        // Write String offsets
        var previosStringsSize = 0
        mSet.forEach {
            bos.writeInt(previosStringsSize)
            previosStringsSize += getRelevantStringSize(it)
        }

        // Write String data
        mSet.forEach {
            bos.write(BinaryHelper.toBinaryStringTableString(it))
        }

        // Write Style data
        bos.write(mStyleData)

        // Writing real chunk size
        val byteArray = bos.toByteArray()
        val chunkSize = ByteArrayOutputStream().apply { writeInt(byteArray.size) }.toByteArray()
        byteArray.replace(1 * WORD_SIZE, chunkSize)

        return byteArray
    }

    fun get(index: Int): String {
        return mSet.elementAt(index)
    }

    override fun size(): Int {
        return toByteArray().size
    }

    private fun getRelevantStringSize(string: String): Int {
        return string.toByteArray(Charset.forName("UTF-16")).size + 2
    }
}