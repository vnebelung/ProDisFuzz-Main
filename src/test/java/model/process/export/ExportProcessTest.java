package model.process.export;

import model.protocol.ProtocolStructure;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
public class ExportProcessTest {

    private ExportProcess exportProcess;

    @BeforeClass
    public void setUp() {
        exportProcess = new ExportProcess();
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> block1 = new ArrayList<>();
        block1.add((byte) 0);
        block1.add((byte) 17);
        protocolStructure.addBlock(block1);
        List<Byte> block2 = new ArrayList<>();
        block2.add(null);
        block2.add(null);
        block2.add(null);
        protocolStructure.addBlock(block2);
        List<Byte> block3 = new ArrayList<>();
        block3.add((byte) 0);
        protocolStructure.addBlock(block3);
        List<Byte> block4 = new ArrayList<>();
        block4.add(null);
        protocolStructure.addBlock(block4);
        List<Byte> block5 = new ArrayList<>();
        block5.add((byte) 0);
        block5.add((byte) 17);
        block5.add((byte) 34);
        protocolStructure.addBlock(block5);
        exportProcess.init(protocolStructure);
    }

    @Test(priority = 2)
    public void testReset() throws Exception {
        exportProcess.reset();
        Assert.assertFalse(exportProcess.isExported());
    }

    @Test(priority = 1)
    public void testExportXML() throws IOException, URISyntaxException {
        Assert.assertFalse(exportProcess.isExported());
        //noinspection HardCodedStringLiteral
        Path path = Files.createTempFile("testng_", null);
        exportProcess.exportXML(path);
        Assert.assertTrue(exportProcess.isExported());
        Assert.assertTrue(Files.exists(path));
        String exportedXML = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        //noinspection HardCodedStringLiteral
        exportedXML = exportedXML.replaceFirst("datetime=\"[^\"]+\"", "datetime=\"2014-04-12T22:55:54+02:00\"");
        //noinspection HardCodedStringLiteral
        String referenceXML = new String(Files.readAllBytes(Paths.get(getClass().getResource("/protocol.xml").toURI()
        )), StandardCharsets.UTF_8);
        Assert.assertEquals(exportedXML, referenceXML);
        Files.delete(path);
    }
}
