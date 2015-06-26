/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.import_;

import model.protocol.ProtocolBlock.Type;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;

public class ImportProcessTest {

    @Test
    public void testImportXML() throws URISyntaxException {
        ImportProcess importProcess = new ImportProcess();
        //noinspection HardCodedStringLiteral
        importProcess.importXML(Paths.get(getClass().getResource("/protocol.xml").toURI()));
        Assert.assertEquals(importProcess.getProtocolStructure().getSize(), 5);

        Byte[] b0 = {0, 17};
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(0).getType(), Type.FIX);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(0).getMinLength(), b0.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(0).getMaxLength(), b0.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(0).getBytes(), b0);

        Byte[] b1 = {null, null, null};
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(1).getType(), Type.VAR);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(1).getMinLength(), b1.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(1).getMaxLength(), b1.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(1).getBytes(), b1);

        Byte[] b2 = {0};
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(2).getType(), Type.FIX);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(2).getMinLength(), b2.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(2).getMaxLength(), b2.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(2).getBytes(), b2);

        Byte[] b3 = {null};
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(3).getType(), Type.VAR);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(3).getMinLength(), b3.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(3).getMaxLength(), b3.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(3).getBytes(), b3);

        Byte[] b4 = {0, 17, 34};
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(4).getType(), Type.FIX);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(4).getMinLength(), b4.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(4).getMaxLength(), b4.length);
        Assert.assertEquals(importProcess.getProtocolStructure().getBlock(4).getBytes(), b4);
    }

    @Test
    public void testIsImported() throws URISyntaxException {
        ImportProcess importProcess = new ImportProcess();
        Assert.assertFalse(importProcess.isImported());
        //noinspection HardCodedStringLiteral
        importProcess.importXML(Paths.get(getClass().getResource("/protocol.xml").toURI()));
        Assert.assertTrue(importProcess.isImported());
    }
}
