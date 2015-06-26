/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.collect;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CollectProcessTest {

    private CollectProcess collectProcess;

    @BeforeClass
    public void setUp() throws Exception {
        collectProcess = new CollectProcess();
    }

    @Test
    public void testSetFolder() throws Exception {
        //noinspection HardCodedStringLiteral
        collectProcess.setFolder("dummy");
        Assert.assertFalse(collectProcess.isFolderValid());

        collectProcess.setFolder(Paths.get("").toAbsolutePath().toString());
        Assert.assertTrue(collectProcess.isFolderValid());
    }

    @Test
    public void testReset() throws Exception {
        collectProcess.setFolder(Paths.get("").toAbsolutePath().toString());
        Assert.assertTrue(collectProcess.getFiles().length > 0);
        collectProcess.reset();
        Assert.assertEquals(collectProcess.getFiles().length, 0);
    }

    @Test
    public void testSetSelected() throws IOException {
        collectProcess.setFolder(Paths.get("").toAbsolutePath().toString());
        int i = 0;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get("").toAbsolutePath())) {
            for (Path each : stream) {
                if (Files.isRegularFile(each) && Files.isReadable(each) && !Files.isHidden(each)) {
                    collectProcess.setSelected(each.getFileName().toString(), (i % 2) == 0);
                    Assert.assertEquals(collectProcess.isSelected(each.getFileName().toString()), (i % 2) == 0);
                    i++;
                }
            }
        }
        Assert.assertEquals(collectProcess.getNumOfSelectedFiles(), (i + 1) / 2);
        Assert.assertEquals(collectProcess.getSelectedFiles().length, (i + 1) / 2);
    }
}
