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

public class DiceValueCalculatorTest {

    @Test
    public void testCall() throws Exception {
        List<Byte> bytes1 = new ArrayList<>(10);
        for (byte b = 0; b < 10; b++) {
            bytes1.add(b);
        }
        List<Byte> bytes2 = new ArrayList<>(10);
        for (byte b = 4; b < 14; b++) {
            bytes2.add(b);
        }

        LearnSequence sequence1 = new LearnSequence(bytes1, new NGramCreator(bytes1, 3).call());
        LearnSequence sequence2 = new LearnSequence(bytes1, new NGramCreator(bytes2, 3).call());
        DiceValueCalculator diceValueCalculator = new DiceValueCalculator(sequence1, sequence2);
        Assert.assertEquals(diceValueCalculator.call(), (2 * 4.0) / (12 + 12), 0.000000001);

        diceValueCalculator = new DiceValueCalculator(sequence1, sequence1);
        Assert.assertEquals(diceValueCalculator.call(), (2 * 12.0) / (12 + 12));
    }
}
