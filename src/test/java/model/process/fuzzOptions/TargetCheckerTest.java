/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import support.SimulatedServer;
import support.SimulatedServer.Mode;

@SuppressWarnings("HardCodedStringLiteral")
public class TargetCheckerTest {

    private SimulatedServer simulatedServer = new SimulatedServer(Mode.STABLE, 50);

    @BeforeClass
    public void setUp() {
        simulatedServer.start();
    }

    @AfterClass
    public void tearDown() {
        simulatedServer.interrupt();
    }

    @Test
    public void testCall() throws Exception {
        TargetChecker targetChecker = new TargetChecker("localhost", simulatedServer.getPort(), 75);
        Assert.assertTrue(targetChecker.call());

        targetChecker = new TargetChecker("localhost", simulatedServer.getPort() + 1, 75);
        Assert.assertFalse(targetChecker.call());

        targetChecker = new TargetChecker("", simulatedServer.getPort(), 75);
        Assert.assertFalse(targetChecker.call());

        targetChecker = new TargetChecker("dummy", simulatedServer.getPort(), 75);
        Assert.assertFalse(targetChecker.call());

        targetChecker = new TargetChecker("localhost", simulatedServer.getPort(), 25);
        Assert.assertTrue(targetChecker.call());
    }
}
