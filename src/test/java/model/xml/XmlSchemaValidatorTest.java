/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.xml;

import model.util.XmlSchema;
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
        Assert.assertTrue(XmlSchema.validateUpdateInformation(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("number").detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchema.validateUpdateInformation(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("name").detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchema.validateUpdateInformation(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("date").detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchema.validateUpdateInformation(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("requirements").detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchema.validateUpdateInformation(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information").detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchema.validateUpdateInformation(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        Elements elements = document.getRootElement().getFirstChildElement("releases").getFirstChildElement
                ("release").getFirstChildElement("information").getChildElements("item");
        for (int i = 0; i < elements.size(); i++) {
            elements.get(i).detach();
        }
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchema.validateUpdateInformation(path));
        Files.delete(path);

        document = getDocument("/releases.xml");
        document.getRootElement().getFirstChildElement("signature").detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchema.validateUpdateInformation(path));
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
        Assert.assertTrue(XmlSchema.validateProtocol(path));
        Files.delete(path);

        document = getDocument("/protocol.xml");
        document.getRootElement().getFirstChildElement("protocolblocks").detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchema.validateProtocol(path));
        Files.delete(path);

        document = getDocument("/protocol.xml");
        while (document.getRootElement().getFirstChildElement("protocolblocks").getChildElements().size() > 0) {
            document.getRootElement().getFirstChildElement("protocolblocks").getChild(0).detach();
        }
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchema.validateProtocol(path));
        Files.delete(path);

        document = getDocument("/protocol.xml");
        document.getRootElement().getFirstChildElement("protocolblocks").getFirstChildElement("blockfix")
                .getFirstChildElement("content").detach();
        path = Files.createTempFile(null, null);
        Files.write(path, document.toXML().getBytes(StandardCharsets.UTF_8));
        Assert.assertFalse(XmlSchema.validateProtocol(path));
        Files.delete(path);
    }
}
