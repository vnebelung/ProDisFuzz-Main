/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.protocol;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class ProtocolStructureTest {

    @Test
    public void testAddBlock() throws Exception {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = new ArrayList<>(3);
        bytes1.add(null);
        bytes1.add(null);
        bytes1.add(null);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = new ArrayList<>(2);
        bytes2.add((byte) 48);
        bytes2.add((byte) 49);
        protocolStructure.addBlock(bytes2);
        Assert.assertEquals(protocolStructure.getBlock(0).getBytes(), bytes1.toArray());
        Assert.assertEquals(protocolStructure.getBlock(1).getBytes(), bytes2.toArray());
    }

    @Test
    public void testClear() throws Exception {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = new ArrayList<>(3);
        bytes1.add(null);
        bytes1.add(null);
        bytes1.add(null);
        protocolStructure.addBlock(bytes1);
        Assert.assertEquals(protocolStructure.getSize(), 1);
        protocolStructure.clear();
        Assert.assertEquals(protocolStructure.getSize(), 0);
    }

    @Test
    public void testGetSize() throws Exception {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        Assert.assertEquals(protocolStructure.getSize(), 0);
        List<Byte> bytes1 = new ArrayList<>(3);
        bytes1.add(null);
        bytes1.add(null);
        bytes1.add(null);
        protocolStructure.addBlock(bytes1);
        Assert.assertEquals(protocolStructure.getSize(), 1);
    }
}
