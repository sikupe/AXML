import de.sikupe.axml.helper.compareWith
import fr.xgouchet.axml.CompressedXmlDomListener
import fr.xgouchet.axml.CompressedXmlParser
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import java.io.File

class ParserTest {
    @Test
    fun test(){
        val parser = CompressedXmlParser()
        val listener = CompressedXmlDomListener()
        val origData = ParserTest::class.java.getResourceAsStream("AndroidManifest.xml").readBytes()
        parser.parse(origData.inputStream(), listener)

        val processedData = listener.mDocument.toByteArray()

        origData.assertEquals(processedData)
    }
}