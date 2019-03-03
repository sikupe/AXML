package de.sikupe.axml.xml

import de.sikupe.axml.BinaryHelper.convertEndianess
import de.sikupe.axml.BinaryHelper.toLEShort
import de.sikupe.axml.Bytable
import de.sikupe.axml.WORD_SIZE
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
        baos.write(convertEndianess(CompressedXmlParser.WORD_START_TAG))

        // Chunk Size
        baos.write(convertEndianess(size()))

        // Line appearence
        baos.write(convertEndianess(mLine))

        // XML comment
        baos.write(convertEndianess(mXMLCommentIndex))

        // XMLNamespace Uri
        baos.write(convertEndianess(mNamespaceUriIndex))

        // Tag namme
        baos.write(convertEndianess(mTagNameIndex))

        // Size of Attributes
        baos.write(convertEndianess(mAttributesByteSize))

        // Number of attributes
        baos.write(toLEShort(mAttributes.size.toShort()))

        // Id attribute indes
        baos.write(convertEndianess(mIdAttributeIndex))

        // Write attributes
        mAttributes.forEach {
            baos.write(it.toByteArray())
        }

        return baos.toByteArray()
    }
}
