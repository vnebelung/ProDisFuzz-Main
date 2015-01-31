package model.process.import_;

import model.protocol.ProtocolBlock;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Paths;

public class ImportProcessTest {

    @Test
    public void testImportXML() throws Exception {
        ImportProcess importProcess = new ImportProcess();
        importProcess.importXML(Paths.get(getClass().getResource("/protocol.xml").toURI()));
        Assert.assertEquals(importProcess.getProtocolStructure().getSize(), 5);

        Byte[] b0 = {0, 17};
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(0).getType(), ProtocolBlock.Type.FIX);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(0).getMinLength(), b0.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(0).getMaxLength(), b0.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(0).getBytes(), b0);

        Byte[] b1 = {null, null, null};
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(1).getType(), ProtocolBlock.Type.VAR);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(1).getMinLength(), b1.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(1).getMaxLength(), b1.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(1).getBytes(), b1);

        Byte[] b2 = {0};
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(2).getType(), ProtocolBlock.Type.FIX);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(2).getMinLength(), b2.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(2).getMaxLength(), b2.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(2).getBytes(), b2);

        Byte[] b3 = {null};
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(3).getType(), ProtocolBlock.Type.VAR);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(3).getMinLength(), b3.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(3).getMaxLength(), b3.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(3).getBytes(), b3);

        Byte[] b4 = {0, 17, 34};
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(4).getType(), ProtocolBlock.Type.FIX);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(4).getMinLength(), b4.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(4).getMaxLength(), b4.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(4).getBytes(), b4);
    }

    @Test
    public void testIsImported() throws Exception {
        ImportProcess importProcess = new ImportProcess();
        Assert.assertFalse(importProcess.isImported());
        importProcess.importXML(Paths.get(getClass().getResource("/protocol.xml").toURI()));
        Assert.assertTrue(importProcess.isImported());
    }
}
