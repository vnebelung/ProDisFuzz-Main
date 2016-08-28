/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.util;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("HardCodedStringLiteral")
public class XmlSchemaTest {

    private Path tmp;

    @BeforeClass
    public void setUp() throws IOException {
        tmp = Files.createTempFile(null, null);
    }

    @AfterClass
    public void tearDown() throws IOException {
        Files.delete(tmp);
    }

    @Test
    public void testValidateUpdateInformation() throws URISyntaxException, IOException {

        Document original = XmlExchange.load(Paths.get(getClass().getResource("/releases.xml").toURI()));
        XmlExchange.save(original, tmp);
        Assert.assertTrue(XmlSchema.validateUpdateInformation(tmp));

        // XML element update

        //noinspection ConstantConditions
        Document manipulated = original.copy().getDocument();
        Element element = manipulated.getRootElement();
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement();
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement();
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        // XML element releases

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases");
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        // XML element release

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release");
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        // XML element number

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("number");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("number");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("number");
        element.appendChild("dummy");
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("number");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        // XML element name

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("name");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("name");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("name");
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("name");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        // XML element date

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("date");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("date");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("date");
        element.appendChild("dummy");
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("date");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        // XML element requirements

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("requirements");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("requirements");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("requirements");
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("requirements");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        // XML element information

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information");
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        // XML element name

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information").getFirstChildElement("item");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information").getFirstChildElement("item");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information").getFirstChildElement("item");
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information").getFirstChildElement("item");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        // XML element signature

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("signature");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("signature");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("signature");
        element.appendChild("dummy");
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("signature");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));
    }

    @Test
    public void testValidateProtocol() throws IOException, URISyntaxException {
        Document original = XmlExchange.load(Paths.get(getClass().getResource("/protocol.xml").toURI()));
        XmlExchange.save(original, tmp);
        Assert.assertTrue(XmlSchema.validateProtocol(tmp));

        // XML element prodisfuzz

        //noinspection ConstantConditions
        Document manipulated = original.copy().getDocument();
        Element element = manipulated.getRootElement();
        element.removeAttribute(element.getAttribute("datetime"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement();
        element.addAttribute(new Attribute("datetime", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement();
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement();
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement();
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        // XML element protocolblocks

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("protocolblocks");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("protocolblocks");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("protocolblocks");
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("protocolblocks");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        //XML element blockfix

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockfix").get(0);
        element.removeAttribute(element.getAttribute("minlength"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockfix").get(0);
        element.addAttribute(new Attribute("minlength", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockfix").get(0);
        element.removeAttribute(element.getAttribute("maxlength"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockfix").get(0);
        element.addAttribute(new Attribute("maxlength", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockfix").get(0);
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockfix").get(0);
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockfix").get(0);
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        // XML element content

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockfix").get(0)
                        .getFirstChildElement("content");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockfix").get(0)
                        .getFirstChildElement("content");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockfix").get(0)
                        .getFirstChildElement("content");
        element.appendChild("dummy");
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockfix").get(0)
                        .getFirstChildElement("content");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        // XML element blockvar

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockvar").get(0);
        element.removeAttribute(element.getAttribute("minlength"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockvar").get(0);
        element.addAttribute(new Attribute("minlength", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockvar").get(0);
        element.removeAttribute(element.getAttribute("maxlength"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockvar").get(0);
        element.addAttribute(new Attribute("maxlength", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockvar").get(0);
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));

        manipulated = original.copy().getDocument();
        element =
                manipulated.getRootElement().getFirstChildElement("protocolblocks").getChildElements("blockvar").get(0);
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSchema.validateProtocol(tmp));
    }

}
