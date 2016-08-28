/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.logger;

import model.Model;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ExceptionHandlerTest {

    @Test
    public void testUncaughtException() throws InterruptedException {
        Thread testThread = new MyThread();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        testThread.start();
        testThread.join();
        Entry[] entries = Model.INSTANCE.getLogger().getUnreadEntries();
        //noinspection HardCodedStringLiteral,HardcodedLineSeparator
        Assert.assertEquals(entries[entries.length - 1].getText(), "java.lang.RuntimeException: Test!\n" +
                "\tat model.logger.ExceptionHandlerTest$MyThread.run(ExceptionHandlerTest.java:32)\n");
    }

    private static class MyThread extends Thread {
        @SuppressWarnings("RefusedBequest")
        @Override
        public void run() {
            //noinspection ProhibitedExceptionThrown
            throw new RuntimeException("Test!");
        }
    }
}
