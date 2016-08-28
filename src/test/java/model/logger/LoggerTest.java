/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.logger;

import model.logger.Entry.Type;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SuppressWarnings({"HardCodedStringLiteral", "resource"})
public class LoggerTest {

    private Logger logger;

    @BeforeClass
    public void setUp() {
        logger = new Logger();
    }

    @Test
    public void testInfo() throws Exception {
        logger.info("testinfo1");
        Entry[] entries = logger.getUnreadEntries();
        Assert.assertEquals(entries.length, 1);
        Assert.assertEquals(entries[0].getType(), Type.INFO);
        Assert.assertEquals(entries[0].getText(), "testinfo1");
        Assert.assertEquals(entries[0].getTime().truncatedTo(ChronoUnit.SECONDS).toString(), Instant.now()
                .truncatedTo(ChronoUnit.SECONDS).toString());
    }

    @Test
    public void testFine() throws Exception {
        logger.fine("testfine1");
        Entry[] entries = logger.getUnreadEntries();
        Assert.assertEquals(entries.length, 1);
        Assert.assertEquals(entries[0].getType(), Type.FINE);
        Assert.assertEquals(entries[0].getText(), "testfine1");
        Assert.assertEquals(entries[0].getTime().truncatedTo(ChronoUnit.SECONDS).toString(), Instant.now()
                .truncatedTo(ChronoUnit.SECONDS).toString());
    }

    @Test
    public void testError() throws Exception {
        logger.error("testerror1");
        Entry[] entries = logger.getUnreadEntries();
        Assert.assertEquals(entries.length, 1);
        Assert.assertEquals(entries[0].getType(), Type.ERROR);
        Assert.assertEquals(entries[0].getText(), "testerror1");
        Assert.assertEquals(entries[0].getTime().truncatedTo(ChronoUnit.SECONDS).toString(), Instant.now()
                .truncatedTo(ChronoUnit.SECONDS).toString());
    }

    @Test
    public void testError1() throws Exception {
        Throwable throwable = new Throwable("testerror1");
        logger.error(throwable);
        Entry[] entries = logger.getUnreadEntries();
        Assert.assertEquals(entries.length, 1);
        Assert.assertEquals(entries[0].getType(), Type.ERROR);
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        Assert.assertEquals(entries[0].getText(), sw.toString());
        Assert.assertEquals(entries[0].getTime().truncatedTo(ChronoUnit.SECONDS).toString(), Instant.now()
                .truncatedTo(ChronoUnit.SECONDS).toString());
    }

    @Test
    public void testWarning() throws Exception {
        logger.warning("testwarning1");
        Entry[] entries = logger.getUnreadEntries();
        Assert.assertEquals(entries.length, 1);
        Assert.assertEquals(entries[0].getType(), Type.WARNING);
        Assert.assertEquals(entries[0].getText(), "testwarning1");
        Assert.assertEquals(entries[0].getTime().truncatedTo(ChronoUnit.SECONDS).toString(), Instant.now()
                .truncatedTo(ChronoUnit.SECONDS).toString());
    }

    @Test
    public void testGetUnreadEntries() throws Exception {
        logger.error("");
        logger.warning("");
        Assert.assertEquals(logger.getUnreadEntries().length, 2);
        Assert.assertEquals(logger.getUnreadEntries().length, 0);
    }

    @Test
    public void testReset() throws Exception {
        logger.error("");
        logger.warning("");
        logger.reset();
        Assert.assertEquals(logger.getUnreadEntries().length, 0);
    }
}
