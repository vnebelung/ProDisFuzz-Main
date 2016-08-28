/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.report;

import model.record.Recordings;
import model.util.Constants;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

import static java.nio.file.FileVisitResult.CONTINUE;

@SuppressWarnings("HardCodedStringLiteral")
public class NameFinderTest {

    public static final byte[] BYTES = new byte[0];
    private Recordings recordings;
    private Path tmpDir;

    @BeforeClass
    public void setUp() throws IOException {
        recordings = new Recordings();
        recordings.addRecording(BYTES, true, Instant.now());
        recordings.addRecording(BYTES, false, Instant.now());
        recordings.addRecording(BYTES, false, Instant.now());

        tmpDir = Files.createTempDirectory(null);
    }

    @Test
    public void testCall() throws IOException {

        NameFinder nameFinder = new NameFinder(tmpDir, recordings);
        String name = nameFinder.call();
        Assert.assertEquals(name, "results");
        //noinspection StringConcatenationMissingWhitespace
        Assert.assertEquals(recordings.getRecord(0).getOutputPath(),
                tmpDir.resolve("results" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-0-0.bytes"));
        //noinspection StringConcatenationMissingWhitespace
        Assert.assertEquals(recordings.getRecord(1).getOutputPath(),
                tmpDir.resolve("results" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-1-0.bytes"));
        //noinspection StringConcatenationMissingWhitespace
        Assert.assertEquals(recordings.getRecord(2).getOutputPath(),
                tmpDir.resolve("results" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-1-1.bytes"));

        Files.createDirectory(tmpDir.resolve(name));
        Files.createFile(tmpDir.resolve(name + ".html"));
        //noinspection StringConcatenationMissingWhitespace
        Files.createDirectory(tmpDir.resolve(name + Constants.RECORDINGS_DIRECTORY_POSTFIX));
        Files.copy(recordings.getRecord(0).getFilePath(), recordings.getRecord(0).getOutputPath());
        Files.copy(recordings.getRecord(1).getFilePath(), recordings.getRecord(1).getOutputPath());
        Files.copy(recordings.getRecord(2).getFilePath(), recordings.getRecord(2).getOutputPath());

        nameFinder = new NameFinder(tmpDir, recordings);
        name = nameFinder.call();
        Assert.assertEquals(name, "results(1)");
        Assert.assertEquals(recordings.getRecord(0).getOutputPath(),
                tmpDir.resolve("results(1)" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-0-0.bytes"));
        Assert.assertEquals(recordings.getRecord(1).getOutputPath(),
                tmpDir.resolve("results(1)" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-1-0.bytes"));
        Assert.assertEquals(recordings.getRecord(2).getOutputPath(),
                tmpDir.resolve("results(1)" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-1-1.bytes"));

        Files.createDirectory(tmpDir.resolve(name));
        Files.createFile(tmpDir.resolve(name + ".html"));
        //noinspection StringConcatenationMissingWhitespace
        Files.createDirectory(tmpDir.resolve(name + Constants.RECORDINGS_DIRECTORY_POSTFIX));
        Files.copy(recordings.getRecord(0).getFilePath(), recordings.getRecord(0).getOutputPath());
        Files.copy(recordings.getRecord(1).getFilePath(), recordings.getRecord(1).getOutputPath());
        Files.copy(recordings.getRecord(2).getFilePath(), recordings.getRecord(2).getOutputPath());

        nameFinder = new NameFinder(tmpDir, recordings);
        name = nameFinder.call();
        Assert.assertEquals(name, "results(2)");
        Assert.assertEquals(recordings.getRecord(0).getOutputPath(),
                tmpDir.resolve("results(2)" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-0-0.bytes"));
        Assert.assertEquals(recordings.getRecord(1).getOutputPath(),
                tmpDir.resolve("results(2)" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-1-0.bytes"));
        Assert.assertEquals(recordings.getRecord(2).getOutputPath(),
                tmpDir.resolve("results(2)" + Constants.RECORDINGS_DIRECTORY_POSTFIX).resolve("record-1-1.bytes"));
    }

    @AfterClass
    public void tearDown() throws IOException {
        Files.walkFileTree(tmpDir, new PathSimpleFileVisitor());
        for (int i = 0; i < recordings.getSize(); i++) {
            Files.delete(recordings.getRecord(i).getFilePath());
        }
    }

    private static class PathSimpleFileVisitor extends SimpleFileVisitor<Path> {

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            super.visitFile(file, attrs);
            Files.delete(file);
            return CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            super.postVisitDirectory(dir, exc);
            if (exc == null) {
                Files.delete(dir);
                return CONTINUE;
            } else {
                throw exc;
            }
        }
    }
}
