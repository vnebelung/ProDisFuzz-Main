/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:29.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.import_;

import model.protocol.ProtocolBlock.Type;
import org.testng.Assert;
import org.testng.annotations.Test;
import support.RunnerMonitor;

import java.net.URISyntaxException;
import java.nio.file.Paths;

@SuppressWarnings("HardCodedStringLiteral")
public class RunnerTest {

    @Test
    public void testRun() throws URISyntaxException {
        Runner runner = new Runner(Paths.get(getClass().getResource("/protocol.xml").toURI()));
        RunnerMonitor monitor = new RunnerMonitor();
        runner.addObserver(monitor);
        runner.run();
        Assert.assertEquals(runner.getProtocolStructure().getSize(), 5);

        Byte[] b0 = {0, 17};
        Assert.assertEquals(runner.getProtocolStructure().getBlock(0).getType(), Type.FIX);
        Assert.assertEquals(runner.getProtocolStructure().getBlock(0).getMinLength(), b0.length);
        Assert.assertEquals(runner.getProtocolStructure().getBlock(0).getMaxLength(), b0.length);
        Assert.assertEquals(runner.getProtocolStructure().getBlock(0).getBytes(), b0);

        Byte[] b1 = {null, null, null};
        Assert.assertEquals(runner.getProtocolStructure().getBlock(1).getType(), Type.VAR);
        Assert.assertEquals(runner.getProtocolStructure().getBlock(1).getMinLength(), b1.length);
        Assert.assertEquals(runner.getProtocolStructure().getBlock(1).getMaxLength(), b1.length);
        Assert.assertEquals(runner.getProtocolStructure().getBlock(1).getBytes(), b1);

        Byte[] b2 = {0};
        Assert.assertEquals(runner.getProtocolStructure().getBlock(2).getType(), Type.FIX);
        Assert.assertEquals(runner.getProtocolStructure().getBlock(2).getMinLength(), b2.length);
        Assert.assertEquals(runner.getProtocolStructure().getBlock(2).getMaxLength(), b2.length);
        Assert.assertEquals(runner.getProtocolStructure().getBlock(2).getBytes(), b2);

        Byte[] b3 = {null};
        Assert.assertEquals(runner.getProtocolStructure().getBlock(3).getType(), Type.VAR);
        Assert.assertEquals(runner.getProtocolStructure().getBlock(3).getMinLength(), b3.length);
        Assert.assertEquals(runner.getProtocolStructure().getBlock(3).getMaxLength(), b3.length);
        Assert.assertEquals(runner.getProtocolStructure().getBlock(3).getBytes(), b3);

        Byte[] b4 = {0, 17, 34};
        Assert.assertEquals(runner.getProtocolStructure().getBlock(4).getType(), Type.FIX);
        Assert.assertEquals(runner.getProtocolStructure().getBlock(4).getMinLength(), b4.length);
        Assert.assertEquals(runner.getProtocolStructure().getBlock(4).getMaxLength(), b4.length);
        Assert.assertEquals(runner.getProtocolStructure().getBlock(4).getBytes(), b4);

        Assert.assertTrue(monitor.areAllStatesVisited());
    }
}
