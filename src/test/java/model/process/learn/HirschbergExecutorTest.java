/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HirschbergExecutorTest {

    @Test
    public void testCall() throws Exception {
        List<Byte> sequence1 = new ArrayList<>();
        sequence1.add(null);
        sequence1.add(null);
        sequence1.add(null);
        sequence1.add(null);
        List<Byte> sequence2 = new ArrayList<>();
        sequence2.add((byte) 'a');
        sequence2.add((byte) 'b');
        sequence2.add((byte) 'b');
        sequence2.add((byte) 'b');
        sequence2.add((byte) 'c');
        sequence2.add((byte) 'c');
        sequence2.add((byte) 'c');
        List<Byte> sequence3 = new ArrayList<>();
        sequence3.add((byte) 'b');
        sequence3.add((byte) 'b');
        sequence3.add((byte) 'c');
        List<Byte> sequence4 = new ArrayList<>();
        sequence4.add((byte) 'a');
        sequence4.add((byte) 'b');
        sequence4.add((byte) 'c');
        sequence4.add((byte) 'c');
        List<Byte> sequence5 = new ArrayList<>();
        sequence5.add((byte) 'b');
        sequence5.add(null);
        sequence5.add(null);
        sequence5.add((byte) 'b');
        sequence5.add((byte) 'c');
        sequence5.add(null);

        Collection<Byte> reference = new ArrayList<>();
        for (int i = 0; i < Math.max(sequence1.size(), sequence2.size()); i++) {
            reference.add(null);
        }
        HirschbergExecutor hirschbergExecutor = new HirschbergExecutor(sequence1, sequence2);
        Assert.assertEquals(hirschbergExecutor.call(), reference);

        reference = new ArrayList<>();
        for (int i = 0; i < Math.max(sequence1.size(), sequence3.size()); i++) {
            reference.add(null);
        }
        hirschbergExecutor = new HirschbergExecutor(sequence1, sequence3);
        Assert.assertEquals(hirschbergExecutor.call(), reference);

        reference = new ArrayList<>();
        reference.add(null);
        reference.add(null);
        reference.add((byte) 'b');
        reference.add((byte) 'b');
        reference.add(null);
        reference.add(null);
        reference.add((byte) 'c');
        hirschbergExecutor = new HirschbergExecutor(sequence2, sequence3);
        Assert.assertEquals(hirschbergExecutor.call(), reference);

        reference = new ArrayList<>();
        reference.add((byte) 'a');
        reference.add(null);
        reference.add(null);
        reference.add((byte) 'b');
        reference.add(null);
        reference.add((byte) 'c');
        reference.add((byte) 'c');
        hirschbergExecutor = new HirschbergExecutor(sequence2, sequence4);
        Assert.assertEquals(hirschbergExecutor.call(), reference);

        reference = new ArrayList<>();
        reference.add(null);
        reference.add((byte) 'b');
        reference.add(null);
        reference.add(null);
        reference.add(null);
        reference.add((byte) 'c');
        reference.add(null);
        hirschbergExecutor = new HirschbergExecutor(sequence2, sequence5);
        Assert.assertEquals(hirschbergExecutor.call(), reference);

        reference = new ArrayList<>();
        reference.add(null);
        reference.add((byte) 'b');
        reference.add(null);
        reference.add((byte) 'c');
        hirschbergExecutor = new HirschbergExecutor(sequence3, sequence4);
        Assert.assertEquals(hirschbergExecutor.call(), reference);

        reference = new ArrayList<>();
        reference.add((byte) 'b');
        reference.add(null);
        reference.add(null);
        reference.add((byte) 'b');
        reference.add((byte) 'c');
        reference.add(null);
        hirschbergExecutor = new HirschbergExecutor(sequence3, sequence5);
        Assert.assertEquals(hirschbergExecutor.call(), reference);

        reference = new ArrayList<>();
        reference.add(null);
        reference.add(null);
        reference.add(null);
        reference.add((byte) 'b');
        reference.add((byte) 'c');
        reference.add(null);
        hirschbergExecutor = new HirschbergExecutor(sequence4, sequence5);
        Assert.assertEquals(hirschbergExecutor.call(), reference);
    }
}
