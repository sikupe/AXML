package fr.xgouchet.axml

import de.sikupe.axml.AndroidXML
import de.sikupe.axml.BinaryHelper
import de.sikupe.axml.ResourceTable
import de.sikupe.axml.StringTable
import de.sikupe.axml.xml.XMLAttribute

import javax.xml.parsers.ParserConfigurationException
import java.io.IOException
import java.io.InputStream
import java.text.DecimalFormat
import java.util.HashMap


class CompressedXmlParser {

    // Data
    private lateinit var mListener: CompressedXmlParserListener

    // Internal
    private val mNamespaces: MutableMap<String, String?>
    private lateinit var mData: ByteArray

    private lateinit var mStringsTable: StringTable
    private lateinit var mResourcesIds: IntArray
    private var mStringsCount: Int = 0
    private var mStylesCount: Int = 0
    private var mResCount: Int = 0
    private var mParserOffset: Int = 0

    init {
        mNamespaces = HashMap()
    }

    /**
     * Parses the xml data in the given file,
     *
     * @param input    the source input to parse
     * @param listener the listener for XML events (must not be null)
     * @throws IOException if the input can't be read
     */
    @Throws(IOException::class)
    fun parse(
        input: InputStream,
        listener: CompressedXmlParserListener?
    ) {

        if (listener == null) {
            throw IllegalArgumentException(
                "CompressedXmlParser Listener can' be null"
            )
        }
        mListener = listener

        mData = input.readBytes()

        // parseCompressedHeader();
        parseCompressedXml()

    }

    /**
     * Parses the xml data in the given file,
     *
     * @param input the source file to parse
     * @return the DOM mDocument object
     * @throws IOException                  if the input can't be read
     * @throws ParserConfigurationException if a DocumentBuilder can't be created
     */
    @Throws(IOException::class, ParserConfigurationException::class)
    fun parseDOM(input: InputStream): AndroidXML {
        val dom = CompressedXmlDomListener()

        parse(input, dom)

        return dom.mDocument
    }

    /**
     * Each tag starts with a 32 bits word (different for start tag, end tag and
     * end doc)
     */
    private fun parseCompressedXml() {
        var word0: Int

        while (mParserOffset < mData.size) {
            word0 = getLEWord(mParserOffset)
            when (word0) {
                WORD_START_DOCUMENT -> parseStartDocument()
                WORD_STRING_TABLE -> parseStringTable()
                WORD_RES_TABLE -> parseResourceTable()
                WORD_START_NS -> parseNamespace(true)
                WORD_END_NS -> parseNamespace(false)
                WORD_START_TAG -> parseStartTag()
                WORD_END_TAG -> parseEndTag()
                WORD_TEXT -> parseText()
                WORD_EOS -> mListener.endDocument()
                else -> mParserOffset += WORD_SIZE
            }//				Log.w(TAG, "Unknown word 0x" + Integer.toHexString(word0)
            //						+ " @" + mParserOffset);
        }

        mListener.endDocument()
    }

    /**
     * A doc starts with the following 4bytes words :
     *
     *  * 0th word : 0x00080003
     *  * 1st word : chunk size
     *
     */
    private fun parseStartDocument() {
        mListener.startDocument()
        mParserOffset += 2 * WORD_SIZE
    }

    /**
     * the string table starts with the following 4bytes words :
     *
     *  * 0th word : 0x1c0001
     *  * 1st word : chunk size
     *  * 2nd word : number of string in the smtring table
     *  * 3rd word : number of styles in the string table
     *  * 4th word : flags - sorted/utf8 flag (0)
     *  * 5th word : Offset to String data
     *  * 6th word : Offset to style data
     *
     */
    private fun parseStringTable() {

        val chunk = getLEWord(mParserOffset + 1 * WORD_SIZE)
        mStringsCount = getLEWord(mParserOffset + 2 * WORD_SIZE)
        mStylesCount = getLEWord(mParserOffset + 3 * WORD_SIZE)
        val strOffset = mParserOffset + getLEWord(mParserOffset + 5 * WORD_SIZE)
        val styleOffset = getLEWord(mParserOffset + 6 * WORD_SIZE)

        val styleData = if(mStylesCount != 0) {
            sliceData((mParserOffset + styleOffset), (mParserOffset + chunk))
        } else {
            ByteArray(0)
        }

        val stringTable = StringTable(mStylesCount, styleData)

        var offset: Int
        for (i in 0 until mStringsCount) {
            offset = getStringOffset(strOffset, i)
            val string = getStringFromStringTable(offset)
            stringTable.addOriginal(string)
        }

        mStringsTable = stringTable
        mListener.setStringTable(stringTable)

        mParserOffset += chunk
    }

    private fun getStringOffset(stringOffset: Int, index: Int): Int {
        return stringOffset + getLEWord(mParserOffset + (index + 7) * WORD_SIZE)
    }

    /**
     * the resource ids table starts with the following 4bytes words :
     *
     *  * 0th word : 0x00080180
     *  * 1st word : chunk size
     *
     */
    private fun parseResourceTable() {
        val chunk = getLEWord(mParserOffset + 1 * WORD_SIZE)

        val resourceTable = ResourceTable(sliceData(chunk))

        mListener.setResourceTable(resourceTable)

        mParserOffset += chunk
    }

