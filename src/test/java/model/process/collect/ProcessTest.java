/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.collect;

import model.process.AbstractProcess.State;
import model.process.AbstractRunner.ExternalState;
import model.protocol.ProtocolFile;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import support.ProcessMonitor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.TimeoutException;

@SuppressWarnings("HardCodedStringLiteral")
public class ProcessTest {

    private Process process = new Process();
    private ProcessMonitor processMonitor = new ProcessMonitor(process);

    @BeforeMethod
    public void initProcess() {
        process.reset();
        processMonitor.reset();
    }

    @Test
    public void testReset() throws URISyntaxException, InterruptedException, TimeoutException {
        process.readDirectory(getClass().getResource("/").toURI().getPath());
        processMonitor.waitForFinishAndReset();
        process.toggleSelection("protocol.xml", true);
        processMonitor.waitForFinishAndReset();
        process.toggleSelection("capture1.txt", true);
        processMonitor.waitForFinishAndReset();
        Assert.assertFalse(process.getFiles().isEmpty());
        Assert.assertTrue(process.isDirectoryValid());
        Assert.assertFalse(process.getSelectedFiles().isEmpty());
        Assert.assertTrue(process.isComplete());

        process.reset();
        Assert.assertTrue(process.getFiles().isEmpty());
        Assert.assertFalse(process.isDirectoryValid());
        Assert.assertTrue(process.getSelectedFiles().isEmpty());
        Assert.assertFalse(process.isComplete());
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);
    }

    @Test
    public void testGetFiles() throws URISyntaxException, InterruptedException, IOException, TimeoutException {
        Path tmp = Files.createTempDirectory(null);
        Path a = Files.copy(Paths.get(getClass().getResource("/capture1.txt").toURI()),
                tmp.resolve(Paths.get(getClass().getResource("/capture1.txt").toURI()).getFileName()));
        //noinspection StandardVariableNames
        Path b = Files.copy(Paths.get(getClass().getResource("/capture2.txt").toURI()),
                tmp.resolve(Paths.get(getClass().getResource("/capture2.txt").toURI()).getFileName()));
        //noinspection StandardVariableNames
        Path c = Files.copy(Paths.get(getClass().getResource("/library1.txt").toURI()),
                tmp.resolve(Paths.get(getClass().getResource("/library1.txt").toURI()).getFileName()));

        process.readDirectory(tmp.toString());
        processMonitor.waitForFinish();
        ProtocolFile[] protocolFiles = new ProtocolFile[3];
        protocolFiles[0] = new ProtocolFile(Paths.get(getClass().getResource("/capture1.txt").toURI()));
        protocolFiles[1] = new ProtocolFile(Paths.get(getClass().getResource("/capture2.txt").toURI()));
        protocolFiles[2] = new ProtocolFile(Paths.get(getClass().getResource("/library1.txt").toURI()));
        Assert.assertEquals(process.getFiles().size(), protocolFiles.length);
        for (ProtocolFile eachReference : protocolFiles) {
            boolean found = false;
            for (ProtocolFile eachActual : process.getFiles()) {
                if (eachActual.getName().equals(eachReference.getName())) {
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
    }

    @Test
    public void testGetSelectedFiles() throws URISyntaxException, InterruptedException, TimeoutException {
        process.readDirectory(getClass().getResource("/").toURI().getPath());
        processMonitor.waitForFinishAndReset();
        process.toggleSelection("protocol.xml", true);
        processMonitor.waitForFinishAndReset();
        Set<ProtocolFile> protocolFiles = process.getSelectedFiles();
        Assert.assertEquals(protocolFiles.size(), 1);
        Assert.assertEquals(protocolFiles.iterator().next().getName(), "protocol.xml");
    }


    @Test
    public void testIsComplete() throws URISyntaxException, InterruptedException, TimeoutException {
        Assert.assertFalse(process.isComplete());

        process.readDirectory(getClass().getResource("/").toURI().getPath());
        processMonitor.waitForFinishAndReset();
        Assert.assertFalse(process.isComplete());

        process.toggleSelection("protocol.xml", true);
        processMonitor.waitForFinishAndReset();
        Assert.assertFalse(process.isComplete());

        process.toggleSelection("releases.xml", true);
        processMonitor.waitForFinishAndReset();
        Assert.assertTrue(process.isComplete());
    }

    @Test
    public void testReadDirectory() throws URISyntaxException, InterruptedException, TimeoutException {
        process.readDirectory(getClass().getResource("/").toURI().getPath());
        processMonitor.waitForFinish();
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());
    }

    @Test
    public void testToggleSelection() throws URISyntaxException, InterruptedException, TimeoutException {
        process.readDirectory(getClass().getResource("/").toURI().getPath());
        processMonitor.waitForFinishAndReset();
        process.toggleSelection("protocol.xml", true);
        processMonitor.waitForFinish();
        Assert.assertTrue(processMonitor.areStatesCompleteAndCorrect());
    }

    @Test
    public void testIsSelected() throws URISyntaxException, InterruptedException, TimeoutException {
        process.readDirectory(getClass().getResource("/").toURI().getPath());
        processMonitor.waitForFinishAndReset();
        Assert.assertFalse(process.isSelected("protocol.xml"));

        process.toggleSelection("protocol.xml", true);
        processMonitor.waitForFinishAndReset();
        Assert.assertTrue(process.isSelected("protocol.xml"));
    }

    @Test
    public void testIsDirectoryValid() throws URISyntaxException, InterruptedException, TimeoutException {
        Assert.assertFalse(process.isDirectoryValid());

        //noinspection StringConcatenationMissingWhitespace
        process.readDirectory(getClass().getResource("/").toURI().getPath() + "dummy");
        processMonitor.waitForFinishAndReset();
        Assert.assertFalse(process.isDirectoryValid());

        process.readDirectory(getClass().getResource("/").toURI().getPath());
        processMonitor.waitForFinishAndReset();
        Assert.assertTrue(process.isDirectoryValid());
    }

    @Test
    public void testUpdate() throws Exception {
        process.update(new DirectoryRunner(""), ExternalState.IDLE);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);

        process.update(new DirectoryRunner(""), ExternalState.RUNNING);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.RUNNING);

        process.update(new DirectoryRunner(""), ExternalState.FINISHED);
        Assert.assertEquals(processMonitor.getObservable(), process);
        Assert.assertEquals(processMonitor.getLastState(), State.IDLE);


    }

}
