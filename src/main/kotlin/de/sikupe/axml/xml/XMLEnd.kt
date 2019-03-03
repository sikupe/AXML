package de.sikupe.axml.xml

import de.sikupe.axml.BinaryHelper
import de.sikupe.axml.Bytable
import de.sikupe.axml.WORD_SIZE
import fr.xgouchet.axml.CompressedXmlParser
import java.io.ByteArrayOutputStream

data class XMLEnd(
    val mLine: Int,
    val mXMLComment: Int,
    val mNamespaceUriIndex: Int,
    val mNameIndex: Int
): Bytable {
    override fun size(): Int {
        return 6 * WORD_SIZE
    }

    override fun toByteArray(): ByteArray {
        val baos = ByteArrayOutputStream()

        // Start Tag
        baos.write(BinaryHelper.convertEndianess(CompressedXmlParser.WORD_END_TAG))

        // Chunk size
        baos.write(BinaryHelper.convertEndianess(size()))

        // Line
        baos.write(BinaryHelper.convertEndianess(mLine))

        // XML Comment
        baos.write(BinaryHelper.convertEndianess(mXMLComment))

        // XMLNamespace
        baos.write(BinaryHelper.convertEndianess(mNamespaceUriIndex))

        // Name
        baos.write(BinaryHelper.convertEndianess(mNameIndex))

        return baos.toByteArray()
    }
}
