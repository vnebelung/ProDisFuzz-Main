/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.logger;

import model.logger.Entry.Type;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SuppressWarnings("HardCodedStringLiteral")
public class EntryTest {

    @Test
    public void testGetText() throws Exception {
        Entry entry = new Entry("abc123", Type.ERROR);
        Assert.assertEquals(entry.getText(), "abc123");
    }

    @Test
    public void testGetTime() throws Exception {
        Instant now = Instant.now();
        Entry entry = new Entry("abc123", Type.FINE);
        Assert.assertEquals(entry.getTime().truncatedTo(ChronoUnit.SECONDS).toString(),
                now.truncatedTo(ChronoUnit.SECONDS).toString());
    }

    @Test
    public void testGetType() throws Exception {
        Entry entry = new Entry("abc123", Type.FINE);
        Assert.assertEquals(entry.getType(), Type.FINE);

        entry = new Entry("abc123", Type.ERROR);
        Assert.assertEquals(entry.getType(), Type.ERROR);

        entry = new Entry("abc123", Type.WARNING);
        Assert.assertEquals(entry.getType(), Type.WARNING);

        entry = new Entry("abc123", Type.INFO);
        Assert.assertEquals(entry.getType(), Type.INFO);
    }
}
