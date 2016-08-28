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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import support.DocumentComparer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("HardCodedStringLiteral")
public class XmlExchangeTest {

    private Document document;

    @BeforeClass
    public void setUp() {
        Element prodisfuzz = new Element("prodisfuzz");
        prodisfuzz.addAttribute(new Attribute("datetime", "2000-01-01T00:11:22+02:00"));
        Element protocolblocks = new Element("protocolblocks");
        Element blockfix1 = new Element("blockfix");
        blockfix1.addAttribute(new Attribute("minlength", "2"));
        blockfix1.addAttribute(new Attribute("maxlength", "2"));
        Element content1 = new Element("content");
        content1.appendChild("0011");
        blockfix1.appendChild(content1);
        protocolblocks.appendChild(blockfix1);
        Element blockvar1 = new Element("blockvar");
        blockvar1.addAttribute(new Attribute("minlength", "3"));
        blockvar1.addAttribute(new Attribute("maxlength", "3"));
        protocolblocks.appendChild(blockvar1);
        Element blockfix2 = new Element("blockfix");
        blockfix2.addAttribute(new Attribute("minlength", "1"));
        blockfix2.addAttribute(new Attribute("maxlength", "1"));
        Element content2 = new Element("content");
        content2.appendChild("00");
        blockfix2.appendChild(content2);
        protocolblocks.appendChild(blockfix2);
        Element blockvar2 = new Element("blockvar");
        blockvar2.addAttribute(new Attribute("minlength", "1"));
        blockvar2.addAttribute(new Attribute("maxlength", "1"));
        protocolblocks.appendChild(blockvar2);
        Element blockfix3 = new Element("blockfix");
        blockfix3.addAttribute(new Attribute("minlength", "3"));
        blockfix3.addAttribute(new Attribute("maxlength", "3"));
        Element content3 = new Element("content");
        content3.appendChild("001122");
        blockfix3.appendChild(content3);
        protocolblocks.appendChild(blockfix3);
        prodisfuzz.appendChild(protocolblocks);
        document = new Document(prodisfuzz);
    }

    @Test
    public void testLoad() throws URISyntaxException {
        Document actual = XmlExchange.load(Paths.get(getClass().getResource("/protocol.xml").toURI()));
        Assert.assertTrue(DocumentComparer.areEqual(actual, document));
    }

    @Test
    public void testSave() throws IOException {
        Path path = Files.createTempFile(null, null);
        XmlExchange.save(document, path);
        Assert.assertTrue(DocumentComparer.areEqual(XmlExchange.load(path), document));
        Files.delete(path);
    }

}
