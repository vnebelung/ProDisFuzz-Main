/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.xml;

import model.util.XmlExchange;
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
