/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:29.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.import_;

import model.process.AbstractProcess.State;
import model.process.AbstractRunner.ExternalState;
import model.protocol.ProtocolBlock.Type;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import support.ProcessMonitor;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("HardCodedStringLiteral")
public class ProcessTest {

    private Process process = new Process();
    private ProcessMonitor processMonitor = new ProcessMonitor(process);

    @BeforeMethod
    public void initProcess() throws URISyntaxException {
        process.reset();
        processMonitor.reset();
    }

    @Test
    public void testReset() throws URISyntaxException, InterruptedException, TimeoutException {
        process.importProtocolStructure(Paths.get(getClass().getResource("/protocol.xml").toURI()));
        processMonitor.waitForFinishAndReset();
        Assert.assertTrue(process.isComplete());
        Assert.assertTrue(process.getProtocolStructure().getSize() > 0);

        process.reset();
        Assert.assertFalse(process.isComplete());
        Assert.assertEquals(process.getProtocolStructure().getSize(), 0);
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);
    }

    @Test
    public void testImportProtocolStructure() throws URISyntaxException, InterruptedException, TimeoutException {
        process.importProtocolStructure(Paths.get(getClass().getResource("/protocol.xml").toURI()));
        processMonitor.waitForFinish();
        // See testGetProtocolStructure
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());
    }

    @Test
    public void testGetProtocolStructure() throws URISyntaxException, InterruptedException, TimeoutException {
        Assert.assertEquals(process.getProtocolStructure().getSize(), 0);
        //noinspection HardCodedStringLiteral
        process.importProtocolStructure(Paths.get(getClass().getResource("/protocol.xml").toURI()));
        processMonitor.waitForFinishAndReset();
        Assert.assertEquals(process.getProtocolStructure().getSize(), 5);

        Byte[] b0 = {0, 17};
        Assert.assertEquals(process.getProtocolStructure().getBlock(0).getType(), Type.FIX);
        Assert.assertEquals(process.getProtocolStructure().getBlock(0).getMinLength(), b0.length);
        Assert.assertEquals(process.getProtocolStructure().getBlock(0).getMaxLength(), b0.length);
        Assert.assertEquals(process.getProtocolStructure().getBlock(0).getBytes(), b0);

        Byte[] b1 = {null, null, null};
        Assert.assertEquals(process.getProtocolStructure().getBlock(1).getType(), Type.VAR);
        Assert.assertEquals(process.getProtocolStructure().getBlock(1).getMinLength(), b1.length);
        Assert.assertEquals(process.getProtocolStructure().getBlock(1).getMaxLength(), b1.length);
        Assert.assertEquals(process.getProtocolStructure().getBlock(1).getBytes(), b1);

        Byte[] b2 = {0};
        Assert.assertEquals(process.getProtocolStructure().getBlock(2).getType(), Type.FIX);
        Assert.assertEquals(process.getProtocolStructure().getBlock(2).getMinLength(), b2.length);
        Assert.assertEquals(process.getProtocolStructure().getBlock(2).getMaxLength(), b2.length);
        Assert.assertEquals(process.getProtocolStructure().getBlock(2).getBytes(), b2);

        Byte[] b3 = {null};
        Assert.assertEquals(process.getProtocolStructure().getBlock(3).getType(), Type.VAR);
        Assert.assertEquals(process.getProtocolStructure().getBlock(3).getMinLength(), b3.length);
        Assert.assertEquals(process.getProtocolStructure().getBlock(3).getMaxLength(), b3.length);
        Assert.assertEquals(process.getProtocolStructure().getBlock(3).getBytes(), b3);

        Byte[] b4 = {0, 17, 34};
        Assert.assertEquals(process.getProtocolStructure().getBlock(4).getType(), Type.FIX);
        Assert.assertEquals(process.getProtocolStructure().getBlock(4).getMinLength(), b4.length);
        Assert.assertEquals(process.getProtocolStructure().getBlock(4).getMaxLength(), b4.length);
        Assert.assertEquals(process.getProtocolStructure().getBlock(4).getBytes(), b4);
    }

    @Test
    public void testUpdate() throws URISyntaxException {
        process.update(new Runner(Paths.get(getClass().getResource("/protocol.xml").toURI())), ExternalState.IDLE);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);

        process.update(new Runner(Paths.get(getClass().getResource("/protocol.xml").toURI())), ExternalState.RUNNING);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.RUNNING);

        process.update(new Runner(Paths.get(getClass().getResource("/protocol.xml").toURI())), ExternalState.FINISHED);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);
    }
}
