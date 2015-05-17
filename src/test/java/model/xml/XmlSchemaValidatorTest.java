package model.xml;

import model.utilities.Constants;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Elements;
import nu.xom.ParsingException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("HardCodedStringLiteral")
public class XmlSchemaValidatorTest {

    @Test
    public void testValidateUpdateCheck() throws URISyntaxException, ParsingException, IOException {
        Document document = getDocument("/releases.xml");
        Path path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("release", Constants.XML_NAMESPACE_PRODISFUZZ)
                .getFirstChildElement("number", Constants.XML_NAMESPACE_PRODISFUZZ).detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("release", Constants.XML_NAMESPACE_PRODISFUZZ)
                .getFirstChildElement("name", Constants.XML_NAMESPACE_PRODISFUZZ).detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("release", Constants.XML_NAMESPACE_PRODISFUZZ)
                .getFirstChildElement("date", Constants.XML_NAMESPACE_PRODISFUZZ).detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("release", Constants.XML_NAMESPACE_PRODISFUZZ)
                .getFirstChildElement("requirements", Constants.XML_NAMESPACE_PRODISFUZZ).detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("release", Constants.XML_NAMESPACE_PRODISFUZZ)
                .getFirstChildElement("information", Constants.XML_NAMESPACE_PRODISFUZZ).detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        Elements elements = document.getRootElement().getFirstChildElement("release", Constants
                .XML_NAMESPACE_PRODISFUZZ).getFirstChildElement("information", Constants.XML_NAMESPACE_PRODISFUZZ)
                .getChildElements("item", Constants.XML_NAMESPACE_PRODISFUZZ);
        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).detach();
        }
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("Signature", Constants.XML_NAMESPACE_SIGNATURE).detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("Signature", Constants.XML_NAMESPACE_SIGNATURE)
                .getFirstChildElement("SignedInfo", Constants.XML_NAMESPACE_SIGNATURE).detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("Signature", Constants.XML_NAMESPACE_SIGNATURE)
                .getFirstChildElement("SignedInfo", Constants.XML_NAMESPACE_SIGNATURE).getFirstChildElement
                ("CanonicalizationMethod", Constants.XML_NAMESPACE_SIGNATURE).detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("Signature", Constants.XML_NAMESPACE_SIGNATURE)
                .getFirstChildElement("SignedInfo", Constants.XML_NAMESPACE_SIGNATURE).getFirstChildElement
                ("SignatureMethod", Constants.XML_NAMESPACE_SIGNATURE).detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("Signature", Constants.XML_NAMESPACE_SIGNATURE)
                .getFirstChildElement("SignedInfo", Constants.XML_NAMESPACE_SIGNATURE).getFirstChildElement
                ("Reference", Constants.XML_NAMESPACE_SIGNATURE).detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("Signature", Constants.XML_NAMESPACE_SIGNATURE)
                .getFirstChildElement("SignedInfo", Constants.XML_NAMESPACE_SIGNATURE).getFirstChildElement
                ("Reference", Constants.XML_NAMESPACE_SIGNATURE).getFirstChildElement("Transforms", Constants
                .XML_NAMESPACE_SIGNATURE).getFirstChildElement("Transform", Constants.XML_NAMESPACE_SIGNATURE).detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("Signature", Constants.XML_NAMESPACE_SIGNATURE)
                .getFirstChildElement("SignedInfo", Constants.XML_NAMESPACE_SIGNATURE).getFirstChildElement
                ("Reference", Constants.XML_NAMESPACE_SIGNATURE).getFirstChildElement("DigestMethod", Constants
                .XML_NAMESPACE_SIGNATURE).detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("Signature", Constants.XML_NAMESPACE_SIGNATURE)
                .getFirstChildElement("SignedInfo", Constants.XML_NAMESPACE_SIGNATURE).getFirstChildElement
                ("Reference", Constants.XML_NAMESPACE_SIGNATURE).getFirstChildElement("DigestValue", Constants
                .XML_NAMESPACE_SIGNATURE).detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("Signature", Constants.XML_NAMESPACE_SIGNATURE)
                .getFirstChildElement("SignatureValue", Constants.XML_NAMESPACE_SIGNATURE).detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateUpdateCheck(path));
        Files.delete(path);
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    private Document getDocument(String string) throws URISyntaxException, ParsingException, IOException {
        Builder parser = new Builder();
        return parser.build(Paths.get(getClass().getResource(string).toURI()).toFile());
    }

    @Test
    public void testValidateProtocol() throws URISyntaxException, ParsingException, IOException {
        Document document = getDocument("/protocol.xml");
        Path path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(XmlSchemaValidator.validateProtocol(path));
        Files.delete(path);

        document = getDocument("/protocol.xml");
        document.getRootElement().getFirstChildElement("protocolblocks").detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateProtocol(path));
        Files.delete(path);

        document = getDocument("/protocol.xml");
        while (document.getRootElement().getFirstChildElement("protocolblocks").getChildElements().size() > 0) {
            document.getRootElement().getFirstChildElement("protocolblocks").getChild(0).detach();
        }
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateProtocol(path));
        Files.delete(path);

        document = getDocument("/protocol.xml");
        document.getRootElement().getFirstChildElement("protocolblocks").getFirstChildElement("blockfix")
                .getFirstChildElement("content").detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchemaValidator.validateProtocol(path));
        Files.delete(path);
    }
}
