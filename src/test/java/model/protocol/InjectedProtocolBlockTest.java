/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.protocol;

import model.protocol.InjectedProtocolBlock.DataInjection;
import model.protocol.ProtocolBlock.Type;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@SuppressWarnings({"NumericCastThatLosesPrecision", "HardCodedStringLiteral"})
public class InjectedProtocolBlockTest {

    public static double chiSquare(int[] counts, int iterationCount) {
        double n0i = iterationCount * 1.0 / counts.length;
        double result = 0;
        for (int each : counts) {
            result += Math.pow(each - n0i, 2) / n0i;
        }
        return result;
    }

    @Test
    public void testGetDataInjection() throws Exception {
        // See testSetLibraryInjection and testSetRandomInjection for not null
        InjectedProtocolBlock injectedProtocolBlock = new InjectedProtocolBlock(Type.FIX, (byte) 0, (byte) 1);
        Assert.assertNull(injectedProtocolBlock.getDataInjection());
    }

    @SuppressWarnings("EmptyMethod")
    @Test
    public void testGetLibrary() throws Exception {
        // See testSetLibrary
    }

    @Test
    public void testCopy() throws URISyntaxException {
        InjectedProtocolBlock injectedProtocolBlock1 = new InjectedProtocolBlock(Type.VAR, (byte) 0, (byte) 1);
        injectedProtocolBlock1.setLibraryInjection();
        injectedProtocolBlock1.setLibrary(Paths.get(getClass().getResource("/library1.txt").toURI()));
        InjectedProtocolBlock injectedProtocolBlock2 = injectedProtocolBlock1.copy();

        //noinspection ObjectEquality
        Assert.assertFalse(injectedProtocolBlock1 == injectedProtocolBlock2);

        Assert.assertTrue(injectedProtocolBlock1.getDataInjection() == injectedProtocolBlock2.getDataInjection());

        Assert.assertTrue(injectedProtocolBlock1.getLibrary().equals(injectedProtocolBlock2.getLibrary()));
        //noinspection ObjectEquality
        Assert.assertFalse(injectedProtocolBlock1.getLibrary() == injectedProtocolBlock2.getLibrary());

        Assert.assertTrue(injectedProtocolBlock1.getBytes().length == injectedProtocolBlock2.getBytes().length);
        for (int i = 0; i < injectedProtocolBlock1.getBytes().length; i++) {
            Assert.assertTrue(injectedProtocolBlock1.getBytes()[i].equals(injectedProtocolBlock2.getBytes()[i]));
            //noinspection NumberEquality
            Assert.assertFalse(injectedProtocolBlock1.getBytes()[i] == injectedProtocolBlock2.getBytes()[i]);
        }
    }

    @Test
    public void testSetLibrary() throws URISyntaxException, IOException {
        Byte[] bytes = {null};
        //noinspection UnqualifiedInnerClassAccess
        InjectedProtocolBlock injectedProtocolBlock = new InjectedProtocolBlock(Type.VAR, bytes);
        //noinspection HardCodedStringLiteral
        Path path = Paths.get(getClass().getResource("/library1.txt").toURI());
        injectedProtocolBlock.setLibrary(path);
        Assert.assertEquals(injectedProtocolBlock.getLibrary(), path);

        injectedProtocolBlock = new InjectedProtocolBlock(Type.FIX, bytes);
        injectedProtocolBlock.setLibrary(path);
        Assert.assertNull(injectedProtocolBlock.getLibrary());

        injectedProtocolBlock = new InjectedProtocolBlock(Type.VAR, bytes);
        injectedProtocolBlock.setLibrary(Paths.get(getClass().getResource("/").toURI()));
        Assert.assertNull(injectedProtocolBlock.getLibrary());
    }

