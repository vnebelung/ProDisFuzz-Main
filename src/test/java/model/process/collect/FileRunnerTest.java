/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:29.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.collect;

import org.testng.Assert;
import org.testng.annotations.Test;
import support.RunnerMonitor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("HardCodedStringLiteral")
public class FileRunnerTest {

    @Test
    public void testGetFileSelections() throws Exception {
        Map<String, Boolean> map1 = new HashMap<>(3);
        map1.put("a", true);
        map1.put("b", false);
        map1.put("c", false);
        FileRunner runner = new FileRunner(map1, "c", true);
        //noinspection TypeMayBeWeakened
        RunnerMonitor monitor = new RunnerMonitor();
        runner.addObserver(monitor);
        Map<String, Boolean> map2 = runner.getFileSelections();
        Assert.assertEquals(map1.size(), map2.size());
        for (Entry<String, Boolean> each : map2.entrySet()) {
            Assert.assertEquals(each.getValue(), map1.get(each.getKey()));
        }
        runner.run();
        map1.put("c", true);
        for (Entry<String, Boolean> each : map2.entrySet()) {
            Assert.assertEquals(each.getValue(), map1.get(each.getKey()));
        }
    }

    @Test
    public void testRun() throws Exception {
        Map<String, Boolean> map1 = new HashMap<>(3);
        map1.put("a", true);
        map1.put("b", false);
        map1.put("c", false);
        FileRunner runner = new FileRunner(map1, "c", true);
        RunnerMonitor monitor = new RunnerMonitor();
        runner.addObserver(monitor);
        runner.run();
        Assert.assertTrue(monitor.areAllStatesVisited());
    }
}
