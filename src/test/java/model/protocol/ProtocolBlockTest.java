package model.protocol;

import model.protocol.ProtocolBlock.Type;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProtocolBlockTest {

    private ProtocolBlock protocolBlock;
    private ProtocolBlock protocolBlockNull;
    private Byte[] bytes = {48, 49, 50};
    private Byte[] bytesNull = {null, null};

    @BeforeMethod
    public void setUp() throws Exception {
        protocolBlock = new ProtocolBlock(Type.FIX, bytes);
        protocolBlockNull = new ProtocolBlock(Type.VAR, bytesNull);
    }

    @Test
    public void testGetType() throws Exception {
        Assert.assertEquals(protocolBlock.getType(), Type.FIX);
        Assert.assertEquals(protocolBlockNull.getType(), Type.VAR);
    }

    @Test
    public void testGetMinLength() throws Exception {
        Assert.assertEquals(protocolBlock.getMinLength(), bytes.length);
        Assert.assertEquals(protocolBlockNull.getMinLength(), bytesNull.length);
    }

    @Test
    public void testGetMaxLength() throws Exception {
        Assert.assertEquals(protocolBlock.getMaxLength(), bytes.length);
        Assert.assertEquals(protocolBlockNull.getMaxLength(), bytesNull.length);
    }

    @Test
    public void testGetBytes() throws Exception {
        Assert.assertEquals(protocolBlock.getBytes(), bytes);
        Assert.assertEquals(protocolBlockNull.getBytes(), bytesNull);
    }
}
