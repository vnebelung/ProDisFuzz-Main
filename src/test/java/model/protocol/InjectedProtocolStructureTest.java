/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.protocol;

import model.process.fuzzoptions.Process.InjectionMethod;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("HardCodedStringLiteral")
public class InjectedProtocolStructureTest {

    @Test
    public void testClear() throws Exception {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        protocolStructure.addBlock(Collections.singletonList(null));
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);
        Assert.assertEquals(injectedProtocolStructure.getSize(), 1);
        injectedProtocolStructure.clear();
        Assert.assertEquals(injectedProtocolStructure.getSize(), 0);
    }

    @Test
    public void testToProtocolStructure() throws Exception {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = Collections.singletonList(null);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = Collections.singletonList((byte) 48);
        protocolStructure.addBlock(bytes2);
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);
        ProtocolStructure protocolStructure2 = injectedProtocolStructure.toProtocolStructure();
        Assert.assertEquals(protocolStructure2.getSize(), 2);
        Assert.assertEquals(protocolStructure2.getBlock(1).getBytes(), bytes2.toArray());
        Assert.assertEquals(protocolStructure2.getBlock(0).getBytes(), bytes1.toArray());
    }

    @Test
    public void testGetSize() throws Exception {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = Collections.singletonList(null);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = Collections.singletonList((byte) 48);
        protocolStructure.addBlock(bytes2);
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);
        Assert.assertEquals(injectedProtocolStructure.getSize(), 2);
    }

    @Test
    public void testGetBlock() throws Exception {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = Collections.singletonList(null);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = Collections.singletonList((byte) 48);
        protocolStructure.addBlock(bytes2);
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);
        Assert.assertEquals(injectedProtocolStructure.getBlock(0).getBytes(), bytes1.toArray());
        Assert.assertEquals(injectedProtocolStructure.getBlock(1).getBytes(), bytes2.toArray());
    }

    @Test
    public void testGetVarBlock() throws Exception {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = Collections.singletonList(null);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = Collections.singletonList((byte) 48);
        protocolStructure.addBlock(bytes2);
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);
        Assert.assertEquals(injectedProtocolStructure.getVarBlock(0).getBytes(), bytes1.toArray());
    }

    @Test
    public void testGetVarSize() throws Exception {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = Collections.singletonList(null);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = Collections.singletonList((byte) 48);
        protocolStructure.addBlock(bytes2);
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);
        Assert.assertEquals(injectedProtocolStructure.getVarSize(), 1);
    }

    @Test
    public void testCopy() throws Exception {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = Collections.singletonList(null);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = Collections.singletonList((byte) 48);
        protocolStructure.addBlock(bytes2);
        InjectedProtocolStructure injectedProtocolStructure1 = new InjectedProtocolStructure(protocolStructure);
        InjectedProtocolStructure injectedProtocolStructure2 = injectedProtocolStructure1.copy();
        //noinspection ObjectEquality
        Assert.assertFalse(injectedProtocolStructure1 == injectedProtocolStructure2);
        for (int i = 0; i < injectedProtocolStructure1.getSize(); i++) {
            //noinspection ObjectEquality
            Assert.assertFalse(injectedProtocolStructure1.getBlock(i) == injectedProtocolStructure2.getBlock(i));
            //noinspection ArrayEquality
            Assert.assertFalse(injectedProtocolStructure1.getBlock(i).getBytes() ==
                    injectedProtocolStructure2.getBlock(i).getBytes());
            Assert.assertEquals(injectedProtocolStructure1.getBlock(i).getBytes(),
                    injectedProtocolStructure2.getBlock(i).getBytes());
        }
    }

    @Test
    public void testGetNumOfIterations() throws URISyntaxException {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = Collections.singletonList(null);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = Collections.singletonList((byte) 48);
        protocolStructure.addBlock(bytes2);
        List<Byte> bytes3 = Collections.singletonList(null);
        protocolStructure.addBlock(bytes3);
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);

        injectedProtocolStructure.getVarBlock(0).setRandomInjection();
        Assert.assertEquals(injectedProtocolStructure.getNumOfIterations(InjectionMethod.SIMULTANEOUS), -1);

        injectedProtocolStructure.getVarBlock(0).setRandomInjection();
        Assert.assertEquals(injectedProtocolStructure.getNumOfIterations(InjectionMethod.SEPARATE), -1);

        injectedProtocolStructure.getVarBlock(0).setLibraryInjection();
        injectedProtocolStructure.getVarBlock(0).setLibrary(Paths.get(getClass().getResource("/library1.txt").toURI()));
        injectedProtocolStructure.getVarBlock(1).setRandomInjection();
        Assert.assertEquals(injectedProtocolStructure.getNumOfIterations(InjectionMethod.SEPARATE), -1);
        Assert.assertEquals(injectedProtocolStructure.getNumOfIterations(InjectionMethod.SIMULTANEOUS), 16);

        injectedProtocolStructure.getVarBlock(1).setLibraryInjection();
        injectedProtocolStructure.getVarBlock(1).setLibrary(Paths.get(getClass().getResource("/library1.txt").toURI()));
        Assert.assertEquals(injectedProtocolStructure.getNumOfIterations(InjectionMethod.SIMULTANEOUS), 16);
        Assert.assertEquals(injectedProtocolStructure.getNumOfIterations(InjectionMethod.SEPARATE), 16 + 16);

    }
}
