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

public class InjectedProtocolStructureTest {

    @Test
    public void testAddBlock() throws Exception {
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure();
        Byte[] bytes1 = {null};
        Assert.assertEquals(injectedProtocolStructure.getSize(), 0);
        Assert.assertEquals(injectedProtocolStructure.getVarSize(), 0);

        injectedProtocolStructure.addBlock(bytes1);
        Assert.assertEquals(injectedProtocolStructure.getSize(), 1);
        Assert.assertEquals(injectedProtocolStructure.getVarSize(), 1);

        Byte[] bytes2 = {48};
        injectedProtocolStructure.addBlock(bytes2);
        Assert.assertEquals(injectedProtocolStructure.getSize(), 2);
        Assert.assertEquals(injectedProtocolStructure.getVarSize(), 1);

        Byte[] bytes3 = {48, null};
        injectedProtocolStructure.addBlock(bytes3);
        Assert.assertEquals(injectedProtocolStructure.getSize(), 2);
        Assert.assertEquals(injectedProtocolStructure.getVarSize(), 1);

        Assert.assertEquals(injectedProtocolStructure.getBlock(1).getBytes(), bytes2);
        Assert.assertEquals(injectedProtocolStructure.getVarBlock(0).getBytes(), bytes1);
    }

    @Test
    public void testClear() throws Exception {
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure();
        Byte[] bytes1 = {null};
        injectedProtocolStructure.addBlock(bytes1);
        Assert.assertEquals(injectedProtocolStructure.getSize(), 1);
        injectedProtocolStructure.clear();
        Assert.assertEquals(injectedProtocolStructure.getSize(), 0);
    }

    @Test
    public void testToProtocolStructure() throws Exception {
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure();
        Byte[] bytes1 = {null};
        injectedProtocolStructure.addBlock(bytes1);
        Byte[] bytes2 = {48};
        injectedProtocolStructure.addBlock(bytes2);
        ProtocolStructure protocolStructure = injectedProtocolStructure.toProtocolStructure();
        Assert.assertEquals(protocolStructure.getSize(), 2);
        Assert.assertEquals(protocolStructure.getBlock(1).getBytes(), bytes2);
        Assert.assertEquals(protocolStructure.getBlock(0).getBytes(), bytes1);
    }
}
