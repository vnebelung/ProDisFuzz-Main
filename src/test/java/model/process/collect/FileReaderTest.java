/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.collect;

import model.protocol.ProtocolFile;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("HardCodedStringLiteral")
public class FileReaderTest {

    private Path tmp;
    private Path a;
    @SuppressWarnings("StandardVariableNames")
    private Path b;
    @SuppressWarnings("StandardVariableNames")
    private Path c;

    @BeforeClass
    public void setUp() throws IOException, URISyntaxException {
        tmp = Files.createTempDirectory(null);
        a = Files.copy(Paths.get(getClass().getResource("/capture1.txt").toURI()),
                tmp.resolve(Paths.get(getClass().getResource("/capture1.txt").toURI()).getFileName()));
        b = Files.copy(Paths.get(getClass().getResource("/capture2.txt").toURI()),
                tmp.resolve(Paths.get(getClass().getResource("/capture2.txt").toURI()).getFileName()));
        c = Files.copy(Paths.get(getClass().getResource("/library1.txt").toURI()),
                tmp.resolve(Paths.get(getClass().getResource("/library1.txt").toURI()).getFileName()));
    }

    @AfterClass
    public void tearDown() throws IOException {
        Files.delete(a);
        Files.delete(b);
        Files.delete(c);
        Files.delete(tmp);
    }

    @Test
    public void testCall() throws URISyntaxException, IOException {
        FileReader fileReader = new FileReader(tmp);
        List<ProtocolFile> protocolFiles2 = fileReader.call();
        //noinspection TypeMayBeWeakened
        List<ProtocolFile> protocolFiles1 = new ArrayList<>(6);
        protocolFiles1.add(new ProtocolFile(Paths.get(getClass().getResource("/capture1.txt").toURI())));
        protocolFiles1.add(new ProtocolFile(Paths.get(getClass().getResource("/capture2.txt").toURI())));
        protocolFiles1.add(new ProtocolFile(Paths.get(getClass().getResource("/library1.txt").toURI())));
        Assert.assertEquals(protocolFiles2.size(), protocolFiles1.size());
        for (ProtocolFile eachOriginFile : protocolFiles1) {
            boolean found = false;
            for (ProtocolFile eachActualFile : protocolFiles2) {
                if (eachOriginFile.getName().equals(eachActualFile.getName())) {
                    found = true;
                    break;
                }
            }
            Assert.assertTrue(found);
        }
    }
}
