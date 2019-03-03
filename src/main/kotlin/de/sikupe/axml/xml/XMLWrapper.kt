package de.sikupe.axml.xml

import de.sikupe.axml.Bytable
import java.io.ByteArrayOutputStream

abstract class XMLWrapper : Bytable {
    protected val mContent = mutableListOf<XMLWrapper>()
    protected val mContentSize
        get() = mContent.sumBy { it.size() }
    protected val mContentData: ByteArray
        get() {
            val baos = ByteArrayOutputStream()
            mContent.forEach {
                baos.write(it.toByteArray())
            }
            return baos.toByteArray()
        }

    open fun addChild(child: XMLWrapper) {
        mContent.add(child)
    }

    fun visit(visitor: (XMLWrapper) -> Unit) {
        visitor(this)
        mContent.forEach(visitor)
    }
}