/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import model.process.fuzzoptions.Process.InjectionMethod;
import model.protocol.InjectedProtocolStructure;
import model.protocol.ProtocolStructure;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import support.RunnerMonitor;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("HardCodedStringLiteral")
public class LibraryRunnerTest {

    private InjectedProtocolStructure injectedProtocolStructure;

    @BeforeClass
    public void setUp() {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = new ArrayList<>(1);
        bytes1.add(null);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = new ArrayList<>(1);
        bytes2.add((byte) 1);
        protocolStructure.addBlock(bytes2);
        List<Byte> bytes3 = new ArrayList<>(1);
        bytes3.add(null);
        protocolStructure.addBlock(bytes3);
        List<Byte> bytes4 = new ArrayList<>(1);
        bytes4.add((byte) 1);
        protocolStructure.addBlock(bytes4);
        injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);
        injectedProtocolStructure.getVarBlock(1).setLibraryInjection();
    }

    @Test
    public void testRun() throws URISyntaxException {
        Path library = Paths.get(getClass().getResource("/library1.txt").toURI());
        LibraryRunner runner = new LibraryRunner(injectedProtocolStructure, InjectionMethod.SEPARATE, 0, library);
        RunnerMonitor monitor = new RunnerMonitor();
        runner.addObserver(monitor);
        runner.run();
        Assert.assertTrue(monitor.areAllStatesVisited());
    }

    @Test
    public void testGetInjectedProtocolStructure() throws URISyntaxException {
        Path library = Paths.get(getClass().getResource("/library1.txt").toURI());

        LibraryRunner libraryRunner =
                new LibraryRunner(injectedProtocolStructure, InjectionMethod.SEPARATE, 0, library);
        libraryRunner.run();
        Assert.assertEquals(libraryRunner.getInjectedProtocolStructure().getVarBlock(0).getLibrary(), library);
        Assert.assertEquals(libraryRunner.getInjectedProtocolStructure().getVarBlock(1).getLibrary(), null);

        libraryRunner = new LibraryRunner(injectedProtocolStructure, InjectionMethod.SIMULTANEOUS, 0, library);
        libraryRunner.run();
        Assert.assertEquals(libraryRunner.getInjectedProtocolStructure().getVarBlock(0).getLibrary(), library);
        Assert.assertEquals(libraryRunner.getInjectedProtocolStructure().getVarBlock(1).getLibrary(), library);
    }
}
