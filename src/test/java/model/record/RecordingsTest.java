/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.record;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Instant;

public class RecordingsTest {

    @Test
    public void testAddRecording() throws Exception {
        Recordings recordings = new Recordings();
        Assert.assertEquals(recordings.getSize(), 0);
        Instant instant = Instant.now();
        recordings.addRecording(new byte[]{0}, false, instant);
        Assert.assertFalse(recordings.getRecord(0).isCrash());
        Assert.assertEquals(recordings.getRecord(0).getSavedTime(), instant);
        recordings.addRecording(new byte[]{0}, true, instant);
        Assert.assertTrue(recordings.getCrashRecord(0).isCrash());
        Assert.assertEquals(recordings.getCrashRecord(0).getSavedTime(), instant);
    }

    @Test
    public void testClear() throws Exception {
        Recordings recordings = new Recordings();
        Assert.assertEquals(recordings.getSize(), 0);
        recordings.addRecording(new byte[]{0}, false, Instant.now());
        Assert.assertEquals(recordings.getSize(), 1);
        recordings.clear();
        Assert.assertEquals(recordings.getSize(), 0);
    }

    @Test
    public void testGetCrashSize() throws Exception {
        Recordings recordings = new Recordings();
        Assert.assertEquals(recordings.getCrashSize(), 0);
        recordings.addRecording(new byte[]{0}, false, Instant.now());
        Assert.assertEquals(recordings.getCrashSize(), 0);
        recordings.addRecording(new byte[]{0}, true, Instant.now());
        Assert.assertEquals(recordings.getCrashSize(), 1);
    }

    @Test
    public void testGetSize() throws Exception {
        Recordings recordings = new Recordings();
        Assert.assertEquals(recordings.getSize(), 0);
        recordings.addRecording(new byte[]{0}, true, Instant.now());
        Assert.assertEquals(recordings.getSize(), 1);
        recordings.addRecording(new byte[]{0}, false, Instant.now());
        Assert.assertEquals(recordings.getSize(), 2);
    }
}
