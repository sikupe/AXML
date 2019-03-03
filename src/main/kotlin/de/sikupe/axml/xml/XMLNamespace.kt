package de.sikupe.axml.xml

import java.io.ByteArrayOutputStream

data class XMLNamespace(val mStart: XMLNamespaceStart): XMLWrapper() {
    lateinit var mEnd: XMLNamespaceEnd

    override fun size(): Int {
        return mStart.size() + mEnd.size() + mContentSize
    }

    override fun addChild(child: XMLWrapper) {
        if(mContent.isEmpty()){
            super.addChild(child)
        } else {
            throw IllegalStateException("A namespace can only have one child element!")
        }
    }

    override fun toByteArray(): ByteArray {
        val baos = ByteArrayOutputStream()

        baos.write(mStart.toByteArray())
        baos.write(mContentData)
        baos.write(mEnd.toByteArray())

        return baos.toByteArray()
    }
}