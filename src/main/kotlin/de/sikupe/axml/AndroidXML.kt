package de.sikupe.axml

import de.sikupe.axml.BinaryHelper.convertEndianess
import de.sikupe.axml.xml.XMLWrapper
import fr.xgouchet.axml.CompressedXmlParser
import java.io.ByteArrayOutputStream

class AndroidXML(private val mStringTable: StringTable, private val mResourceTable: ResourceTable, private val mXML: XMLWrapper) : Bytable {
    override fun toByteArray(): ByteArray {
        val buffer = ByteArrayOutputStream()

        // Write mDocument
        buffer.write(convertEndianess(CompressedXmlParser.WORD_START_DOCUMENT))
        buffer.write(BinaryHelper.convertEndianess(size()))

        // Write string table
        buffer.write(mStringTable.toByteArray())

        //Write resource table
        buffer.write(mResourceTable.toByteArray())

        // Write XML tree
        buffer.write(mXML.toByteArray())

        return buffer.toByteArray()
    }

    override fun size(): Int {
        return 2 * WORD_SIZE + mStringTable.size() + mResourceTable.size() + mXML.size()
    }
}