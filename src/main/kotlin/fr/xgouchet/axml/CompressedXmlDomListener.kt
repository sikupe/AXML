package fr.xgouchet.axml

import de.sikupe.axml.AndroidXML
import de.sikupe.axml.ResourceTable
import de.sikupe.axml.StringTable
import de.sikupe.axml.xml.*

import javax.xml.parsers.ParserConfigurationException
import java.util.Stack

class CompressedXmlDomListener
/**
 * @throws ParserConfigurationException if a DocumentBuilder can't be created
 */
@Throws(ParserConfigurationException::class)
constructor() : CompressedXmlParserListener {
    private val mStack: Stack<XMLWrapper>
    private lateinit var mXMLRoot: XMLWrapper
    private lateinit var mStringTable: StringTable
    private lateinit var mResourceTable: ResourceTable

    /**
     * @return the parsed mDocument
     */
    lateinit var mDocument: AndroidXML

    init {
        mStack = Stack()
    }
    override fun startDocument() {
    }

    override fun endDocument() {
        mDocument = AndroidXML(mStringTable, mResourceTable, mXMLRoot)
    }

    override fun startNamespace(sliceData: ByteArray) {
        pushToStack(XMLNamespace(XMLNamespaceStart(sliceData)))
    }

    override fun endNamespace(data: ByteArray) {
        (mStack.pop() as XMLNamespace).mEnd = XMLNamespaceEnd(data)
    }

    private fun pushToStack(xmlWrapper: XMLWrapper) {
        if(!this::mXMLRoot.isInitialized) {
            mXMLRoot = xmlWrapper
        }
        mStack.push(xmlWrapper)
    }

    override fun startElement(line: Int, comment: Int, namespace: Int, tag: Int, attributes: List<XMLAttribute>){
        val element = XMLNode(XMLStart(line, comment, namespace, tag, attributes))
        mStack.peek().addChild(element)
        mStack.push(element)
    }

    override fun endElement(line: Int, comment: Int, namepaceUriIndex: Int, nameIndex: Int) {
        mStack.pop()
    }

    override fun characterData(data: String) {
        throw NotImplementedError("CDATA is currently not supported")
    }

    override fun text(line: Int, comment: Int, strIndex: Int, fifth: Int, sixth: Int) {
        mStack.peek().addChild(XMLTextNode(line, comment, strIndex, fifth, sixth))
    }

    override fun processingInstruction(target: String, data: String) {
        throw NotImplementedError("Processing Instructions are currently not supported")
    }

    override fun setStringTable(stringTable: StringTable) {
        mStringTable = stringTable
    }

    override fun setResourceTable(resourceTable: ResourceTable) {
        mResourceTable = resourceTable
    }
}
