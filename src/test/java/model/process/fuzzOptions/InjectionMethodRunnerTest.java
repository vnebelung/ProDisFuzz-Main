/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import model.process.fuzzoptions.Process.InjectionMethod;
import model.protocol.InjectedProtocolBlock.DataInjection;
import model.protocol.InjectedProtocolStructure;
import model.protocol.ProtocolStructure;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class InjectionMethodRunnerTest {

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
    }

    @SuppressWarnings("EmptyMethod")
    @Test
    public void testGetInjectionMethod() throws Exception {
        // See testRun
    }

    @Test
    public void testRun() throws Exception {
        injectedProtocolStructure.getVarBlock(1).setLibraryInjection();

        InjectionMethodRunner injectionMethodRunner =
                new InjectionMethodRunner(injectedProtocolStructure, InjectionMethod.SIMULTANEOUS);
        injectionMethodRunner.run();
        InjectedProtocolStructure result = injectionMethodRunner.getInjectedProtocolStructure();
        Assert.assertEquals(result.getVarBlock(0).getDataInjection(), DataInjection.RANDOM);
        Assert.assertEquals(result.getVarBlock(1).getDataInjection(), DataInjection.RANDOM);
        Assert.assertEquals(injectionMethodRunner.getInjectionMethod(), InjectionMethod.SIMULTANEOUS);

        injectedProtocolStructure.getVarBlock(0).setLibraryInjection();
        injectedProtocolStructure.getVarBlock(1).setLibraryInjection();
        injectionMethodRunner = new InjectionMethodRunner(injectedProtocolStructure, InjectionMethod.SEPARATE);
        injectionMethodRunner.run();
        result = injectionMethodRunner.getInjectedProtocolStructure();
        Assert.assertEquals(result.getVarBlock(0).getDataInjection(), DataInjection.LIBRARY);
        Assert.assertEquals(result.getVarBlock(1).getDataInjection(), DataInjection.RANDOM);
        Assert.assertEquals(injectionMethodRunner.getInjectionMethod(), InjectionMethod.SEPARATE);
    }

    @SuppressWarnings("EmptyMethod")
    @Test
    public void testGetInjectedProtocolStructure() throws Exception {
        // See testRun
    }
}
