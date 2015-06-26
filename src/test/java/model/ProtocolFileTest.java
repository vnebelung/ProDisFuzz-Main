/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("HardCodedStringLiteral")
public class ProtocolFileTest {

    private Path path;
    private ProtocolFile protocolFile;

    @BeforeMethod
    public void setUp() throws IOException {
        path = Files.createTempFile("b_", null);
        Files.write(path, "123".getBytes(StandardCharsets.UTF_8));
        protocolFile = new ProtocolFile(path);
    }

    @AfterMethod
    public void tearDown() throws IOException {
        Files.delete(path);
    }

    @Test
    public void testGetName() throws Exception {
        Pattern pattern = Pattern.compile("[^\\.]+\\.[^\\.]+");
        Matcher matcher = pattern.matcher(protocolFile.getName());
        Assert.assertTrue(matcher.matches());
    }

    @Test
    public void testGetSha256() throws Exception {
        Assert.assertEquals(protocolFile.getSha256(),
                "a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3");
    }

    @Test
    public void testGetSize() throws Exception {
        Assert.assertEquals(protocolFile.getSize(), 3);
    }

    @Test
    public void testGetLastModified() throws IOException {
        Assert.assertEquals(Files.getLastModifiedTime(path).toMillis(), protocolFile.getLastModified());
    }

    @Test
    public void testGetContent() throws Exception {
        Assert.assertEquals("123".getBytes(StandardCharsets.UTF_8), protocolFile.getContent());
    }

    @Test
    public void testCompareTo() throws IOException {
        Path path1 = Files.createTempFile("a_", null);
        Path path3 = Files.createTempFile("c_", null);
        ProtocolFile protocolFile1 = new ProtocolFile(path1);
        ProtocolFile protocolFile3 = new ProtocolFile(path3);
        Assert.assertTrue(protocolFile.compareTo(protocolFile1) > 0);
        Assert.assertTrue(protocolFile.compareTo(protocolFile3) < 0);
        Files.delete(path1);
        Files.delete(path3);
    }
}
