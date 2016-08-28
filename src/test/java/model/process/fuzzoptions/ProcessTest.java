/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import model.process.AbstractProcess.State;
import model.process.AbstractRunner.ExternalState;
import model.process.fuzzoptions.Process.InjectionMethod;
import model.process.fuzzoptions.Process.RecordingMethod;
import model.protocol.InjectedProtocolBlock.DataInjection;
import model.protocol.InjectedProtocolStructure;
import model.protocol.ProtocolStructure;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import support.ProcessMonitor;
import support.SimulatedServer;
import support.SimulatedServer.Mode;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("HardCodedStringLiteral")
public class ProcessTest {

    private ProtocolStructure protocolStructure;
    private Process process = new Process();
    private ProcessMonitor processMonitor = new ProcessMonitor(process);
    private SimulatedServer simulatedServer = new SimulatedServer(Mode.STABLE);

    @BeforeClass
    public void setUp() {
        protocolStructure = new ProtocolStructure();
        List<Byte> block1 = new ArrayList<>();
        block1.add((byte) 0);
        protocolStructure.addBlock(block1);
        List<Byte> block2 = new ArrayList<>();
        block2.add(null);
        protocolStructure.addBlock(block2);
        List<Byte> block3 = new ArrayList<>();
        block3.add((byte) 1);
        block3.add((byte) 2);
        protocolStructure.addBlock(block3);
        List<Byte> block4 = new ArrayList<>();
        block4.add(null);
        block4.add(null);
        protocolStructure.addBlock(block4);

        simulatedServer.start();
    }

    @AfterClass
    public void tearDown() {
        simulatedServer.interrupt();
    }

    @BeforeMethod
    public void initProcess() throws URISyntaxException {
        process.reset();
        process.init(protocolStructure);
        processMonitor.reset();
    }

    @Test
    public void testInit() throws Exception {
        Assert.assertEquals(process.getTimeout(), 5 * TimeoutRunner.TIMEOUT_MIN);
        Assert.assertEquals(process.getInterval(), 5 * IntervalRunner.INTERVAL_MIN);
        Assert.assertEquals(process.getInjectionMethod(), InjectionMethod.SIMULTANEOUS);
        Assert.assertNull(process.getTarget());
        Assert.assertEquals(process.getRecordingMethod(), RecordingMethod.CRITICAL);
        Assert.assertEquals(process.getInjectedProtocolStructure().getSize(), protocolStructure.getSize());
    }

