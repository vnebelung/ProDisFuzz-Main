/*
 * This file is part of ProDisFuzz, modified on 6/28/15 12:31 AM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.logger;

import model.logger.Message.Type;
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
        Message[] messages = logger.getUnreadEntries();
        Assert.assertEquals(messages.length, 1);
        Assert.assertEquals(messages[0].getType(), Type.INFO);
        Assert.assertEquals(messages[0].getText(), "testinfo1");
        Assert.assertEquals(messages[0].getTime().truncatedTo(ChronoUnit.SECONDS).toString(), Instant.now()
                .truncatedTo(ChronoUnit.SECONDS).toString());
    }

    @Test
    public void testFine() throws Exception {
        logger.fine("testfine1");
        Message[] messages = logger.getUnreadEntries();
        Assert.assertEquals(messages.length, 1);
        Assert.assertEquals(messages[0].getType(), Type.FINE);
        Assert.assertEquals(messages[0].getText(), "testfine1");
        Assert.assertEquals(messages[0].getTime().truncatedTo(ChronoUnit.SECONDS).toString(), Instant.now()
                .truncatedTo(ChronoUnit.SECONDS).toString());
    }

    @Test
    public void testError() throws Exception {
        logger.error("testerror1");
        Message[] messages = logger.getUnreadEntries();
        Assert.assertEquals(messages.length, 1);
        Assert.assertEquals(messages[0].getType(), Type.ERROR);
        Assert.assertEquals(messages[0].getText(), "testerror1");
        Assert.assertEquals(messages[0].getTime().truncatedTo(ChronoUnit.SECONDS).toString(), Instant.now()
                .truncatedTo(ChronoUnit.SECONDS).toString());
    }

    @Test
    public void testError1() throws Exception {
        Throwable throwable = new Throwable("testerror1");
        logger.error(throwable);
        Message[] messages = logger.getUnreadEntries();
        Assert.assertEquals(messages.length, 1);
        Assert.assertEquals(messages[0].getType(), Type.ERROR);
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        Assert.assertEquals(messages[0].getText(), sw.toString());
        Assert.assertEquals(messages[0].getTime().truncatedTo(ChronoUnit.SECONDS).toString(), Instant.now()
                .truncatedTo(ChronoUnit.SECONDS).toString());
    }

    @Test
    public void testWarning() throws Exception {
        logger.warning("testwarning1");
        Message[] messages = logger.getUnreadEntries();
        Assert.assertEquals(messages.length, 1);
        Assert.assertEquals(messages[0].getType(), Type.WARNING);
        Assert.assertEquals(messages[0].getText(), "testwarning1");
        Assert.assertEquals(messages[0].getTime().truncatedTo(ChronoUnit.SECONDS).toString(), Instant.now()
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
