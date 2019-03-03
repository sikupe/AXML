package de.sikupe.axml.xml

import de.sikupe.axml.BinaryHelper
import de.sikupe.axml.WORD_SIZE
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
        baos.write(BinaryHelper.convertEndianess(CompressedXmlParser.WORD_TEXT))

        // Chunk size
        baos.write(BinaryHelper.convertEndianess(size()))

        // Line
        baos.write(BinaryHelper.convertEndianess(mLine))

        // XML Comment
        baos.write(BinaryHelper.convertEndianess(mXMLComment))

        // String
        baos.write(BinaryHelper.convertEndianess(mStringIndex))

        // Fifth Word (don't know, always 8)
        baos.write(BinaryHelper.convertEndianess(mFifthWord))

        // Fifth Word (don't know, always 0)
        baos.write(BinaryHelper.convertEndianess(mSixthWord))
        return baos.toByteArray()
    }

    override fun addChild(child: XMLWrapper) {
        // Nothing to do here
    }
}