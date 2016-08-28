/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LearnSequenceTest {

    @SuppressWarnings("EmptyMethod")
    @Test
    public void testAddDistanceTo() throws Exception {
        // GetDistanceTo
    }

    @Test
    public void testGetDistanceTo() throws Exception {
        LearnSequence learnSequence = new LearnSequence(new ArrayList<>(0), new HashSet<>(0));
        LearnSequence neighbor = new LearnSequence(new ArrayList<>(0), new HashSet<>(0));
        learnSequence.addDistanceTo(neighbor, 0.75);
        Assert.assertEquals(learnSequence.getDistanceTo(neighbor), 0.25);

        Assert.assertEquals(learnSequence.getDistanceTo(new LearnSequence(new ArrayList<>(0), new HashSet<>(0))), -1.0);
    }

    @Test
    public void testRemoveDistanceTo() throws Exception {
        LearnSequence learnSequence = new LearnSequence(new ArrayList<>(0), new HashSet<>(0));
        LearnSequence neighbor = new LearnSequence(new ArrayList<>(0), new HashSet<>(0));
        learnSequence.addDistanceTo(neighbor, 0.75);
        learnSequence.removeDistanceTo(neighbor);

        Assert.assertEquals(learnSequence.getDistanceTo(neighbor), -1.0);
    }

    @Test
    public void testGetSequence() throws Exception {
        List<Byte> bytes = new ArrayList<>(5);
        for (byte b = 0; b < 5; b++) {
            bytes.add(b);
        }
        LearnSequence learnSequence = new LearnSequence(bytes, new HashSet<>(0));
        Assert.assertEquals(learnSequence.getSequence(), bytes);
    }

    @Test
    public void testGetNGrams() throws Exception {
        List<Byte> bytes = new ArrayList<>(5);
        bytes.add((byte) 0);
        bytes.add((byte) 0);
        bytes.add((byte) 1);
        bytes.add((byte) 1);
        bytes.add((byte) 1);
        bytes.add((byte) 1);
        bytes.add((byte) 1);
        Set<String> ngrams = new NGramCreator(bytes, 3).call();
        LearnSequence learnSequence = new LearnSequence(bytes, ngrams);
        Assert.assertEquals(learnSequence.getNGrams(), ngrams);
    }

    @Test
    public void testGetAverageDistance() throws Exception {
        LearnSequence learnSequence = new LearnSequence(new ArrayList<>(0), new HashSet<>(0));
        learnSequence.addDistanceTo(new LearnSequence(new ArrayList<>(0), new HashSet<>(0)), 0.25);
        learnSequence.addDistanceTo(new LearnSequence(new ArrayList<>(0), new HashSet<>(0)), 0.1);
        learnSequence.addDistanceTo(new LearnSequence(new ArrayList<>(0), new HashSet<>(0)), 0.335);
        learnSequence.addDistanceTo(new LearnSequence(new ArrayList<>(0), new HashSet<>(0)), 0.015);
        Assert.assertEquals(learnSequence.getAverageDistance(), 1.1, 0.000000001);
    }

    @Test
    public void testGetCombinedDistanceTo() throws Exception {
        LearnSequence learnSequence = new LearnSequence(new ArrayList<>(0), new HashSet<>(0));
        LearnSequence neighbor1 = new LearnSequence(new ArrayList<>(0), new HashSet<>(0));
        LearnSequence neighbor2 = new LearnSequence(new ArrayList<>(0), new HashSet<>(0));
        Assert.assertEquals(learnSequence.getCombinedDistanceTo(neighbor1), Double.MAX_VALUE);

        learnSequence.addDistanceTo(neighbor1, 0.2);
        learnSequence.addDistanceTo(neighbor2, 0.5);
        neighbor1.addDistanceTo(learnSequence, 0.2);
        neighbor1.addDistanceTo(neighbor2, 0.9);
        neighbor2.addDistanceTo(learnSequence, 0.5);
        neighbor2.addDistanceTo(neighbor1, 0.9);

        Assert.assertEquals(learnSequence.getCombinedDistanceTo(neighbor1), -1.4, 0.000000001);
        Assert.assertEquals(learnSequence.getCombinedDistanceTo(neighbor1), -1.4, 0.000000001);
    }
}
