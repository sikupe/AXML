package de.sikupe.axml.xml

import de.sikupe.axml.BinaryHelper
import de.sikupe.axml.Bytable
import de.sikupe.axml.WORD_SIZE
import java.io.ByteArrayOutputStream

data class XMLAttribute(
    val mName: String,
    val mNamespaceUriIndex: Int,
    val mNameIndex: Int,
    val mValue: Int,
    val mValueType: Int,
    val mResourceId: Int
): Bytable {
    override fun size(): Int {
        return 5 * WORD_SIZE
    }

    override fun toByteArray(): ByteArray {
        val baos = ByteArrayOutputStream()

        // XMLNamespace Uri
        baos.write(BinaryHelper.convertEndianess(mNamespaceUriIndex))

        // Name index
        baos.write(BinaryHelper.convertEndianess(mNameIndex))

        // Value index
        baos.write(BinaryHelper.convertEndianess(mValue))

        // Attribute type
        baos.write(BinaryHelper.convertEndianess(mValueType))

        // Resource Id
        baos.write(BinaryHelper.convertEndianess(mResourceId))

        return baos.toByteArray()
    }
}