    /**
     * Creates a subarray of the data array. The original data array stays the same.
     * @param from Start index (including)
     * @param to End index (excluding)
     * @return subarray of the data within the given bounds
     */
    private fun sliceData(from: Int, to: Int): ByteArray{
        return mData.sliceArray(from until to)
    }

    /**
     * Creates a subarray of the data array. The start index is the current parser offset. The end index is the parser index plus the chunk size.
     * @param chunkSize Length of the subarray (starting at the current parser offset)
     * @return subarray of the data
     */
    private fun sliceData(chunkSize: Int): ByteArray {
        return sliceData(mParserOffset, mParserOffset + chunkSize)
    }

    /**
     * A namespace tag contains the following 4bytes words :
     *
     *  * 0th word : 0x00100100 = Start NS / 0x00100101 = end NS
     *  * 1st word : chunk size
     *  * 2nd word : line this tag appeared
     *  * 3rd word : optional xml comment for element (usually 0xFFFFFF)
     *  * 4th word : index of namespace prefix in StringIndexTable
     *  * 5th word : index of namespace uri in StringIndexTable
     *
     */
    private fun parseNamespace(start: Boolean) {
        val size = 6 * WORD_SIZE

        if (start) {
            mListener.startNamespace(sliceData(size))
        } else {
            mListener.endNamespace(sliceData(size))
        }

        // Offset to first tag
        mParserOffset += 6 * WORD_SIZE
    }

    /**
     * A start tag will start with the following 4bytes words :
     *
     *  * 0th word : 0x00100102 = Start_Tag
     *  * 1st word : chunk size
     *  * 2nd word : line this tag appeared in the original file
     *  * 3rd word : optional xml comment for element (usually 0xFFFFFF)
     *  * 4th word : index of namespace uri in StringIndexTable, or 0xFFFFFFFF
     * for default NS
     *  * 5th word : index of element name in StringIndexTable
     *  * 6th word : size of attribute structures to follow
     *  * 7th word : number of attributes following the start tag
     *  * 8th word : index of id attribute (0 if none)
     *
     */
    private fun parseStartTag() {
        // get tag info
        val line = getLEWord(mParserOffset + 2 * WORD_SIZE)
        val comment = getLEWord(mParserOffset + 3 * WORD_SIZE)
        val uriIdx = getLEWord(mParserOffset + 4 * WORD_SIZE)
        val nameIdx = getLEWord(mParserOffset + 5 * WORD_SIZE)
        val attrCount = getLEShort(mParserOffset + 7 * WORD_SIZE)

        // offset to start of attributes
        mParserOffset += 9 * WORD_SIZE

        val attrs = mutableListOf<XMLAttribute>() // NOPMD
        for (a in 0 until attrCount) {
            attrs.add(parseAttribute()) // NOPMD

            // offset to next attribute or tag
            mParserOffset += 5 * 4
        }

        mListener.startElement(line, comment, uriIdx, nameIdx, attrs)
    }

    /**
     * An attribute will have the following 4bytes words :
     *
     *  * 0th word : index of namespace uri in StringIndexTable, or 0xFFFFFFFF
     * for default NS
     *  * 1st word : index of attribute name in StringIndexTable
     *  * 2nd word : index of attribute value, or 0xFFFFFFFF if value is a
     * typed value
     *  * 3rd word : value type
     *  * 4th word : resource id value
     *
     */
    private fun parseAttribute(): XMLAttribute {
        val attrNSIdx = getLEWord(mParserOffset)
        val attrNameIdx = getLEWord(mParserOffset + 1 * WORD_SIZE)
        val attrValueIdx = getLEWord(mParserOffset + 2 * WORD_SIZE)
        val attrType = getLEWord(mParserOffset + 3 * WORD_SIZE)
        val attrData = getLEWord(mParserOffset + 4 * WORD_SIZE)
        val name = mStringsTable.get(attrNameIdx)

        return XMLAttribute(name, attrNSIdx, attrNameIdx, attrValueIdx, attrType, attrData)
    }

    /**
     * A text will start with the following 4bytes word :
     *
     *  * 0th word : 0x00100104 = Text
     *  * 1st word : chunk size
     *  * 2nd word : line this element appeared in the original mDocument
     *  * 3rd word : optional xml comment for element (usually 0xFFFFFF)
     *  * 4rd word : string index in string table
     *  * 5rd word : ??? (always 8)
     *  * 6rd word : ??? (always 0)
     *
     */
    private fun parseText() {
        // get tag infos
        val line = getLEWord(mParserOffset + 2 * WORD_SIZE)
        val comment = getLEWord(mParserOffset + 3 * WORD_SIZE)
        val strIndex = getLEWord(mParserOffset + 4 * WORD_SIZE)
        val fifth = getLEWord(mParserOffset + 5 * WORD_SIZE)
        val sixth = getLEWord(mParserOffset + 6 * WORD_SIZE)

        mListener.text(line, comment, strIndex, fifth, sixth)

        // offset to next node
        mParserOffset += 7 * WORD_SIZE
    }

