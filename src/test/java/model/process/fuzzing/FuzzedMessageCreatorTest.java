/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import model.process.fuzzoptions.Process.InjectionMethod;
import model.protocol.InjectedProtocolStructure;
import model.protocol.ProtocolStructure;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"HardCodedStringLiteral", "NumericCastThatLosesPrecision"})
public class FuzzedMessageCreatorTest {

    @Test
    public void testCall1() throws Exception {
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

        FuzzedMessageCreator fuzzedMessageCreator =
                new FuzzedMessageCreator(injectedProtocolStructure, InjectionMethod.SIMULTANEOUS);
        byte[] bytes = fuzzedMessageCreator.call();
        Assert.assertTrue(bytes.length % 2 == 0);
        for (int i = 0; i < bytes.length / 2; i++) {
            Assert.assertTrue(bytes[i] == bytes[bytes.length / 2 + i]);
        }
    }

    @Test
    public void testCall2() throws Exception {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = new ArrayList<>(1);
        bytes1.add(null);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = new ArrayList<>(1);
        bytes2.add((byte) 0);
        bytes2.add((byte) 0);
        bytes2.add((byte) 0);
        bytes2.add((byte) 0);
        protocolStructure.addBlock(bytes2);
        List<Byte> bytes3 = new ArrayList<>(1);
        bytes3.add(null);
        protocolStructure.addBlock(bytes3);
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);

        for (int i = 0; i < 10000; i++) {
            FuzzedMessageCreator fuzzedMessageCreator =
                    new FuzzedMessageCreator(injectedProtocolStructure, InjectionMethod.SEPARATE);
            byte[] bytes = fuzzedMessageCreator.call();
            byte[] separator = new byte[bytes2.size()];
            for (int j = 0; j < bytes2.size(); j++) {
                separator[j] = bytes2.get(j);
            }
            Assert.assertTrue(containsSubArray(bytes, separator));
        }
    }

    @SuppressWarnings("MethodCanBeVariableArityMethod")
    private static boolean containsSubArray(byte[] bytes, byte[] subBytes) {
        //noinspection LoopStatementThatDoesntLoop
        for (int i = 0; i < bytes.length - subBytes.length; i++) {
            for (int j = 0; j < subBytes.length; j++) {
                if (bytes[i + j] != subBytes[j]) {
                    break;
                }
            }
            return true;
        }
        return false;
    }

    @Test
    public void testCall3() throws URISyntaxException {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = new ArrayList<>(1);
        bytes1.add(null);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = new ArrayList<>(1);
        bytes2.add((byte) 0);
        bytes2.add((byte) 0);
        bytes2.add((byte) 0);
        bytes2.add((byte) 0);
        protocolStructure.addBlock(bytes2);
        List<Byte> bytes3 = new ArrayList<>(1);
        bytes3.add(null);
        protocolStructure.addBlock(bytes3);
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);
        injectedProtocolStructure.getVarBlock(0).setLibraryInjection();
        injectedProtocolStructure.getVarBlock(0).setLibrary(Paths.get(getClass().getResource("/library1.txt").toURI()));

        FuzzedMessageCreator fuzzedMessageCreator =
                new FuzzedMessageCreator(injectedProtocolStructure, InjectionMethod.SIMULTANEOUS);
        for (int i = 0; i < 16; i++) {
            byte[] bytes = fuzzedMessageCreator.call();
            Assert.assertEquals(bytes[0], (byte) (i * 17));
            Assert.assertEquals(bytes[1], (byte) 0);
            Assert.assertEquals(bytes[2], (byte) 0);
            Assert.assertEquals(bytes[3], (byte) 0);
            Assert.assertEquals(bytes[4], (byte) 0);
            Assert.assertEquals(bytes[5], (byte) (i * 17));
        }
        Assert.assertNull(fuzzedMessageCreator.call());
    }

    @Test
    public void testCall4() throws URISyntaxException {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = new ArrayList<>(1);
        bytes1.add(null);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = new ArrayList<>(1);
        bytes2.add((byte) 0);
        bytes2.add((byte) 0);
        bytes2.add((byte) 0);
        bytes2.add((byte) 0);
        protocolStructure.addBlock(bytes2);
        List<Byte> bytes3 = new ArrayList<>(1);
        bytes3.add(null);
        protocolStructure.addBlock(bytes3);
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);
        injectedProtocolStructure.getVarBlock(0).setLibraryInjection();
        injectedProtocolStructure.getVarBlock(0).setLibrary(Paths.get(getClass().getResource("/library1.txt").toURI()));
        injectedProtocolStructure.getVarBlock(1).setLibraryInjection();
        injectedProtocolStructure.getVarBlock(1).setLibrary(Paths.get(getClass().getResource("/library1.txt").toURI()));

        FuzzedMessageCreator fuzzedMessageCreator =
                new FuzzedMessageCreator(injectedProtocolStructure, InjectionMethod.SEPARATE);
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 16; j++) {
                byte[] bytes = fuzzedMessageCreator.call();
                // Either the first or the second variable block
                Assert.assertEquals(bytes[i * 5], (byte) (j * 17));
                Assert.assertEquals(bytes[1], (byte) 0);
                Assert.assertEquals(bytes[2], (byte) 0);
                Assert.assertEquals(bytes[3], (byte) 0);
                Assert.assertEquals(bytes[4], (byte) 0);
            }
        }
        Assert.assertNull(fuzzedMessageCreator.call());
    }

    @Test
    public void testCall5() throws URISyntaxException {
        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = new ArrayList<>(1);
        bytes1.add(null);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = new ArrayList<>(1);
        bytes2.add((byte) 0);
        bytes2.add((byte) 0);
        bytes2.add((byte) 0);
        bytes2.add((byte) 0);
        protocolStructure.addBlock(bytes2);
        List<Byte> bytes3 = new ArrayList<>(1);
        bytes3.add(null);
        protocolStructure.addBlock(bytes3);
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);
        injectedProtocolStructure.getVarBlock(0).setLibraryInjection();
        injectedProtocolStructure.getVarBlock(0).setLibrary(Paths.get(getClass().getResource("/library1.txt").toURI()));

        FuzzedMessageCreator fuzzedMessageCreator =
                new FuzzedMessageCreator(injectedProtocolStructure, InjectionMethod.SEPARATE);
        for (int i = 0; i < 1000; i++) {
            byte[] bytes = fuzzedMessageCreator.call();
            // +256 because of negative byte values
            if (bytes[0] >= 0) {
                Assert.assertTrue(bytes[0] % 17 == 0);
            } else {
                Assert.assertTrue((bytes[0] + 256) % 17 == 0);
            }
            Assert.assertEquals(bytes[1], (byte) 0);
            Assert.assertEquals(bytes[2], (byte) 0);
            Assert.assertEquals(bytes[3], (byte) 0);
            Assert.assertEquals(bytes[4], (byte) 0);
        }
    }
}
