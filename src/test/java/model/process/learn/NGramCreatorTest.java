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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NGramCreatorTest {

    @Test
    public void testCall() throws Exception {
        List<Byte> bytes1 = new ArrayList<>(10);
        for (byte b = 0; b < 10; b++) {
            bytes1.add(b);
        }
        Set<String> reference1 = new HashSet<>();
        reference1.add("- - " + (byte) 0);
        reference1.add("- " + (byte) 0 + ' ' + (byte) 1);
        reference1.add((byte) 0 + " " + (byte) 1 + ' ' + (byte) 2);
        reference1.add((byte) 1 + " " + (byte) 2 + ' ' + (byte) 3);
        reference1.add((byte) 2 + " " + (byte) 3 + ' ' + (byte) 4);
        reference1.add((byte) 3 + " " + (byte) 4 + ' ' + (byte) 5);
        reference1.add((byte) 4 + " " + (byte) 5 + ' ' + (byte) 6);
        reference1.add((byte) 5 + " " + (byte) 6 + ' ' + (byte) 7);
        reference1.add((byte) 6 + " " + (byte) 7 + ' ' + (byte) 8);
        reference1.add((byte) 7 + " " + (byte) 8 + ' ' + (byte) 9);
        reference1.add((byte) 8 + " " + (byte) 9 + " -");
        reference1.add((byte) 9 + " - -");
        NGramCreator nGramCreator = new NGramCreator(bytes1, 3);
        Assert.assertEquals(nGramCreator.call(), reference1);

        Set<String> reference2 = new HashSet<>();
        reference2.add("- - - " + (byte) 0);
        reference2.add("- - " + (byte) 0 + ' ' + (byte) 1);
        reference2.add("- " + (byte) 0 + ' ' + (byte) 1 + ' ' + (byte) 2);
        reference2.add((byte) 0 + " " + (byte) 1 + ' ' + (byte) 2 + ' ' + (byte) 3);
        reference2.add((byte) 1 + " " + (byte) 2 + ' ' + (byte) 3 + ' ' + (byte) 4);
        reference2.add((byte) 2 + " " + (byte) 3 + ' ' + (byte) 4 + ' ' + (byte) 5);
        reference2.add((byte) 3 + " " + (byte) 4 + ' ' + (byte) 5 + ' ' + (byte) 6);
        reference2.add((byte) 4 + " " + (byte) 5 + ' ' + (byte) 6 + ' ' + (byte) 7);
        reference2.add((byte) 5 + " " + (byte) 6 + ' ' + (byte) 7 + ' ' + (byte) 8);
        reference2.add((byte) 6 + " " + (byte) 7 + ' ' + (byte) 8 + ' ' + (byte) 9);
        reference2.add((byte) 7 + " " + (byte) 8 + ' ' + (byte) 9 + " -");
        reference2.add((byte) 8 + " " + (byte) 9 + " - -");
        reference2.add((byte) 9 + " - - -");
        nGramCreator = new NGramCreator(bytes1, 4);
        Assert.assertEquals(nGramCreator.call(), reference2);
    }
}
