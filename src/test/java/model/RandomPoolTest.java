/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class RandomPoolTest {

    @Test
    public void testNextBloatBytes() throws Exception {
        for (int i = 0; i < 100; i++) {
            List<Byte> bytes = RandomPool.getInstance().nextBloatBytes(i);
            Assert.assertTrue(bytes.size() < ((i * 10000) + 1));
        }
    }
}
