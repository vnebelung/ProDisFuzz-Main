/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:29.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import org.testng.Assert;
import org.testng.annotations.Test;
import support.RunnerMonitor;

public class IntervalRunnerTest {

    @Test
    public void testRun() throws Exception {
        RunnerMonitor monitor = new RunnerMonitor();
        IntervalRunner runner = new IntervalRunner(IntervalRunner.INTERVAL_MIN + 1);
        runner.addObserver(monitor);
        runner.run();
        Assert.assertTrue(monitor.areAllStatesVisited());
    }

    @Test
    public void testGetInterval() throws Exception {
        IntervalRunner runner = new IntervalRunner(IntervalRunner.INTERVAL_MIN + 1);
        runner.run();
        Assert.assertEquals(runner.getInterval(), IntervalRunner.INTERVAL_MIN + 1);

        runner = new IntervalRunner(IntervalRunner.INTERVAL_MIN - 1);
        runner.run();
        Assert.assertEquals(runner.getInterval(), IntervalRunner.INTERVAL_MIN);

        runner = new IntervalRunner(IntervalRunner.INTERVAL_MAX + 1);
        runner.run();
        Assert.assertEquals(runner.getInterval(), IntervalRunner.INTERVAL_MAX);
    }
}
