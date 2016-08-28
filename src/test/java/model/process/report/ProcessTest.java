/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.report;

import model.process.AbstractProcess.State;
import model.process.AbstractRunner.ExternalState;
import model.process.fuzzoptions.Process.RecordingMethod;
import model.protocol.InjectedProtocolStructure;
import model.protocol.ProtocolStructure;
import model.record.Recordings;
import model.util.Constants;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import support.ProcessMonitor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("HardCodedStringLiteral")
public class ProcessTest {

    private Process process = new Process();
    private ProcessMonitor processMonitor = new ProcessMonitor(process);
    private byte[] bytes = {0, 17, 34};
    private Duration duration = Duration.ofSeconds(3723);
    private Recordings recordings;
    private InjectedProtocolStructure injectedProtocolStructure;
    private Path tmpDir;
    private InetSocketAddress target = new InetSocketAddress("example.net", 999);

    @BeforeMethod
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

        process.reset();
        processMonitor.reset();
        tmpDir = Files.createTempDirectory(null);
    }

    @AfterMethod
    public void tearDown() throws IOException {
        Files.walkFileTree(tmpDir, new PathSimpleFileVisitor());
        for (int i = 0; i < recordings.getSize(); i++) {
            Files.deleteIfExists(recordings.getRecord(i).getFilePath());
        }
    }

    @SuppressWarnings("StringConcatenationMissingWhitespace")
    @Test
    public void testSave() throws InterruptedException, IOException, TimeoutException {
        process.save(recordings, duration, target, 10, injectedProtocolStructure, 3, 3, RecordingMethod.ALL, 20,
                tmpDir);
        processMonitor.waitForFinish();
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());

        Assert.assertEquals(Files.list(tmpDir).count(), 2);
        Assert.assertTrue(Files.exists(tmpDir.resolve("results.html")));
        Assert.assertEquals(Files.list(tmpDir.resolve("results" + Constants.RECORDINGS_DIRECTORY_POSTFIX)).count(), 4);
        Assert.assertTrue(Files.exists(
                tmpDir.resolve("results" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-0-0.bytes")));
        Assert.assertTrue(Files.exists(
                tmpDir.resolve("results" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-0-1.bytes")));
        Assert.assertTrue(Files.exists(
                tmpDir.resolve("results" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-1-0.bytes")));
        Assert.assertTrue(Files.exists(
                tmpDir.resolve("results" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-2-0.bytes")));

        Assert.assertEquals(Files.readAllBytes(
                tmpDir.resolve("results" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-0-0.bytes")), bytes);
        Assert.assertEquals(Files.readAllBytes(
                tmpDir.resolve("results" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-0-1.bytes")), bytes);
        Assert.assertEquals(Files.readAllBytes(
                tmpDir.resolve("results" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-1-0.bytes")), bytes);
        Assert.assertEquals(Files.readAllBytes(
                tmpDir.resolve("results" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-2-0.bytes")), bytes);

        process.reset();
        processMonitor.reset();
        for (int i = 0; i < recordings.getSize(); i++) {
            Files.copy(recordings.getRecord(i).getOutputPath(), recordings.getRecord(i).getFilePath());
        }
        process.save(recordings, duration, target, 10, injectedProtocolStructure, 3, 3, RecordingMethod.ALL, 20,
                tmpDir);
        processMonitor.waitForFinish();
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());

        Assert.assertEquals(Files.list(tmpDir).count(), 4);
        Assert.assertEquals(Files.list(tmpDir.resolve("results(1)" + Constants.RECORDINGS_DIRECTORY_POSTFIX)).count(),
                4);
        Assert.assertTrue(Files.exists(tmpDir.resolve("results(1).html")));
        Assert.assertEquals(Files.list(tmpDir.resolve("results(1)" + Constants.RECORDINGS_DIRECTORY_POSTFIX)).count(),
                4);
        Assert.assertTrue(Files.exists(
                tmpDir.resolve("results(1)" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-0-0.bytes")));
        Assert.assertTrue(Files.exists(
                tmpDir.resolve("results(1)" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-0-1.bytes")));
        Assert.assertTrue(Files.exists(
                tmpDir.resolve("results(1)" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-1-0.bytes")));
        Assert.assertTrue(Files.exists(
                tmpDir.resolve("results(1)" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-2-0.bytes")));

        Assert.assertEquals(Files.readAllBytes(
                tmpDir.resolve("results(1)" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-0-0.bytes")),
                bytes);
        Assert.assertEquals(Files.readAllBytes(
                tmpDir.resolve("results(1)" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-0-1.bytes")),
                bytes);
        Assert.assertEquals(Files.readAllBytes(
                tmpDir.resolve("results(1)" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-1-0.bytes")),
                bytes);
        Assert.assertEquals(Files.readAllBytes(
                tmpDir.resolve("results(1)" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-2-0.bytes")),
                bytes);
    }

    @Test
    public void testReset() throws InterruptedException, TimeoutException {
        process.save(recordings, duration, target, 10, injectedProtocolStructure, 3, 3, RecordingMethod.ALL, 20,
                tmpDir);
        processMonitor.waitForFinishAndReset();

        Assert.assertTrue(process.isComplete());
        process.reset();

        Assert.assertFalse(process.isComplete());
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);
    }

    @Test
    public void testUpdate() throws Exception {
        process.update(
                new Runner(recordings, duration, target, 10, injectedProtocolStructure, 3, 3, RecordingMethod.ALL, 20,
                        tmpDir), ExternalState.IDLE);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);

        process.update(
                new Runner(recordings, duration, target, 10, injectedProtocolStructure, 3, 3, RecordingMethod.ALL, 20,
                        tmpDir), ExternalState.RUNNING);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.RUNNING);

        process.update(
                new Runner(recordings, duration, target, 10, injectedProtocolStructure, 3, 3, RecordingMethod.ALL, 20,
                        tmpDir), ExternalState.FINISHED);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);
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
