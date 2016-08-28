/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.export;

import model.process.AbstractProcess.State;
import model.process.AbstractRunner.ExternalState;
import model.protocol.ProtocolStructure;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import support.ProcessMonitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ProcessTest {

    private ProtocolStructure protocolStructure;
    private model.process.export.Process process = new model.process.export.Process();
    private ProcessMonitor processMonitor = new ProcessMonitor(process);

    @BeforeMethod
    public void initProcess() {
        process.reset();
        process.init(protocolStructure);
        processMonitor.reset();
    }

    @BeforeClass
    public void setUp() {
        protocolStructure = new ProtocolStructure();
        List<Byte> block1 = new ArrayList<>();
        block1.add((byte) 0);
        block1.add((byte) 17);
        protocolStructure.addBlock(block1);
        List<Byte> block2 = new ArrayList<>();
        block2.add(null);
        block2.add(null);
        block2.add(null);
        protocolStructure.addBlock(block2);
        List<Byte> block3 = new ArrayList<>();
        block3.add((byte) 0);
        protocolStructure.addBlock(block3);
        List<Byte> block4 = new ArrayList<>();
        block4.add(null);
        protocolStructure.addBlock(block4);
        List<Byte> block5 = new ArrayList<>();
        block5.add((byte) 0);
        block5.add((byte) 17);
        block5.add((byte) 34);
        protocolStructure.addBlock(block5);
    }

    @Test
    public void testReset() throws IOException, InterruptedException, TimeoutException {
        Path path = Files.createTempFile(null, null);

        process.exportProtocolStructure(path);
        processMonitor.waitForFinishAndReset();
        Assert.assertTrue(process.isComplete());

        process.reset();
        Assert.assertFalse(process.isComplete());
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);

        Files.delete(path);
    }

    @SuppressWarnings("EmptyMethod")
    @Test
    public void testInit() throws Exception {
        // See testExportProtocolStructure
    }

    @Test
    public void testExportProtocolStructure() throws IOException, InterruptedException, TimeoutException {
        Path path = Files.createTempFile(null, null);

        process.exportProtocolStructure(path);
        processMonitor.waitForFinish();
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());

        Files.delete(path);
    }

    @Test
    public void testUpdate() throws Exception {
        process.update(new Runner(Paths.get(""), protocolStructure), ExternalState.IDLE);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);

        process.update(new Runner(Paths.get(""), protocolStructure), ExternalState.RUNNING);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.RUNNING);

        process.update(new Runner(Paths.get(""), protocolStructure), ExternalState.FINISHED);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);
    }

    @Test
    public void testIsComplete() throws IOException, InterruptedException, TimeoutException {
        Path path = Files.createTempFile(null, null);
        Assert.assertFalse(process.isComplete());

        process.exportProtocolStructure(path);
        processMonitor.waitForFinishAndReset();
        Assert.assertTrue(process.isComplete());

        Files.delete(path);
    }
}
