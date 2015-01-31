package model.helper;

import org.testng.Assert;
import org.testng.annotations.Test;

public class HexTest {

    @Test
    public void testByte2Hex() throws Exception {
        Assert.assertEquals(Hex.byte2Hex((byte) 0), "00");

        Assert.assertEquals(Hex.byte2Hex((byte) 127), "7f");
    }

    @Test
    public void testHex2Byte() throws Exception {
        Assert.assertEquals(Hex.hex2Byte("00"), 0);

        Assert.assertEquals(Hex.hex2Byte("7f"), 127);
    }
}
