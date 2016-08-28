/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import model.process.fuzzoptions.Process.InjectionMethod;
import model.process.fuzzoptions.Process.RecordingMethod;
import model.protocol.InjectedProtocolStructure;
import model.protocol.ProtocolStructure;
import model.record.Recordings;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import support.RunnerMonitor;
import support.SimulatedServer;
import support.SimulatedServer.Mode;

import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("HardCodedStringLiteral")
public class RunnerTest {

    private InjectedProtocolStructure injectedProtocolStructure;
    private SimulatedServer simulatedCrashingServer = new SimulatedServer(Mode.UNSTABLE);
    private InetSocketAddress inetSocketAddress;

    @BeforeClass
    public void setUp() throws URISyntaxException, InterruptedException {
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
        simulatedCrashingServer.start();
        inetSocketAddress = new InetSocketAddress("localhost", simulatedCrashingServer.getPort());
    }

    @AfterClass
    public void tearDown() {
        simulatedCrashingServer.interrupt();
    }

    @Test
    public void testGetRecordings1() throws Exception {
        Runner runner = new Runner(InjectionMethod.SIMULTANEOUS, injectedProtocolStructure, inetSocketAddress, 50, 50,
                RecordingMethod.CRITICAL);
        Instant start = Instant.now();
        runner.run();
        Instant end = Instant.now();
        Recordings recordings = runner.getRecordings();
        Assert.assertEquals(recordings.getSize(), 1);
        Assert.assertEquals(recordings.getCrashSize(), 1);
        Assert.assertTrue(recordings.getCrashRecord(0).isCrash());
        Assert.assertTrue(recordings.getCrashRecord(0).getSavedTime().isAfter(start) &&
                recordings.getCrashRecord(0).getSavedTime().isBefore(end));

        for (int i = 0; i < recordings.getSize(); i++) {
            Files.delete(recordings.getRecord(i).getFilePath());
        }
    }

    @Test
    public void testGetRecordings2() throws Exception {
        Runner runner = new Runner(InjectionMethod.SIMULTANEOUS, injectedProtocolStructure, inetSocketAddress, 50, 50,
                RecordingMethod.ALL);
        Instant start = Instant.now();
        runner.run();
        Instant end = Instant.now();
        Recordings recordings = runner.getRecordings();
        // #28: 0xee is a crash
        Assert.assertEquals(recordings.getSize(), 31);
        for (int i = 0; i < recordings.getSize(); i++) {
            if (i != 28) {
                Assert.assertFalse(recordings.getRecord(i).isCrash());
            }
            Assert.assertTrue(recordings.getRecord(i).getSavedTime().isAfter(start));
            Assert.assertTrue(recordings.getRecord(i).getSavedTime().isBefore(end));
            if (i > 0) {
                Assert.assertFalse(
                        recordings.getRecord(i - 1).getSavedTime().isAfter(recordings.getRecord(i).getSavedTime()));
            }
        }
        Assert.assertEquals(recordings.getCrashSize(), 1);
        Assert.assertTrue(recordings.getRecord(28).isCrash());
        //noinspection ObjectEquality
        Assert.assertTrue(recordings.getRecord(28) == recordings.getCrashRecord(0));

        for (int i = 0; i < recordings.getSize(); i++) {
            Files.delete(recordings.getRecord(i).getFilePath());
        }
    }

    @Test
    public void testRun() throws Exception {
        RunnerMonitor monitor = new RunnerMonitor();
        Runner runner = new Runner(InjectionMethod.SIMULTANEOUS, injectedProtocolStructure, inetSocketAddress, 50, 50,
                RecordingMethod.ALL);
        runner.addObserver(monitor);
        runner.run();
        Assert.assertTrue(monitor.areAllStatesVisited());

        for (int i = 0; i < runner.getRecordings().getSize(); i++) {
            Files.delete(runner.getRecordings().getRecord(i).getFilePath());
        }
    }
}