    @Test
    public void testSetLibraryInjection() throws Exception {
        Byte[] bytes = {null};
        //noinspection UnqualifiedInnerClassAccess
        InjectedProtocolBlock injectedProtocolBlock = new InjectedProtocolBlock(Type.VAR, bytes);
        injectedProtocolBlock.setLibraryInjection();
        Assert.assertEquals(injectedProtocolBlock.getDataInjection(), DataInjection.LIBRARY);

        injectedProtocolBlock = new InjectedProtocolBlock(Type.FIX, bytes);
        injectedProtocolBlock.setLibraryInjection();
        Assert.assertNull(injectedProtocolBlock.getDataInjection());
    }

    @Test
    public void testSetRandomInjection() throws Exception {
        Byte[] bytes = {null};
        //noinspection UnqualifiedInnerClassAccess
        InjectedProtocolBlock injectedProtocolBlock = new InjectedProtocolBlock(Type.VAR, bytes);
        injectedProtocolBlock.setRandomInjection();
        Assert.assertEquals(injectedProtocolBlock.getDataInjection(), DataInjection.RANDOM);

        injectedProtocolBlock = new InjectedProtocolBlock(Type.FIX, bytes);
        injectedProtocolBlock.setRandomInjection();
        Assert.assertNull(injectedProtocolBlock.getDataInjection());
    }

    @Test
    public void testGetNumOfLibraryLines() throws URISyntaxException {
        Byte[] bytes = {null};
        //noinspection UnqualifiedInnerClassAccess
        InjectedProtocolBlock injectedProtocolBlock = new InjectedProtocolBlock(Type.VAR, bytes);
        //noinspection HardCodedStringLiteral
        Path path = Paths.get(getClass().getResource("/library1.txt").toURI());
        injectedProtocolBlock.setLibraryInjection();
        injectedProtocolBlock.setLibrary(path);
        Assert.assertEquals(injectedProtocolBlock.getNumOfLibraryLines(), 16);
    }

    @Test
    public void testGetLibraryLine() throws URISyntaxException, IOException {
        Byte[] bytes = {null};
        //noinspection UnqualifiedInnerClassAccess
        InjectedProtocolBlock injectedProtocolBlock = new InjectedProtocolBlock(Type.VAR, bytes);
        //noinspection HardCodedStringLiteral
        Path path = Paths.get(getClass().getResource("/library1.txt").toURI());
        injectedProtocolBlock.setLibrary(path);
        for (int i = 0; i < injectedProtocolBlock.getNumOfLibraryLines(); i++) {
            Assert.assertEquals(injectedProtocolBlock.getLibraryLine(i), new byte[]{(byte) (i * 11)});
        }

        Path tmpFile = Files.createTempFile(null, null);
        Files.copy(path, tmpFile, StandardCopyOption.REPLACE_EXISTING);
        injectedProtocolBlock = new InjectedProtocolBlock(Type.VAR, bytes);
        injectedProtocolBlock.setLibrary(tmpFile);
        Files.delete(tmpFile);
        Assert.assertNull(injectedProtocolBlock.getLibraryLine(0));
    }

    @Test
    public void testGetRandomLibraryLine() throws URISyntaxException {
        Byte[] bytes = {null};
        //noinspection UnqualifiedInnerClassAccess
        InjectedProtocolBlock injectedProtocolBlock = new InjectedProtocolBlock(Type.VAR, bytes);
        //noinspection HardCodedStringLiteral
        Path path = Paths.get(getClass().getResource("/library1.txt").toURI());
        injectedProtocolBlock.setLibraryInjection();
        injectedProtocolBlock.setLibrary(path);
        int[] counts = new int[injectedProtocolBlock.getNumOfLibraryLines()];
        int iterationCount = 10000;
        for (int i = 0; i < iterationCount; i++) {
            byte[] line = injectedProtocolBlock.getRandomLibraryLine();
            // Chosen line no. cannot be accessed directly, so we count the unique bytes of a one byte sized line
            counts[Byte.toUnsignedInt(line[0]) / 17]++;
        }
        double chiSquareValue = chiSquare(counts, iterationCount);
        // Fix chi square value
        Assert.assertTrue(chiSquareValue < 24.9958);
    }
}
