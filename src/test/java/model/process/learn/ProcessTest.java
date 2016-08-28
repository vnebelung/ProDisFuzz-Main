/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:29.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.process.AbstractProcess.State;
import model.process.AbstractRunner.ExternalState;
import model.protocol.ProtocolBlock.Type;
import model.protocol.ProtocolFile;
import model.protocol.ProtocolStructure;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import support.ProcessMonitor;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("HardCodedStringLiteral")
public class ProcessTest {

    private model.process.learn.Process process = new model.process.learn.Process();
    private ProcessMonitor processMonitor = new ProcessMonitor(process);

    @BeforeMethod
    public void initProcess() {
        process.reset();
        processMonitor.reset();
    }

    @Test
    public void testReset() throws URISyntaxException, InterruptedException, TimeoutException {
        Set<ProtocolFile> protocolFiles = new HashSet<>(2);
        protocolFiles.add(new ProtocolFile(Paths.get(getClass().getResource("/capture1.txt").toURI())));
        protocolFiles.add(new ProtocolFile(Paths.get(getClass().getResource("/capture2.txt").toURI())));
        process.learnProtocolStructure(protocolFiles);
        processMonitor.waitForFinishAndReset();
        Assert.assertTrue(process.getProtocolStructure().getSize() >= 0);
        Assert.assertTrue(process.isComplete());

        process.reset();
        Assert.assertTrue(process.getProtocolStructure().getSize() == 0);
        Assert.assertFalse(process.isComplete());
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);
    }

    @Test
    public void testLearnProtocolStructure() throws URISyntaxException, InterruptedException, TimeoutException {
        Set<ProtocolFile> protocolFiles = new HashSet<>(2);
        protocolFiles.add(new ProtocolFile(Paths.get(getClass().getResource("/capture1.txt").toURI())));
        protocolFiles.add(new ProtocolFile(Paths.get(getClass().getResource("/capture2.txt").toURI())));
        process.learnProtocolStructure(protocolFiles);
        processMonitor.waitForFinish();
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());
    }

    @Test
    public void testGetProtocolStructure() throws URISyntaxException, InterruptedException, TimeoutException {
        Set<ProtocolFile> protocolFiles = new HashSet<>(2);
        protocolFiles.add(new ProtocolFile(Paths.get(getClass().getResource("/capture1.txt").toURI())));
        protocolFiles.add(new ProtocolFile(Paths.get(getClass().getResource("/capture2.txt").toURI())));
        process.learnProtocolStructure(protocolFiles);
        processMonitor.waitForFinishAndReset();
        ProtocolStructure actual = process.getProtocolStructure();
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

    @Test
    public void testUpdate() throws Exception {
        process.update(new Runner(new HashSet<>(0)), ExternalState.IDLE);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);

        process.update(new Runner(new HashSet<>(0)), ExternalState.RUNNING);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.RUNNING);

        process.update(new Runner(new HashSet<>(0)), ExternalState.FINISHED);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);
    }
}
