/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.util;

import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings({"ZeroLengthArrayAllocation", "HardCodedStringLiteral"})
public class HexTest {

    @Test
    public void testByte2Hex() throws Exception {
        Assert.assertEquals(Hex.byte2HexBin((byte) 0), "00");
        Assert.assertEquals(Hex.byte2HexBin(new byte[]{0, 127, -128}), "007f80");
    }

    @Test
    public void testHex2Byte() throws Exception {
        Assert.assertEquals(Hex.hexBin2Byte("007f80"), new byte[]{0, 127, -128});

        Assert.assertEquals(Hex.hexBin2Byte("7F"), new byte[]{127});
        Assert.assertEquals(Hex.hexBin2Byte("007f800"), new byte[]{});
        Assert.assertEquals(Hex.hexBin2Byte("0z"), new byte[]{});

        Assert.assertEquals(Hex.hexBin2Byte("7f"), new byte[]{127});
    }
}
