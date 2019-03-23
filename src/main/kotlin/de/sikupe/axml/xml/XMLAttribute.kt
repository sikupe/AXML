package de.sikupe.axml.xml

import de.sikupe.axml.Bytable
import de.sikupe.axml.WORD_SIZE
import de.sikupe.axml.helper.writeInt
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
        baos.writeInt(mNamespaceUriIndex)

        // Name index
        baos.writeInt(mNameIndex)

        // Value index
        baos.writeInt(mValue)

        // Attribute type
        baos.writeInt(mValueType)

        // Resource Id
        baos.writeInt(mResourceId)

        return baos.toByteArray()
    }
}
