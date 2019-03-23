import fr.xgouchet.axml.CompressedXmlDomListener
import fr.xgouchet.axml.CompressedXmlParser
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.io.File

class StringTableParsingTest {
    @Test
    fun test() {
        val parser = CompressedXmlParser()
        val listener = Mockito.spy(CompressedXmlDomListener())
        Mockito.doNothing().`when`(listener).endDocument()
        val origData = StringTableParsingTest::class.java.getResourceAsStream("stringtable.xml").readBytes()
        parser.parse(origData.inputStream(), listener)

        val processedData = listener.mStringTable.toByteArray()

        origData.assertEquals(processedData)
    }
}