/*
 * This file is part of ProDisFuzz, modified on 28.06.15 01:22.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import model.process.fuzzOptions.FuzzOptionsProcess.InjectionMethod;
import model.protocol.InjectedProtocolStructure;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings({"OverlyComplexMethod", "NumericCastThatLosesPrecision"})
public class FuzzingMessageCallableTest {

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Test
    public void testCall() throws Exception {
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure();
        Byte[] b1 = {48};
        Byte[] b2 = {null};
        injectedProtocolStructure.addBlock(b1);
        injectedProtocolStructure.addBlock(b2);
        injectedProtocolStructure.addBlock(b1);
        injectedProtocolStructure.addBlock(b2);

        // Test SIMULTANEOUS
        //noinspection UnqualifiedInnerClassAccess
        FuzzingMessageCallable fuzzingMessageCallable = new FuzzingMessageCallable(injectedProtocolStructure,
                InjectionMethod.SIMULTANEOUS);
        byte[] answer1 = fuzzingMessageCallable.call();
        for (int i = 0; i < (answer1.length / 2); i++) {
            // Test whether the same random values are inserted in both variable parts
            Assert.assertEquals(answer1[i], answer1[i + (answer1.length / 2)]);
        }
        byte[] answer2 = fuzzingMessageCallable.call();
        // Test whether the fixed parts are still the there
        Assert.assertEquals(b1[0], Byte.valueOf(answer1[0]));
        Assert.assertEquals(b1[0], Byte.valueOf(answer1[answer1.length / 2]));
        Assert.assertEquals(b1[0], Byte.valueOf(answer2[0]));
        Assert.assertEquals(b1[0], Byte.valueOf(answer2[answer2.length / 2]));
        int count = 0;
        for (int i = b1.length; i < Math.min(answer1.length, answer2.length); i++) {
            count += answer1[i] == answer2[i] ? -1 : 1;
        }
        // Test whether the first variable part contains random bytes
        Assert.assertTrue(count > 0);

        //noinspection HardCodedStringLiteral
        Path library = Paths.get(getClass().getResource("/library.txt").toURI());
        injectedProtocolStructure.getVarBlock(0).setLibraryInjection();
        injectedProtocolStructure.getVarBlock(0).setLibrary(library);
        injectedProtocolStructure.getVarBlock(1).setLibraryInjection();
        injectedProtocolStructure.getVarBlock(1).setLibrary(library);
        for (int i = 0; i < 5; i++) {
            byte[] answer3 = fuzzingMessageCallable.call();
            // Tests whether the correct bytes are present
            Assert.assertEquals(b1[0], Byte.valueOf(answer3[0]));
            Assert.assertEquals((byte) (65 + i), answer3[1]);
            Assert.assertEquals(b1[0], Byte.valueOf(answer3[2]));
            Assert.assertEquals((byte) (65 + i), answer3[3]);
        }

        // Test SEPARATE

        count = 0;
        byte[] answer4;
        for (int j = 0; j < 100; j++) {
            fuzzingMessageCallable = new FuzzingMessageCallable(injectedProtocolStructure, InjectionMethod.SEPARATE);
            for (int i = 0; i < 5; i++) {
                answer4 = fuzzingMessageCallable.call();
                // Tests whether the correct fixed bytes are present
                Assert.assertEquals(b1[0], Byte.valueOf(answer4[0]));
                Assert.assertEquals(b1[0], Byte.valueOf(answer4[2]));
                // Test whether the first variable block is the correct library line
                Assert.assertEquals(answer4[1], (byte) (65 + i));
                // Test whether the second variable block is a library line
                Assert.assertTrue((answer4[3] > 64) && (answer4[3] < 70));
                count += answer4[3] == answer4[1] ? -1 : 1;
            }
            for (int i = 0; i < 5; i++) {
                answer4 = fuzzingMessageCallable.call();
                // Tests whether the correct fixed bytes are present
                Assert.assertEquals(b1[0], Byte.valueOf(answer4[0]));
                Assert.assertEquals(b1[0], Byte.valueOf(answer4[2]));
                // Test whether the first variable block is the correct library line
                Assert.assertEquals(answer4[3], (byte) (65 + i));
                // Test whether the second variable block is a library line
                Assert.assertTrue((answer4[1] > 64) && (answer4[1] < 70));
                count += answer4[3] == answer4[1] ? -1 : 1;
            }
            Assert.assertEquals(fuzzingMessageCallable.call(), null);
        }
        // Test whether the second variable block is different from the first one
        Assert.assertTrue(count > 0);

        fuzzingMessageCallable = new FuzzingMessageCallable(injectedProtocolStructure, InjectionMethod.SEPARATE);

        injectedProtocolStructure.getVarBlock(0).setRandomInjection();
        injectedProtocolStructure.getVarBlock(1).setRandomInjection();
        byte[] answer5 = fuzzingMessageCallable.call();
        count = 0;
        for (int i = 0; i < (answer5.length / 2); i++) {
            count += (answer5[i] == answer5[(answer5.length / 2) + i]) ? -1 : 1;
        }
        // Test whether the first variable block is different from the second one
        Assert.assertTrue(count > 0);

        // Test mixed mode
        injectedProtocolStructure.getVarBlock(0).setLibraryInjection();
        //noinspection HardCodedStringLiteral
        injectedProtocolStructure.getVarBlock(0).setLibrary(Paths.get(getClass().getResource("/library.txt").toURI()));
        byte[] answer6 = fuzzingMessageCallable.call();
        // Tests whether the correct fixed bytes are present
        Assert.assertEquals(b1[0], Byte.valueOf(answer6[0]));
        Assert.assertEquals(b1[0], Byte.valueOf(answer6[2]));
        // Test whether the first variable block is the correct library line
        Assert.assertTrue((answer6[1] > 64) && (answer6[1] < 70));
        Assert.assertTrue((answer6.length - 3) > 0);
    }


}