    @Test
    public void testReset() throws Exception {
        process.setTimeout(1000);
        processMonitor.waitForFinishAndReset();
        process.setInterval(1000);
        processMonitor.waitForFinishAndReset();
        process.setInjectionMethod(InjectionMethod.SEPARATE);
        processMonitor.waitForFinishAndReset();
        process.setTarget("localhost", simulatedServer.getPort());
        processMonitor.waitForFinishAndReset();
        process.setRecordingMethod(RecordingMethod.ALL);
        processMonitor.waitForFinishAndReset();

        Assert.assertEquals(process.getTimeout(), 1000);
        Assert.assertEquals(process.getInterval(), 1000);
        Assert.assertEquals(process.getInjectionMethod(), InjectionMethod.SEPARATE);
        Assert.assertNotNull(process.getTarget());
        Assert.assertEquals(process.getRecordingMethod(), RecordingMethod.ALL);
        Assert.assertTrue(process.getInjectedProtocolStructure().getSize() > 0);
        Assert.assertTrue(process.isComplete());

        process.reset();
        Assert.assertEquals(process.getTimeout(), 5 * TimeoutRunner.TIMEOUT_MIN);
        Assert.assertEquals(process.getInterval(), 5 * IntervalRunner.INTERVAL_MIN);
        Assert.assertEquals(process.getInjectionMethod(), InjectionMethod.SIMULTANEOUS);
        Assert.assertNull(process.getTarget());
        Assert.assertEquals(process.getRecordingMethod(), RecordingMethod.CRITICAL);
        Assert.assertTrue(process.getInjectedProtocolStructure().getSize() == 0);
        Assert.assertFalse(process.isComplete());
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);
    }

    @SuppressWarnings("EmptyMethod")
    @Test
    public void testGetInjectionMethod() throws Exception {
        // See testSetInjectionMethod
    }

    @SuppressWarnings("EmptyMethod")
    @Test
    public void testGetRecordingMethod() throws Exception {
        // See testSetRecordingMethod
    }

    @Test
    public void testSetRecordingMethod() throws InterruptedException, TimeoutException {
        Assert.assertEquals(process.getRecordingMethod(), RecordingMethod.CRITICAL);

        process.setRecordingMethod(RecordingMethod.ALL);
        processMonitor.waitForFinish();
        Assert.assertEquals(process.getRecordingMethod(), RecordingMethod.ALL);
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());

        processMonitor.reset();
        process.setRecordingMethod(RecordingMethod.CRITICAL);
        processMonitor.waitForFinish();
        Assert.assertEquals(process.getRecordingMethod(), RecordingMethod.CRITICAL);
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());
    }

    @Test
    public void testSetLibraryForVarProtocolBlock() throws URISyntaxException, InterruptedException, TimeoutException {
        process.setInjectionDataForVarProtocolBlock(0, DataInjection.LIBRARY);
        processMonitor.waitForFinishAndReset();

        Path library = Paths.get(getClass().getResource("/library1.txt").toURI());
        process.setLibraryForVarProtocolBlock(0, library);
        processMonitor.waitForFinish();
        Assert.assertEquals(process.getInjectedProtocolStructure().getVarBlock(0).getLibrary(), library);
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());

        processMonitor.reset();
        library = Paths.get(getClass().getResource("/library2.txt").toURI());
        process.setLibraryForVarProtocolBlock(0, library);
        processMonitor.waitForFinish();
        Assert.assertNull(process.getInjectedProtocolStructure().getVarBlock(0).getLibrary());
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());

        processMonitor.reset();
        library = Paths.get(getClass().getResource("/library3.txt").toURI());
        process.setLibraryForVarProtocolBlock(0, library);
        processMonitor.waitForFinish();
        Assert.assertNull(process.getInjectedProtocolStructure().getVarBlock(0).getLibrary());
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());

        processMonitor.reset();
        library = Paths.get(getClass().getResource("/library4.txt").toURI());
        process.setLibraryForVarProtocolBlock(0, library);
        processMonitor.waitForFinish();
        Assert.assertNull(process.getInjectedProtocolStructure().getVarBlock(0).getLibrary());
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());
    }

    @Test
    public void testSetInjectionDataForVarProtocolBlock() throws InterruptedException, TimeoutException {
        Assert.assertEquals(process.getInjectedProtocolStructure().getVarBlock(1).getDataInjection(),
                DataInjection.RANDOM);

        process.setInjectionDataForVarProtocolBlock(1, DataInjection.LIBRARY);
        processMonitor.waitForFinish();
        Assert.assertEquals(process.getInjectedProtocolStructure().getVarBlock(1).getDataInjection(),
                DataInjection.LIBRARY);
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());

        processMonitor.reset();
        process.setInjectionDataForVarProtocolBlock(1, DataInjection.RANDOM);
        processMonitor.waitForFinish();
        Assert.assertEquals(process.getInjectedProtocolStructure().getVarBlock(1).getDataInjection(),
                DataInjection.RANDOM);
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());
    }

    @Test
    public void testSetInjectionMethod() throws InterruptedException, TimeoutException {
        Assert.assertEquals(process.getInjectionMethod(), InjectionMethod.SIMULTANEOUS);

        process.setInjectionMethod(InjectionMethod.SEPARATE);
        processMonitor.waitForFinish();
        Assert.assertEquals(process.getInjectionMethod(), InjectionMethod.SEPARATE);
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());

        processMonitor.reset();
        process.setInjectionMethod(InjectionMethod.SIMULTANEOUS);
        processMonitor.waitForFinish();
        Assert.assertEquals(process.getInjectionMethod(), InjectionMethod.SIMULTANEOUS);
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());
    }

    @Test
    public void testSetTarget() throws InterruptedException, TimeoutException {
        Assert.assertNull(process.getTarget());

        process.setTarget("localhost", simulatedServer.getPort() + 1);
        processMonitor.waitForFinish();
        Assert.assertNull(process.getTarget());
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());

        processMonitor.reset();
        process.setTarget("", simulatedServer.getPort());
        processMonitor.waitForFinish();
        Assert.assertNull(process.getTarget());
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());

        processMonitor.reset();
        process.setTarget("localhost", simulatedServer.getPort());
        processMonitor.waitForFinish();
        Assert.assertNotNull(process.getTarget());
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());
    }

    @Test
    public void testIsTargetReachable() throws InterruptedException, TimeoutException {
        Assert.assertFalse(process.isTargetReachable());

        process.setTarget("localhost", simulatedServer.getPort());
        processMonitor.waitForFinish();
        Assert.assertTrue(process.isTargetReachable());
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());
    }

    @SuppressWarnings("EmptyMethod")
    @Test
    public void testGetTarget() throws Exception {
        // See testSetTarget
    }

    @SuppressWarnings("EmptyMethod")
    @Test
    public void testGetInterval() throws Exception {
        // See testSetInterval
    }

    @Test
    public void testSetInterval() throws InterruptedException, TimeoutException {
        Assert.assertEquals(process.getInterval(), 500);

        process.setInterval(250);
        processMonitor.waitForFinish();
        Assert.assertEquals(process.getInterval(), 250);
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());
    }

    @SuppressWarnings("EmptyMethod")
    @Test
    public void testGetTimeout() throws Exception {
        // See testSetTimeout
    }

    @Test
    public void testSetTimeout() throws InterruptedException, TimeoutException {
        Assert.assertEquals(process.getTimeout(), 250);

        process.setTimeout(350);
        processMonitor.waitForFinish();
        Assert.assertEquals(process.getTimeout(), 350);
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());
    }

    @Test
    public void testGetInjectedProtocolStructure() throws Exception {
        Assert.assertEquals(process.getInjectedProtocolStructure().getSize(), protocolStructure.getSize());
        for (int i = 0; i < protocolStructure.getSize(); i++) {
            Assert.assertEquals(process.getInjectedProtocolStructure().getBlock(i).getType(),
                    protocolStructure.getBlock(i).getType());
            Assert.assertEquals(process.getInjectedProtocolStructure().getBlock(i).getBytes(),
                    protocolStructure.getBlock(i).getBytes());
        }
    }

    @Test
    public void testUpdate() throws Exception {
        process.update(new InjectionDataRunner(new InjectedProtocolStructure(protocolStructure), DataInjection.RANDOM,
                InjectionMethod.SIMULTANEOUS, 0), ExternalState.IDLE);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);

        process.update(new InjectionDataRunner(new InjectedProtocolStructure(protocolStructure), DataInjection.RANDOM,
                InjectionMethod.SIMULTANEOUS, 0), ExternalState.RUNNING);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.RUNNING);

        process.update(new InjectionDataRunner(new InjectedProtocolStructure(protocolStructure), DataInjection.RANDOM,
                InjectionMethod.SIMULTANEOUS, 0), ExternalState.FINISHED);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);
    }
}
