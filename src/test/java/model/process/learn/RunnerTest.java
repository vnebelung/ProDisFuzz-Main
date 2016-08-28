/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:29.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.protocol.ProtocolBlock.Type;
import model.protocol.ProtocolFile;
import model.protocol.ProtocolStructure;
import org.testng.Assert;
import org.testng.annotations.Test;
import support.RunnerMonitor;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("HardCodedStringLiteral")
public class RunnerTest {

    @Test
    public void testRun() throws URISyntaxException {
        Set<ProtocolFile> protocolFiles = new HashSet<>(2);
        protocolFiles.add(new ProtocolFile(Paths.get(getClass().getResource("/capture1.txt").toURI())));
        protocolFiles.add(new ProtocolFile(Paths.get(getClass().getResource("/capture2.txt").toURI())));
        Runner runner = new Runner(protocolFiles);
        RunnerMonitor monitor = new RunnerMonitor();
        runner.addObserver(monitor);
        runner.run();
        Assert.assertTrue(monitor.areAllStatesVisited());
    }

    @Test
    public void testGetProtocolStructure() throws URISyntaxException {
        Set<ProtocolFile> protocolFiles = new HashSet<>(2);
        protocolFiles.add(new ProtocolFile(Paths.get(getClass().getResource("/capture1.txt").toURI())));
        protocolFiles.add(new ProtocolFile(Paths.get(getClass().getResource("/capture2.txt").toURI())));
        Runner runner = new Runner(protocolFiles);
        Assert.assertEquals(runner.getProtocolStructure().getSize(), 0);

        runner.run();
        ProtocolStructure actual = runner.getProtocolStructure();
        Assert.assertEquals(actual.getSize(), 2);
        Assert.assertEquals(actual.getBlock(0).getType(), Type.VAR);
        Assert.assertEquals(actual.getBlock(1).getType(), Type.FIX);
        Assert.assertEquals(actual.getBlock(0).getBytes().length, 10);
        for (Byte each : actual.getBlock(0).getBytes()) {
            Assert.assertNull(each);
        }
        Assert.assertEquals(actual.getBlock(1).getBytes().length, 1);
        Assert.assertNotNull(actual.getBlock(1).getBytes()[0]);
    }
}
