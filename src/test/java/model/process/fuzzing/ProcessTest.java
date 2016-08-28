/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import model.process.AbstractProcess.State;
import model.process.AbstractRunner.ExternalState;
import model.process.fuzzoptions.Process.InjectionMethod;
import model.process.fuzzoptions.Process.RecordingMethod;
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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("HardCodedStringLiteral")
public class ProcessTest {

    private SimulatedServer simulatedServer = new SimulatedServer(Mode.STABLE);
    private Process process = new Process();
    private ProcessMonitor processMonitor = new ProcessMonitor(process);
    private InjectedProtocolStructure injectedProtocolStructure;
    private InetSocketAddress inetSocketAddress;

    @BeforeClass
    public void setUp() throws InterruptedException, URISyntaxException {
        simulatedServer.start();

        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = new ArrayList<>(1);
        bytes1.add(null);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = new ArrayList<>(1);
        bytes2.add((byte) 1);
        protocolStructure.addBlock(bytes2);
        injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);
        injectedProtocolStructure.getVarBlock(0).setLibraryInjection();
        injectedProtocolStructure.getVarBlock(0).setLibrary(Paths.get(getClass().getResource("/library1.txt").toURI()));
        inetSocketAddress = new InetSocketAddress("localhost", simulatedServer.getPort());
    }

    @AfterClass
    public void tearDown() {
        simulatedServer.interrupt();
    }

    @BeforeMethod
    public void initProcess() throws URISyntaxException, InterruptedException {
        process.reset();
        processMonitor.reset();
    }

    @Test
    public void testStartFuzzing() throws URISyntaxException, InterruptedException, TimeoutException, IOException {
        process.startFuzzing(injectedProtocolStructure, inetSocketAddress, 100, 100, RecordingMethod.ALL,
                InjectionMethod.SIMULTANEOUS);
        processMonitor.waitForFinish();
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());

        for (int i = 0; i < process.getRecordings().getSize(); i++) {
            Files.delete(process.getRecordings().getRecord(i).getFilePath());
        }
    }

    @Test
    public void testReset() throws URISyntaxException, InterruptedException, TimeoutException, IOException {
        process.startFuzzing(injectedProtocolStructure, inetSocketAddress, 100, 100, RecordingMethod.ALL,
                InjectionMethod.SIMULTANEOUS);
        processMonitor.waitForFinishAndReset();
        Assert.assertTrue(process.getRecordings().getSize() > 0);
        Assert.assertTrue(process.isComplete());

        process.reset();
        Assert.assertTrue(process.getRecordings().getSize() == 0);
        Assert.assertFalse(process.isComplete());
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);

        for (int i = 0; i < process.getRecordings().getSize(); i++) {
            Files.delete(process.getRecordings().getRecord(i).getFilePath());
        }
    }

    @Test
    public void testUpdate() throws URISyntaxException {
        process.update(new Runner(InjectionMethod.SIMULTANEOUS, injectedProtocolStructure, inetSocketAddress, 100, 100,
                RecordingMethod.CRITICAL), ExternalState.IDLE);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);

        process.update(new Runner(InjectionMethod.SIMULTANEOUS, injectedProtocolStructure, inetSocketAddress, 100, 100,
                RecordingMethod.CRITICAL), ExternalState.RUNNING);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.RUNNING);

        process.update(new Runner(InjectionMethod.SIMULTANEOUS, injectedProtocolStructure, inetSocketAddress, 100, 100,
                RecordingMethod.CRITICAL), ExternalState.FINISHED);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);
    }

    @Test
    public void testGetRecordings() throws URISyntaxException, InterruptedException, TimeoutException, IOException {
        Assert.assertEquals(process.getRecordings().getSize(), 0);

        process.startFuzzing(injectedProtocolStructure, inetSocketAddress, 100, 100, RecordingMethod.ALL,
                InjectionMethod.SIMULTANEOUS);
        processMonitor.waitForFinishAndReset();
        Assert.assertEquals(process.getRecordings().getSize(), 32);

        for (int i = 0; i < process.getRecordings().getSize(); i++) {
            Files.delete(process.getRecordings().getRecord(i).getFilePath());
        }
    }
}
