import org.junit.Assert

fun ByteArray.assertEquals(byteArray: ByteArray) {
    this.forEachIndexed { index, byte ->
        if(byte != byteArray[index]) {
            Assert.fail("Failed at byte $index")
        }
    }
}