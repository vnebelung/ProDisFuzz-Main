/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.protocol.ProtocolFile;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SequenceConverterTest {

    @Test
    public void testCall() throws URISyntaxException {
        //noinspection HardCodedStringLiteral
        ProtocolFile protocolFile = new ProtocolFile(Paths.get(getClass().getResource("/capture1.txt").toURI()));
        SequenceConverter sequenceConverter = new SequenceConverter(protocolFile);
        //noinspection TypeMayBeWeakened
        List<Byte> reference = new ArrayList<>(11);
        for (int i = 0; i < 10; i++) {
            //noinspection CharUsedInArithmeticContext,NumericCastThatLosesPrecision
            reference.add((byte) ('0' + i));
        }
        //noinspection HardcodedLineSeparator
        reference.add((byte) '\n');
        Assert.assertEquals(sequenceConverter.call(), reference);
    }
}
