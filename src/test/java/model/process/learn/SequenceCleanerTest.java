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
import java.util.List;

public class SequenceCleanerTest {

    @Test
    public void testCall() throws Exception {
        List<Byte> reference = new ArrayList<>(12);
        reference.add((byte) 'A');
        reference.add((byte) 'A');
        reference.add((byte) 'A');
        reference.add((byte) 'a');
        reference.add((byte) 'b');
        reference.add((byte) 'c');
        reference.add((byte) 'd');
        reference.add((byte) 'e');
        reference.add((byte) 'f');
        reference.add((byte) 'g');
        reference.add((byte) 'A');
        reference.add((byte) 'A');
        reference.add((byte) 'A');

        SequenceCleaner sequenceCleaner = new SequenceCleaner(reference);
        Assert.assertEquals(sequenceCleaner.call(), reference);

        reference.set(4, null);
        sequenceCleaner = new SequenceCleaner(reference);
        Assert.assertEquals(sequenceCleaner.call(), reference);

        reference.set(7, null);
        sequenceCleaner = new SequenceCleaner(reference);
        Assert.assertEquals(sequenceCleaner.call(), reference);

        reference.set(9, null);
        sequenceCleaner = new SequenceCleaner(reference);
        Assert.assertEquals(sequenceCleaner.call(), reference);

        reference.set(3, null);
        List<Byte> sequence = new ArrayList<>(reference);
        sequence.set(5, null);
        sequence.set(6, null);
        sequenceCleaner = new SequenceCleaner(reference);
        Assert.assertEquals(sequenceCleaner.call(), sequence);

        reference.set(8, null);
        sequence.set(8, null);
        sequenceCleaner = new SequenceCleaner(reference);
        Assert.assertEquals(sequenceCleaner.call(), sequence);
    }
}