    /**
     * EndTag contains the following 4bytes words :
     *
     *  * 0th word : 0x00100103 = End_Tag
     *  * 1st word : chunk size
     *  * 2nd word : line this tag appeared in the original file
     *  * 3rd word : optional xml comment for element (usually 0xFFFFFF)
     *  * 4th word : index of namespace name in StringIndexTable, or 0xFFFFFFFF
     * for default NS
     *  * 5th word : index of element name in StringIndexTable
     *
     */
    private fun parseEndTag() {
        // get tag info
        val line = getLEWord(mParserOffset + 2 * WORD_SIZE)
        val comment = getLEWord(mParserOffset + 3 * WORD_SIZE)
        val uriIdx = getLEWord(mParserOffset + 4 * WORD_SIZE)
        val nameIdx = getLEWord(mParserOffset + 5 * WORD_SIZE)

        mListener.endElement(line, comment, uriIdx, nameIdx)

        // offset to start of next tag
        mParserOffset += 6 * WORD_SIZE
    }

    /**
     * @param offset offset of the beginning of the string inside the de.sikupe.axml.StringTable
     * (and not the whole data array)
     * @return the String
     */
    private fun getStringFromStringTable(offset: Int): String {
        val strLength: Int
        val chars: ByteArray
        if (mData[offset + 1] == mData[offset]) {
            strLength = mData[offset].toInt()
            chars = ByteArray(strLength)// NOPMD
            for (i in 0 until strLength) {
                chars[i] = mData[offset + 2 + i] // NOPMD
            }
        } else {
            strLength = mData[offset + 1].toInt() shl 8 and 0xFF00 or (mData[offset].toInt() and 0xFF)
            chars = ByteArray(strLength) // NOPMD
            for (i in 0 until strLength) {
                chars[i] = mData[offset + 2 + i * 2] // NOPMD
            }

        }
        val string = String(chars)
        return string
    }

    /**
     * @param off the offset of the word to read
     * @return value of a Little Endian 32 bit word from the byte arrayat offset
     * off.
     */
    private fun getLEWord(off: Int): Int {
        return BinaryHelper.convertEndianess(mData, off)
    }

    /**
     * @param off the offset of the word to read
     * @return value of a Little Endian 16 bit word from the byte array at offset
     * off.
     */
    private fun getLEShort(off: Int): Short {
        return BinaryHelper.getLEShort(mData, off)
    }

    /**
     * @param type the attribute type
     * @param data the data value
     * @return the typed value
     */
    private fun getAttributeValue(type: Int, data: Int): String? {
        val res: String?

        when (type) {
            TYPE_STRING -> res = mStringsTable.get(data)
            TYPE_DIMEN -> res = Integer.toString(data shr 8) + DIMEN[data and 0xFF]
            TYPE_FRACTION -> {
                val fracValue = data.toDouble() / 0x7FFFFFFF.toDouble()
                // res = String.format("%.2f%%", fracValue);
                res = DecimalFormat("#.##%").format(fracValue)
            }
            TYPE_FLOAT -> res = java.lang.Float.toString(java.lang.Float.intBitsToFloat(data))
            TYPE_INT, TYPE_FLAGS -> res = Integer.toString(data)
            TYPE_BOOL -> res = java.lang.Boolean.toString(data != 0)
            TYPE_COLOR, TYPE_COLOR2 -> res = String.format("#%08X", data)
            TYPE_ID_REF -> res = String.format("@id/0x%08X", data)
            TYPE_ATTR_REF -> res = String.format("?id/0x%08X", data)
            else ->
                //                Log.w(TAG, "(type=" + Integer.toHexString(type) + ") : " + data
                //                        + " (0x" + Integer.toHexString(data) + ") @"
                //                        + mParserOffset);
                res = String.format("%08X/0x%08X", type, data)
        }

        return res
    }

    companion object {

        val TAG = "CXP"

        val WORD_START_DOCUMENT = 0x00080003

        val WORD_STRING_TABLE = 0x001C0001
        val WORD_RES_TABLE = 0x00080180

        val WORD_START_NS = 0x00100100
        val WORD_END_NS = 0x00100101
        val WORD_START_TAG = 0x00100102
        val WORD_END_TAG = 0x00100103
        val WORD_TEXT = 0x00100104
        val WORD_EOS = -0x1
        val WORD_SIZE = 4

        private val TYPE_ID_REF = 0x01000008
        private val TYPE_ATTR_REF = 0x02000008
        private val TYPE_STRING = 0x03000008
        private val TYPE_DIMEN = 0x05000008
        private val TYPE_FRACTION = 0x06000008
        private val TYPE_INT = 0x10000008
        private val TYPE_FLOAT = 0x04000008

        private val TYPE_FLAGS = 0x11000008
        private val TYPE_BOOL = 0x12000008
        private val TYPE_COLOR = 0x1C000008
        private val TYPE_COLOR2 = 0x1D000008

        private val DIMEN = arrayOf("px", "dp", "sp", "pt", "in", "mm")
    }

}
