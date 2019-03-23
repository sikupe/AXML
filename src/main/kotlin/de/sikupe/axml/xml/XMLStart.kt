package de.sikupe.axml.xml

import de.sikupe.axml.Bytable
import de.sikupe.axml.WORD_SIZE
import de.sikupe.axml.helper.writeInt
import fr.xgouchet.axml.CompressedXmlParser
import java.io.ByteArrayOutputStream

data class XMLStart(
    val mLine: Int,
    val mXMLCommentIndex: Int,
    val mNamespaceUriIndex: Int,
    val mTagNameIndex: Int,
    val mAttributes: List<XMLAttribute>
): Bytable {
    private val mAttributesByteSize: Int
        get() = 5 * WORD_SIZE * mAttributes.size

    private val mIdAttributeIndex: Int
        get() = mAttributes.indexOf(mAttributes.find { it.mName == "id" })


    override fun size(): Int {
        // Start tag size itself
        val tagSize = 9 * WORD_SIZE

        return tagSize + mAttributesByteSize
    }

    override fun toByteArray(): ByteArray {
        val baos = ByteArrayOutputStream()

        // Start Tag
        baos.writeInt(CompressedXmlParser.WORD_START_TAG)

        // Chunk Size
        baos.writeInt(size())

        // Line appearence
        baos.writeInt(mLine)

        // XML comment
        baos.writeInt(mXMLCommentIndex)

        // XMLNamespace Uri
        baos.writeInt(mNamespaceUriIndex)

        // Tag namme
        baos.writeInt(mTagNameIndex)

        // Size of Attributes
        baos.writeInt(mAttributesByteSize)

        // Number of attributes
        baos.writeInt((mAttributes.size))

        // Id attribute indes
        baos.writeInt(mIdAttributeIndex)

        // Write attributes
        mAttributes.forEach {
            baos.write(it.toByteArray())
        }

        return baos.toByteArray()
    }
}
