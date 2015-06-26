/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.util;

import nu.xom.Document;
import nu.xom.ParsingException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("HardCodedStringLiteral")
public class XmlSignatureTest {

    @Test
    public void testValidateSignature() throws URISyntaxException, IOException, ParsingException {
        Assert.assertTrue(XmlSignature.validate(Paths.get(getClass().getResource("/releases.xml").toURI())));

        Document document = XmlExchange.importXml(Paths.get(getClass().getResource("/releases.xml").toURI()));
        //noinspection ConstantConditions
        document.getRootElement().getChildElements().get(0).removeChild(0);
        Path path = Files.createTempFile("testng_", null);
        XmlExchange.exportXML(document, path);
        Assert.assertFalse(XmlSignature.validate(path));
        Files.delete(path);
    }
}
