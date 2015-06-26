/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.protocol.ProtocolStructure;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class LearnStructureCallableTest {

    @Test
    public void testCall() throws Exception {
        List<Byte> block1 = new ArrayList<>();
        block1.add(null);
        block1.add(null);

        List<Byte> block2 = new ArrayList<>();
        block2.add((byte) 48);
        block2.add((byte) 48);

        List<Byte> block3 = new ArrayList<>();
        block3.add(null);

        List<Byte> block4 = new ArrayList<>();
        block4.add((byte) 48);

        ProtocolStructure reference = new ProtocolStructure();
        reference.addBlock(block1);
        reference.addBlock(block2);
        reference.addBlock(block3);
        reference.addBlock(block4);

        List<Byte> bytes = new ArrayList<>(6);
        bytes.addAll(block1);
        bytes.addAll(block2);
        bytes.addAll(block3);
        bytes.addAll(block4);

        LearnStructureCallable learnStructureCallable = new LearnStructureCallable(bytes);
        ProtocolStructure protocolStructure = learnStructureCallable.call();

        Assert.assertEquals(protocolStructure.getSize(), reference.getSize());
        for (int i = 0; i < reference.getSize(); i++) {
            Assert.assertEquals(protocolStructure.getBlock(i).getBytes(), reference.getBlock(i).getBytes());
            Assert.assertEquals(protocolStructure.getBlock(i).getMaxLength(), reference.getBlock(i).getMaxLength());
            Assert.assertEquals(protocolStructure.getBlock(i).getMinLength(), reference.getBlock(i).getMinLength());
            Assert.assertEquals(protocolStructure.getBlock(i).getType(), reference.getBlock(i).getType());
        }
    }
}
