package fr.xgouchet.axml

import de.sikupe.axml.ResourceTable
import de.sikupe.axml.StringTable
import de.sikupe.axml.xml.XMLAttribute

interface CompressedXmlParserListener {

    /**
     * Receive notification of the beginning of a mDocument.
     */
    fun startDocument()

    /**
     * Receive notification of the end of a mDocument.
     */
    fun endDocument()

    /**
     * Receive notification of the beginning of an element.
     *
     * Just receives the extracted and converted bytes and the parsed attrivutes from the file.
     */
    fun startElement(line: Int, comment: Int, namespace: Int, tag: Int, attributes: List<XMLAttribute>)

    /**
     * Receive notification of the end of an element.
     *
     * @param line
     * the XMLNamespace URI, or the empty string if the element has no
     * XMLNamespace URI or if XMLNamespace processing is not being
     * performed
     * @param comment
     * the local name (without prefix), or the empty string if
     * XMLNamespace processing is not being performed
     * @param namepaceUriIndex
     * the qualified XML name (with prefix), or the empty string if
     * qualified names are not available
     */
    fun endElement(line: Int, comment: Int, namepaceUriIndex: Int, nameIndex: Int)

    /**
     * Receive notification of text.
     */
    fun text(line: Int, comment: Int, strIndex: Int, fifth: Int, sixth: Int)

    /**
     * Receive notification of character data (in a &lt;![CDATA[ ]]&gt; block).
     *
     * @param data
     * the text data
     */
    fun characterData(data: String)

    /**
     * Receive notification of a processing instruction.
     *
     * @param target
     * the processing instruction target
     * @param data
     * the processing instruction data, or null if none was supplied.
     * The data does not include any whitespace separating it from
     * the target
     */
    fun processingInstruction(target: String, data: String)

    fun setStringTable(stringTable: StringTable)

    fun setResourceTable(resourceTable: ResourceTable)

    fun startNamespace(sliceData: ByteArray)

    fun endNamespace(data: ByteArray)
}
