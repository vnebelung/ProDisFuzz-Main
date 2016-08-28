/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.updater;

import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("ClassIndependentOfModule")
public class UpdateCheckTest {

    @Test
    public void testHasUpdate() throws Exception {
        UpdateCheck updateCheck = new UpdateCheck();
        Assert.assertFalse(updateCheck.hasUpdate());
    }

    @Test
    public void testGetReleaseInformation() throws Exception {
        UpdateCheck updateCheck = new UpdateCheck();
        Assert.assertNull(updateCheck.getReleaseInformation());

        updateCheck.hasUpdate();
        Assert.assertTrue(updateCheck.getReleaseInformation().length > 0);
    }

}
