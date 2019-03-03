package de.sikupe.axml.xml

import java.io.ByteArrayOutputStream

class XMLNode(val mStart: XMLStart): XMLWrapper() {
    lateinit var mEnd: XMLEnd

    override fun size(): Int {
        return mStart.size() + mContentSize + mEnd.size()
    }

    override fun toByteArray(): ByteArray {
        val baos = ByteArrayOutputStream()

        baos.write(mStart.toByteArray())

        baos.write(mContentData)

        baos.write(mEnd.toByteArray())

        return baos.toByteArray()
    }
}