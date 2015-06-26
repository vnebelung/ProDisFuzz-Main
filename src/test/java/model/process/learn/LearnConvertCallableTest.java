/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.ProtocolFile;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class LearnConvertCallableTest {

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Test
    public void testCall() throws Exception {
        ProtocolFile[] protocolFiles = new ProtocolFile[2];
        //noinspection HardCodedStringLiteral
        protocolFiles[0] = new ProtocolFile(Paths.get(getClass().getResource("/capture1.txt").toURI()));
        //noinspection HardCodedStringLiteral
        protocolFiles[1] = new ProtocolFile(Paths.get(getClass().getResource("/capture2.txt").toURI()));
        Callable<List<List<Byte>>> learnConvertCallable = new LearnConvertCallable(protocolFiles);
        //noinspection TypeMayBeWeakened
        List<List<Byte>> reference = new ArrayList<>();
        List<Byte> bytes = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            //noinspection CharUsedInArithmeticContext,NumericCastThatLosesPrecision
            bytes.add((byte) ('0' + i));
        }
        //noinspection HardcodedLineSeparator
        bytes.add((byte) '\n');
        reference.add(bytes);
        bytes = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            //noinspection CharUsedInArithmeticContext,NumericCastThatLosesPrecision
            bytes.add((byte) ('a' + i));
        }
        //noinspection HardcodedLineSeparator
        bytes.add((byte) '\n');
        reference.add(bytes);
        Assert.assertEquals(learnConvertCallable.call(), reference);
    }
}
