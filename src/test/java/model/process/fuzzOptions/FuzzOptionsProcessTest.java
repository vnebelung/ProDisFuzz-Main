/*
 * This file is part of ProDisFuzz, modified on 28.06.15 01:39.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzOptions;

import model.Model;
import model.process.tmp.FuzzOptionsProcess;
import model.process.tmp.FuzzOptionsProcess.CommunicationSave;
import model.protocol.InjectedProtocolBlock.DataInjectionMethod;
import model.protocol.ProtocolStructure;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FuzzOptionsProcessTest {

    private SimulatedServer simulatedServer;
    private ProtocolStructure protocolStructure;

    @BeforeClass
    public void setUp() throws Exception {
        protocolStructure = new ProtocolStructure();
        simulatedServer = new SimulatedServer();
        simulatedServer.start();
        List<Byte> block1 = new ArrayList<>();
        block1.add((byte) 0);
        block1.add((byte) 11);
        protocolStructure.addBlock(block1);
        List<Byte> block2 = new ArrayList<>();
        block2.add(null);
        block2.add(null);
        block2.add(null);
        protocolStructure.addBlock(block2);
        List<Byte> block3 = new ArrayList<>();
        block3.add((byte) 0);
        protocolStructure.addBlock(block3);
        List<Byte> block4 = new ArrayList<>();
        block4.add(null);
        protocolStructure.addBlock(block4);
        List<Byte> block5 = new ArrayList<>();
        block5.add(null);
        protocolStructure.addBlock(block5);
        List<Byte> block6 = new ArrayList<>();
        block6.add((byte) 0);
        block6.add((byte) 11);
        block6.add((byte) 22);
        protocolStructure.addBlock(block6);
    }

    @AfterClass
    public void tearDown() throws Exception {
        simulatedServer.interrupt();
    }

    @Test
    public void testSetSimultaneousInjectionMode() throws Exception {
        Model.INSTANCE.getImportProcess().importXML(Paths.get(getClass().getResource("/protocol.xml").toURI()));
        FuzzOptionsProcess fuzzOptionsProcess = new FuzzOptionsProcess();
        fuzzOptionsProcess.init(protocolStructure);
        fuzzOptionsProcess.setSimultaneousInjectionMode();

        fuzzOptionsProcess.setRandomInjection(1000);
        for (int i = 0; i < fuzzOptionsProcess.getInjectedProtocolStructure().getVarSize(); i++) {
            Assert.assertEquals(fuzzOptionsProcess.getInjectedProtocolStructure().getVarBlock(i).getDataInjectionMethod(), DataInjectionMethod.RANDOM);
        }

        fuzzOptionsProcess.setLibraryInjection(-2);
        for (int i = 0; i < fuzzOptionsProcess.getInjectedProtocolStructure().getVarSize(); i++) {
            Assert.assertEquals(fuzzOptionsProcess.getInjectedProtocolStructure().getVarBlock(i).getDataInjectionMethod(), DataInjectionMethod.LIBRARY);
        }
    }

    @Test
    public void testSetSeparateInjectionMode() throws Exception {
        Model.INSTANCE.getImportProcess().importXML(Paths.get(getClass().getResource("/protocol.xml").toURI()));
        FuzzOptionsProcess fuzzOptionsProcess = new FuzzOptionsProcess();
        fuzzOptionsProcess.init(protocolStructure);
        fuzzOptionsProcess.setSeparateInjectionMode();

        fuzzOptionsProcess.setLibraryInjection(0);
        Assert.assertTrue(fuzzOptionsProcess.getInjectedProtocolStructure().getVarBlock(0).getDataInjectionMethod() == DataInjectionMethod.LIBRARY);
        for (int i = 1; i < fuzzOptionsProcess.getInjectedProtocolStructure().getVarSize(); i++) {
            Assert.assertFalse(fuzzOptionsProcess.getInjectedProtocolStructure().getVarBlock(i).getDataInjectionMethod() == DataInjectionMethod.LIBRARY);
        }

        for (int i = 1; i < fuzzOptionsProcess.getInjectedProtocolStructure().getVarSize(); i++) {
            fuzzOptionsProcess.getInjectedProtocolStructure().getVarBlock(i).setLibraryInjection();
        }
        fuzzOptionsProcess.setRandomInjection(0);
        Assert.assertTrue(fuzzOptionsProcess.getInjectedProtocolStructure().getVarBlock(0).getDataInjectionMethod() == DataInjectionMethod.RANDOM);
        for (int i = 1; i < fuzzOptionsProcess.getInjectedProtocolStructure().getVarSize(); i++) {
            Assert.assertFalse(fuzzOptionsProcess.getInjectedProtocolStructure().getVarBlock(i).getDataInjectionMethod() == DataInjectionMethod.RANDOM);
        }
    }

    @Test
    public void testSetSaveAllCommunication() throws Exception {
        FuzzOptionsProcess fuzzOptionsProcess = new FuzzOptionsProcess();

        fuzzOptionsProcess.setSaveAllCommunication();
        Assert.assertEquals(fuzzOptionsProcess.getSaveCommunication(), CommunicationSave.ALL);

    }

    @Test
    public void testSetSaveCriticalCommunication() throws Exception {
        FuzzOptionsProcess fuzzOptionsProcess = new FuzzOptionsProcess();

        fuzzOptionsProcess.setSaveCriticalCommunication();
        Assert.assertEquals(fuzzOptionsProcess.getSaveCommunication(), CommunicationSave.CRITICAL);
    }

    @Test
    public void testSetTarget() throws Exception {
        FuzzOptionsProcess fuzzOptionsProcess = new FuzzOptionsProcess();
        Assert.assertNull(fuzzOptionsProcess.getTarget());

        fuzzOptionsProcess.setTarget("localhost", 10030);
        Assert.assertEquals(fuzzOptionsProcess.getTarget(), new InetSocketAddress("localhost", 10030));

        fuzzOptionsProcess.setTarget("localhost", -2);
        Assert.assertNull(fuzzOptionsProcess.getTarget());
    }

    @Test
    public void testIsTargetReachable() throws Exception {
        FuzzOptionsProcess fuzzOptionsProcess = new FuzzOptionsProcess();

        fuzzOptionsProcess.setTarget("dummy", 10030);
        Assert.assertFalse(fuzzOptionsProcess.isTargetReachable());

        fuzzOptionsProcess.setTarget("localhost", 10030);
        Assert.assertTrue(fuzzOptionsProcess.isTargetReachable());

        fuzzOptionsProcess.setTarget("localhost", -2);
        Assert.assertFalse(fuzzOptionsProcess.isTargetReachable());
    }

    @Test
    public void testSetInterval() throws Exception {
        FuzzOptionsProcess fuzzOptionsProcess = new FuzzOptionsProcess();

        fuzzOptionsProcess.setInterval(-2);
        Assert.assertFalse(fuzzOptionsProcess.getInterval() == -2);

        fuzzOptionsProcess.setInterval(Integer.MAX_VALUE);
        Assert.assertFalse(fuzzOptionsProcess.getInterval() == Integer.MAX_VALUE);

        fuzzOptionsProcess.setInterval(5000);
        Assert.assertEquals(fuzzOptionsProcess.getInterval(), 5000);
    }

    @Test
    public void testSetLibraryInjection() throws Exception {
        Model.INSTANCE.getImportProcess().importXML(Paths.get(getClass().getResource("/protocol.xml").toURI()));
        FuzzOptionsProcess fuzzOptionsProcess = new FuzzOptionsProcess();
        fuzzOptionsProcess.init(protocolStructure);

        fuzzOptionsProcess.setSimultaneousInjectionMode();
        fuzzOptionsProcess.setLibraryInjection(0);
        for (int i = 0; i < fuzzOptionsProcess.getInjectedProtocolStructure().getVarSize(); i++) {
            Assert.assertTrue(fuzzOptionsProcess.getInjectedProtocolStructure().getVarBlock(i).getDataInjectionMethod() == DataInjectionMethod.LIBRARY);
        }

        fuzzOptionsProcess.setRandomInjection(0);
        fuzzOptionsProcess.setSeparateInjectionMode();
        fuzzOptionsProcess.setLibraryInjection(0);
        Assert.assertTrue(fuzzOptionsProcess.getInjectedProtocolStructure().getVarBlock(0).getDataInjectionMethod() == DataInjectionMethod.LIBRARY);
        for (int i = 1; i < fuzzOptionsProcess.getInjectedProtocolStructure().getVarSize(); i++) {
            Assert.assertFalse(fuzzOptionsProcess.getInjectedProtocolStructure().getVarBlock(i).getDataInjectionMethod() == DataInjectionMethod.LIBRARY);
        }
    }

    @Test
    public void testSetLibrary() throws Exception {
        Model.INSTANCE.getImportProcess().importXML(Paths.get(getClass().getResource("/protocol.xml").toURI()));
        FuzzOptionsProcess fuzzOptionsProcess = new FuzzOptionsProcess();
        fuzzOptionsProcess.init(protocolStructure);
        Path path = Paths.get(getClass().getResource("/library.txt").toURI());

        fuzzOptionsProcess.setSimultaneousInjectionMode();
        fuzzOptionsProcess.setLibraryInjection(0);
        fuzzOptionsProcess.setLibrary(path, 0);
        for (int i = 0; i < fuzzOptionsProcess.getInjectedProtocolStructure().getVarSize(); i++) {
            Assert.assertEquals(fuzzOptionsProcess.getInjectedProtocolStructure().getVarBlock(i).getLibrary(), path);
        }

        fuzzOptionsProcess.setRandomInjection(0);
        fuzzOptionsProcess.setSeparateInjectionMode();
        fuzzOptionsProcess.setLibraryInjection(0);
        fuzzOptionsProcess.setLibrary(path, 0);
        Assert.assertEquals(fuzzOptionsProcess.getInjectedProtocolStructure().getVarBlock(0).getLibrary(), path);
        for (int i = 1; i < fuzzOptionsProcess.getInjectedProtocolStructure().getVarSize(); i++) {
            Assert.assertNull(fuzzOptionsProcess.getInjectedProtocolStructure().getVarBlock(i).getLibrary());
        }
    }

    @Test
    public void testSetRandomInjection() throws Exception {
        Model.INSTANCE.getImportProcess().importXML(Paths.get(getClass().getResource("/protocol.xml").toURI()));
        FuzzOptionsProcess fuzzOptionsProcess = new FuzzOptionsProcess();
        fuzzOptionsProcess.init(protocolStructure);

        fuzzOptionsProcess.setSimultaneousInjectionMode();
        fuzzOptionsProcess.setRandomInjection(0);
        for (int i = 0; i < fuzzOptionsProcess.getInjectedProtocolStructure().getVarSize(); i++) {
            Assert.assertEquals(fuzzOptionsProcess.getInjectedProtocolStructure().getVarBlock(i).getDataInjectionMethod(), DataInjectionMethod.RANDOM);
        }

        fuzzOptionsProcess.setLibraryInjection(0);
        fuzzOptionsProcess.setSeparateInjectionMode();
        fuzzOptionsProcess.setRandomInjection(0);
        for (int i = 0; i < fuzzOptionsProcess.getInjectedProtocolStructure().getVarSize(); i++) {
            Assert.assertEquals(fuzzOptionsProcess.getInjectedProtocolStructure().getVarBlock(i).getDataInjectionMethod(), DataInjectionMethod.RANDOM);
        }
    }
}
