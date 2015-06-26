/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.protocol;

import model.protocol.ProtocolBlock.Type;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ProtocolBlockTest {

    private ProtocolBlock protocolBlock;
    private ProtocolBlock protocolBlockNull;
    private Byte[] bytes = {48, 49, 50};
    private Byte[] bytesNull = {null, null};

    @BeforeMethod
    public void setUp() throws Exception {
        protocolBlock = new ProtocolBlock(Type.FIX, bytes);
        protocolBlockNull = new ProtocolBlock(Type.VAR, bytesNull);
    }

    @Test
    public void testGetType() throws Exception {
        Assert.assertEquals(protocolBlock.getType(), Type.FIX);
        Assert.assertEquals(protocolBlockNull.getType(), Type.VAR);
    }

    @Test
    public void testGetMinLength() throws Exception {
        Assert.assertEquals(protocolBlock.getMinLength(), bytes.length);
        Assert.assertEquals(protocolBlockNull.getMinLength(), bytesNull.length);
    }

    @Test
    public void testGetMaxLength() throws Exception {
        Assert.assertEquals(protocolBlock.getMaxLength(), bytes.length);
        Assert.assertEquals(protocolBlockNull.getMaxLength(), bytesNull.length);
    }

    @Test
    public void testGetBytes() throws Exception {
        Assert.assertEquals(protocolBlock.getBytes(), bytes);
        Assert.assertEquals(protocolBlockNull.getBytes(), bytesNull);
    }
}
