/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.protocol;

import model.util.Hex;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.regex.Pattern;

@SuppressWarnings("HardCodedStringLiteral")
public class ProtocolFileTest {

    private static final Pattern QUOTE_SIGN = Pattern.compile("\"");

    @Test
    public void testGetName() throws URISyntaxException {
        ProtocolFile protocolFile = new ProtocolFile(Paths.get(getClass().getResource("/library1.txt").toURI()));
        Assert.assertEquals(protocolFile.getName(), "library1.txt");
    }

    @Test
    public void testGetSha256() throws URISyntaxException {
        ProtocolFile protocolFile = new ProtocolFile(Paths.get(getClass().getResource("/library1.txt").toURI()));
        Assert.assertEquals(protocolFile.getSha256(),
                "bc75f2894523f621802c5f4236abce8be0c55c9d2ea7a7b4d842f412f8effb63");
    }

    @Test
    public void testGetSize() throws URISyntaxException {
        ProtocolFile protocolFile = new ProtocolFile(Paths.get(getClass().getResource("/library1.txt").toURI()));
        Assert.assertEquals(protocolFile.getSize(), 48);

    }

    @Test
    public void testGetLastModified() throws URISyntaxException, IOException, InterruptedException {
        ProtocolFile protocolFile = new ProtocolFile(Paths.get(getClass().getResource("/library1.txt").toURI()));
        Runtime runtime = Runtime.getRuntime();
        //noinspection CallToRuntimeExecWithNonConstantString
        Process process = runtime.exec(
                "stat -f \"%m\" " + Paths.get(getClass().getResource("/library1.txt").toURI()).toAbsolutePath());
        try (BufferedReader input = new BufferedReader(
                new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            String line = input.readLine();
            process.waitFor();
            line = QUOTE_SIGN.matcher(line).replaceAll("");
            long modifiedDateTime = Long.parseLong(line);
            Assert.assertEquals(protocolFile.getLastModified(), Instant.ofEpochSecond(modifiedDateTime).toEpochMilli());
        }
    }

    @Test
    public void testGetContent() throws URISyntaxException {
        ProtocolFile protocolFile = new ProtocolFile(Paths.get(getClass().getResource("/library1.txt").toURI()));
        Assert.assertEquals(protocolFile.getContent(), Hex.hexBin2Byte(
                "30300A31310A32320A33330A34340A35350A36360A37370A38380A39390A61610A62620A63630A64640A65650A66660A"));
    }

    @Test
    public void testCompareTo() throws URISyntaxException {
        ProtocolFile protocolFile1 = new ProtocolFile(Paths.get(getClass().getResource("/library1.txt").toURI()));
        ProtocolFile protocolFile2 = new ProtocolFile(Paths.get(getClass().getResource("/library2.txt").toURI()));
        ProtocolFile protocolFile3 = new ProtocolFile(Paths.get(getClass().getResource("/library1.txt").toURI()));
        Assert.assertTrue(protocolFile1.compareTo(protocolFile2) < 0);
        Assert.assertTrue(protocolFile1.compareTo(protocolFile3) == 0);
        Assert.assertTrue(protocolFile2.compareTo(protocolFile1) > 0);
        Assert.assertTrue(protocolFile2.compareTo(protocolFile3) > 0);
        Assert.assertTrue(protocolFile3.compareTo(protocolFile1) == 0);
        Assert.assertTrue(protocolFile3.compareTo(protocolFile2) < 0);
    }

}
