/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:29.
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
import support.RunnerMonitor;
import support.SimulatedServer;
import support.SimulatedServer.Mode;

@SuppressWarnings("HardCodedStringLiteral")
public class TargetRunnerTest {

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
    public void testRun() throws Exception {
        TargetRunner runner = new TargetRunner("localhost", simulatedServer.getPort(), 75);
        RunnerMonitor monitor = new RunnerMonitor();
        runner.addObserver(monitor);
        runner.run();
        Assert.assertTrue(monitor.areAllStatesVisited());
    }

    @Test
    public void testGetTarget() throws Exception {
        TargetRunner runner = new TargetRunner("localhost", simulatedServer.getPort(), 75);
        runner.run();
        Assert.assertNotNull(runner.getTarget());

        runner = new TargetRunner("localhost", simulatedServer.getPort() + 1, 75);
        runner.run();
        Assert.assertNull(runner.getTarget());

        runner = new TargetRunner("", simulatedServer.getPort(), 75);
        runner.run();
        Assert.assertNull(runner.getTarget());

        runner = new TargetRunner("dummy", simulatedServer.getPort(), 75);
        runner.run();
        Assert.assertNull(runner.getTarget());

        runner = new TargetRunner("localhost", simulatedServer.getPort(), 25);
        runner.run();
        Assert.assertNotNull(runner.getTarget());
    }
}
