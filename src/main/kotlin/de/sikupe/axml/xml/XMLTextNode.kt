package de.sikupe.axml.xml

import de.sikupe.axml.WORD_SIZE
import de.sikupe.axml.helper.writeInt
import fr.xgouchet.axml.CompressedXmlParser
import java.io.ByteArrayOutputStream

class XMLTextNode(
    val mLine: Int,
    val mXMLComment: Int,
    val mStringIndex: Int,
    val mFifthWord: Int,
    val mSixthWord: Int
): XMLWrapper() {
    override fun size(): Int {
        return 7 * WORD_SIZE
    }

    override fun toByteArray(): ByteArray {
        val baos = ByteArrayOutputStream()

        // Start Tag
        baos.writeInt(CompressedXmlParser.WORD_TEXT)

        // Chunk size
        baos.writeInt(size())

        // Line
        baos.writeInt(mLine)

        // XML Comment
        baos.writeInt(mXMLComment)

        // String
        baos.writeInt(mStringIndex)

        // Fifth Word (don't know, always 8)
        baos.writeInt(mFifthWord)

        // Fifth Word (don't know, always 0)
        baos.writeInt(mSixthWord)
        return baos.toByteArray()
    }

    override fun addChild(child: XMLWrapper) {
        // Nothing to do here
    }
}