/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.updater;

import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("HardCodedStringLiteral")
public class ReleaseInformationTest {

    @Test
    public void testGetNumber() throws Exception {
        ReleaseInformation releaseInformation = new ReleaseInformation(12, "test", "date", "requ", "1", "2");
        Assert.assertEquals(releaseInformation.getNumber(), 12);
    }

    @Test
    public void testGetName() throws Exception {
        ReleaseInformation releaseInformation = new ReleaseInformation(12, "test", "date", "requ", "1", "2");
        Assert.assertEquals(releaseInformation.getName(), "test");
    }

    @Test
    public void testGetDate() throws Exception {
        ReleaseInformation releaseInformation = new ReleaseInformation(12, "test", "date", "requ", "1", "2");
        Assert.assertEquals(releaseInformation.getDate(), "date");
    }

    @Test
    public void testGetRequirements() throws Exception {
        ReleaseInformation releaseInformation = new ReleaseInformation(12, "test", "date", "requ", "1", "2");
        Assert.assertEquals(releaseInformation.getRequirements(), "requ");
    }

    @Test
    public void testGetInformation() throws Exception {
        ReleaseInformation releaseInformation = new ReleaseInformation(12, "test", "date", "requ", "1", "2");
        Assert.assertEquals(releaseInformation.getInformation(), new String[]{"1", "2"});
    }
}
