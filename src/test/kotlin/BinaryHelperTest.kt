import de.sikupe.axml.BinaryHelper
import fr.xgouchet.axml.CompressedXmlParser
import org.junit.Assert
import org.junit.Test

class BinaryHelperTest {
    @Test
    fun testConvertEndianess() {
        val orig = CompressedXmlParser.WORD_START_DOCUMENT

        val conv = BinaryHelper.convertEndianess(orig)
        Assert.assertEquals(0x03000800, conv)


        val back = BinaryHelper.convertEndianess(conv)
        Assert.assertEquals(orig, back)
    }
}