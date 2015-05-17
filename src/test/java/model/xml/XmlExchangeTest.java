package model.xml;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings({"HardCodedStringLiteral", "ConstantConditions", "AccessOfSystemProperties"})
public class XmlExchangeTest {

    @Test
    public void testImportXml() throws IOException {
        Element element = new Element("root");
        element.addAttribute(new Attribute("name", "root"));
        element.appendChild(new Element("child1"));
        element.appendChild(new Element("child2"));
        Document document = new Document(element);
        Path path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Document doc = XmlExchange.importXml(path);
        Assert.assertEquals(doc.toXML(), document.toXML());
        Files.delete(path);
    }

    @Test
    public void testExportXML() throws IOException {
        Element element = new Element("root");
        element.addAttribute(new Attribute("name", "root"));
        element.appendChild(new Element("child1"));
        element.appendChild(new Element("child2"));
        Document document = new Document(element);
        Path path = Paths.get(System.getProperty("java.io.tmpdir"));
        Assert.assertFalse(XmlExchange.exportXML(document, path));
        path = path.resolve("tmp");
        Assert.assertTrue(XmlExchange.exportXML(document, path));
        Files.deleteIfExists(path);
    }
}
