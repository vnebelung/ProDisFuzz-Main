/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class LearnSelectCallableTest {

    @Test
    public void testCall() throws Exception {
        List<List<Byte>> sequences = new ArrayList<>();
        List<Byte> bytes = new ArrayList<>();
        bytes.add((byte) 'a');
        bytes.add((byte) 'b');
        bytes.add((byte) 'c');
        sequences.add(bytes);
        bytes = new ArrayList<>();
        bytes.add((byte) 'a');
        bytes.add((byte) 'b');
        bytes.add((byte) 'b');
        bytes.add((byte) 'c');
        sequences.add(bytes);
        bytes = new ArrayList<>();
        bytes.add((byte) 'b');
        bytes.add((byte) 'b');
        bytes.add((byte) 'c');
        sequences.add(bytes);
        bytes = new ArrayList<>();
        bytes.add((byte) 'a');
        bytes.add((byte) 'b');
        bytes.add((byte) 'c');
        bytes.add((byte) 'c');
        sequences.add(bytes);
        bytes = new ArrayList<>();
        bytes.add((byte) 'b');
        bytes.add((byte) 'b');
        bytes.add((byte) 'b');
        bytes.add((byte) 'b');
        sequences.add(bytes);

        LearnSelectCallable learnSelectCallable = new LearnSelectCallable(sequences);
        int[] reference1 = {2, 3};
        Assert.assertEquals(learnSelectCallable.call(), reference1);

        sequences.remove(3);
        learnSelectCallable = new LearnSelectCallable(sequences);
        int[] reference2 = {1, 3};
        Assert.assertEquals(learnSelectCallable.call(), reference2);

        sequences.remove(1);
        learnSelectCallable = new LearnSelectCallable(sequences);
        int[] reference3 = {0, 1};
        Assert.assertEquals(learnSelectCallable.call(), reference3);
    }
}
