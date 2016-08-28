/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.export;

import model.protocol.ProtocolStructure;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("HardCodedStringLiteral")
public class XmlBuilderTest {

    private static final Pattern PATTERN =
            Pattern.compile("datetime=\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{2}:\\d{2}\"");

    @Test
    public void testCall() throws Exception {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> block1 = new ArrayList<>(2);
        block1.add((byte) 0);
        block1.add((byte) 1);
        protocolStructure.addBlock(block1);
        List<Byte> block2 = new ArrayList<>(3);
        block2.add(null);
        block2.add(null);
        block2.add(null);
        protocolStructure.addBlock(block2);
        XmlBuilder xmlBuilder = new XmlBuilder(protocolStructure);
        String actual = xmlBuilder.call().toXML();
        actual = PATTERN.matcher(actual).replaceFirst("datetime=\"2015-07-18T21:02:28+02:00\"");
        //noinspection HardcodedLineSeparator
        String reference = "<?xml version=\"1.0\"?>\n<prodisfuzz " +
                "datetime=\"2015-07-18T21:02:28+02:00\"><protocolblocks><blockfix minlength=\"2\" maxlength" +
                "=\"2\"><content>0001</content></blockfix><blockvar minlength=\"3\" " +
                "maxlength=\"3\" /></protocolblocks></prodisfuzz>\n";
        Assert.assertEquals(actual, reference);
    }
}
