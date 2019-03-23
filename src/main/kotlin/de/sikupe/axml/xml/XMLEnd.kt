package de.sikupe.axml.xml

import de.sikupe.axml.Bytable
import de.sikupe.axml.WORD_SIZE
import de.sikupe.axml.helper.writeInt
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
        baos.writeInt(CompressedXmlParser.WORD_END_TAG)

        // Chunk size
        baos.writeInt(size())

        // Line
        baos.writeInt(mLine)

        // XML Comment
        baos.writeInt(mXMLComment)

        // XMLNamespace
        baos.writeInt(mNamespaceUriIndex)

        // Name
        baos.writeInt(mNameIndex)

        return baos.toByteArray()
    }
}
