package model.process.export;

import model.protocol.ProtocolStructure;
import model.xml.WhiteSpaceEliminator;
import nu.xom.*;
import nu.xom.canonical.Canonicalizer;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ExportProcessTest {

    private ExportProcess exportProcess;
    private Pattern pattern;

    @BeforeClass
    public void setUp() {
        exportProcess = new ExportProcess();
        pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{2}:\\d{2}");
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
        Assert.assertTrue(exportProcess.isExported());
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

        String exported = null;
        //noinspection OverlyBroadCatchBlock
        try (OutputStream outputStream = new ByteArrayOutputStream()) {
            Builder parser = new Builder(new WhiteSpaceEliminator());
            Document exportedDoc = parser.build(path.toFile());
            replaceDateTimes(exportedDoc.getRootElement());
            Canonicalizer canonicalizer = new Canonicalizer(outputStream, false);
            canonicalizer.write(exportedDoc);
            exported = outputStream.toString();
        } catch (ParsingException | IOException e) {
            e.printStackTrace();
        }
        String reference = null;
        //noinspection OverlyBroadCatchBlock
        try (OutputStream outputStream = new ByteArrayOutputStream()) {
            Builder parser = new Builder(new WhiteSpaceEliminator());
            Document referenceDoc = parser.build(Paths.get(getClass().getResource("/protocol.xml").toURI()).toFile());
            Canonicalizer canonicalizer = new Canonicalizer(outputStream, Canonicalizer.EXCLUSIVE_XML_CANONICALIZATION);
            canonicalizer.write(referenceDoc);
            reference = outputStream.toString();
        } catch (ParsingException | IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(exported, reference);
        Files.delete(path);
    }

    private void replaceDateTimes(Node node) {
        //noinspection ChainOfInstanceofChecks
        if (node instanceof Text) {
            if (pattern.matcher(node.getValue()).matches()) {
                ((Text) node).setValue("2000-01-01T00:11:22+02:00");
            }
            return;
        }
        if (node instanceof Attribute) {
            if (pattern.matcher(node.getValue()).matches()) {
                ((Attribute) node).setValue("2000-01-01T00:11:22+02:00");
            }
            return;
        }
        if (node instanceof Element) {
            for (int i = 0; i < ((Element) node).getAttributeCount(); i++) {
                replaceDateTimes(((Element) node).getAttribute(i));
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            replaceDateTimes(node.getChild(i));
        }
    }

}
