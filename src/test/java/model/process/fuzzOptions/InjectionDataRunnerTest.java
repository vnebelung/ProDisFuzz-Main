/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
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
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class InjectionDataRunnerTest {

    @Test
    public void testRun() throws Exception {
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
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);

        InjectionDataRunner injectionDataRunner =
                new InjectionDataRunner(injectedProtocolStructure, DataInjection.LIBRARY, InjectionMethod.SIMULTANEOUS,
                        0);
        injectionDataRunner.run();
        InjectedProtocolStructure result = injectionDataRunner.getInjectedProtocolStructure();
        Assert.assertEquals(result.getVarBlock(0).getDataInjection(), DataInjection.LIBRARY);
        Assert.assertEquals(result.getVarBlock(1).getDataInjection(), DataInjection.LIBRARY);

        injectionDataRunner =
                new InjectionDataRunner(injectedProtocolStructure, DataInjection.RANDOM, InjectionMethod.SEPARATE, 1);
        injectionDataRunner.run();
        result = injectionDataRunner.getInjectedProtocolStructure();
        Assert.assertEquals(result.getVarBlock(0).getDataInjection(), DataInjection.LIBRARY);
        Assert.assertEquals(result.getVarBlock(1).getDataInjection(), DataInjection.RANDOM);
    }

    @SuppressWarnings("EmptyMethod")
    @Test
    public void testGetInjectedProtocolStructure() throws Exception {
        // See testRun
    }
}
