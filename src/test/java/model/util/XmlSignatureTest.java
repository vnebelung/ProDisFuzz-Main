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
import nu.xom.ParsingException;
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
public class XmlSignatureTest {

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
    public void testValidate() throws URISyntaxException, IOException, ParsingException {
        Assert.assertTrue(XmlSignature.validate(Paths.get(getClass().getResource("/releases.xml").toURI())));

        Document original = XmlExchange.load(Paths.get(getClass().getResource("/releases.xml").toURI()));

        // XML element releases

        //noinspection ConstantConditions
        Document manipulated = original.copy().getDocument();
        Element element = manipulated.getRootElement().getFirstChildElement("releases");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases");
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        // XML element release

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release");
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        // XML element number

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("number");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("number");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("number");
        element.appendChild("dummy");
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("number");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        // XML element name

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("name");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("name");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("name");
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("name");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        // XML element date

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("date");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("date");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("date");
        element.appendChild("dummy");
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("date");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        // XML element requirements

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("requirements");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("requirements");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("requirements");
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("requirements");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        // XML element information

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information");
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        // XML element name

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information").getFirstChildElement("item");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information").getFirstChildElement("item");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information").getFirstChildElement("item");
        element.appendChild(new Element("dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("releases").getFirstChildElement("release")
                .getFirstChildElement("information").getFirstChildElement("item");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        // XML element signature

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("signature");
        element.detach();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("signature");
        element.addAttribute(new Attribute("dummy", "dummy"));
        XmlExchange.save(manipulated, tmp);
        Assert.assertTrue(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("signature");
        element.appendChild("dummy");
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("signature");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        // Change signature

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("signature");
        element.removeChildren();
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("signature");
        element.appendChild(element.getValue().substring(1));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("signature");
        element.appendChild(element.getValue().replace('a', 'z'));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("signature");
        //noinspection StringConcatenationMissingWhitespace
        element.appendChild(element.getValue() + "aa");
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));

        manipulated = original.copy().getDocument();
        element = manipulated.getRootElement().getFirstChildElement("signature");
        element.appendChild(element.getValue().replace('a', 'b'));
        XmlExchange.save(manipulated, tmp);
        Assert.assertFalse(XmlSignature.validate(tmp));
    }
}
