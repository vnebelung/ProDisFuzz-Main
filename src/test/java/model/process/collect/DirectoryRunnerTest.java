/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.collect;

import model.protocol.ProtocolFile;
import org.testng.Assert;
import org.testng.annotations.Test;
import support.RunnerMonitor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("HardCodedStringLiteral")
public class DirectoryRunnerTest {

    @Test
    public void testIsDirectoryValid() throws Exception {
        DirectoryRunner runner = new DirectoryRunner(getClass().getResource("/").toURI().getPath());
        Assert.assertFalse(runner.isDirectoryValid());

        runner.run();
        Assert.assertTrue(runner.isDirectoryValid());

        //noinspection StringConcatenationMissingWhitespace
        runner = new DirectoryRunner(getClass().getResource("/").toURI().getPath() + "dummy");
        runner.run();
        Assert.assertFalse(runner.isDirectoryValid());
    }

    @Test
    public void testGetProtocolFiles() throws URISyntaxException, IOException {
        Path tmp = Files.createTempDirectory(null);
        Path a = Files.createFile(tmp.resolve("a"));
        //noinspection StandardVariableNames
        Path b = Files.createFile(tmp.resolve("b"));
        //noinspection StandardVariableNames
        Path c = Files.createFile(tmp.resolve("c"));

        //noinspection TypeMayBeWeakened
        List<ProtocolFile> protocolFiles1 = new ArrayList<>(6);
        protocolFiles1.add(new ProtocolFile(a));
        protocolFiles1.add(new ProtocolFile(b));
        protocolFiles1.add(new ProtocolFile(c));
        DirectoryRunner runner = new DirectoryRunner(tmp.toString());
        List<ProtocolFile> protocolFiles2 = runner.getProtocolFiles();
        Assert.assertEquals(protocolFiles2.size(), 0);
        runner.run();
        protocolFiles2 = runner.getProtocolFiles();
        Assert.assertEquals(protocolFiles2.size(), protocolFiles1.size());
        for (ProtocolFile eachOriginFile : protocolFiles1) {
            boolean found = false;
            for (ProtocolFile eachActualFile : protocolFiles2) {
                if (eachOriginFile.getName().equals(eachActualFile.getName())) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found);
        }
        Files.delete(a);
        Files.delete(b);
        Files.delete(c);
        Files.delete(tmp);

        runner = new DirectoryRunner(tmp.toString());
        List<ProtocolFile> protocolFiles = runner.getProtocolFiles();
        Assert.assertEquals(protocolFiles.size(), 0);
    }

    @Test
    public void testRun() throws URISyntaxException {
        DirectoryRunner runner = new DirectoryRunner(getClass().getResource("/").toURI().getPath());
        RunnerMonitor monitor = new RunnerMonitor();
        runner.addObserver(monitor);
        runner.run();
        Assert.assertTrue(monitor.areAllStatesVisited());
    }
}
