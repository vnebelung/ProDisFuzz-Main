/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.report;

import model.process.fuzzoptions.Process.RecordingMethod;
import model.protocol.InjectedProtocolStructure;
import model.protocol.ProtocolStructure;
import model.record.Recordings;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import support.RunnerMonitor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("HardCodedStringLiteral")
public class RunnerTest {

    private byte[] bytes = {0, 17, 34};
    private Duration duration = Duration.ofSeconds(3723);
    private Recordings recordings;
    private InjectedProtocolStructure injectedProtocolStructure;
    private InetSocketAddress target = new InetSocketAddress("example.net", 999);
    private Path tmpDir;

    @BeforeClass
    public void setUp() throws URISyntaxException, IOException {
        recordings = new Recordings();
        recordings.addRecording(bytes, false, Instant.parse("2000-01-01T00:11:22Z"));
        recordings.addRecording(bytes, false, Instant.parse("2000-01-01T00:11:22Z"));
        recordings.addRecording(bytes, true, Instant.parse("2000-01-01T00:11:22Z"));
        recordings.addRecording(bytes, true, Instant.parse("2000-01-01T00:11:22Z"));

        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = new ArrayList<>(2);
        bytes1.add((byte) 0);
        bytes1.add((byte) 17);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = new ArrayList<>(3);
        bytes2.add(null);
        bytes2.add(null);
        bytes2.add(null);
        protocolStructure.addBlock(bytes2);
        List<Byte> bytes3 = new ArrayList<>(1);
        bytes3.add((byte) 0);
        protocolStructure.addBlock(bytes3);
        List<Byte> bytes4 = new ArrayList<>(1);
        bytes4.add(null);
        protocolStructure.addBlock(bytes4);
        List<Byte> bytes5 = new ArrayList<>(3);
        bytes5.add((byte) 0);
        bytes5.add((byte) 17);
        bytes5.add((byte) 34);
        protocolStructure.addBlock(bytes5);
        injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);
        injectedProtocolStructure.getVarBlock(0).setLibraryInjection();
        injectedProtocolStructure.getVarBlock(0).setLibrary(Paths.get(getClass().getResource("/library1.txt").toURI()));

        tmpDir = Files.createTempDirectory(null);
    }

    @AfterClass
    public void tearDown() throws IOException {
        Files.walkFileTree(tmpDir, new PathSimpleFileVisitor());
    }

    @Test
    public void testRun() throws Exception {
        Runner runner =
                new Runner(recordings, duration, target, 10, injectedProtocolStructure, 3, 3, RecordingMethod.ALL, 20,
                        tmpDir);
        RunnerMonitor monitor = new RunnerMonitor();
        runner.addObserver(monitor);
        runner.run();
        Assert.assertTrue(monitor.areAllStatesVisited());
    }

    private static class PathSimpleFileVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            super.visitFile(file, attrs);
            Files.delete(file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            super.postVisitDirectory(dir, exc);
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
        }
    }
}
