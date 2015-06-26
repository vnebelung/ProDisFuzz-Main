/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.connector;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("HardCodedStringLiteral")
public class MonitorTest {

    private SimulatedMonitor simulatedMonitor;

    @BeforeClass
    public void setUp() {
        simulatedMonitor = new SimulatedMonitor(10000);
        simulatedMonitor.start();
    }

    @AfterClass
    public void tearDown() {
        simulatedMonitor.interrupt();
    }

    @Test(priority = 1)
    public void testSetAddress() throws Exception {
        Monitor monitor = new Monitor();

        monitor.setAddress("test", 1234);
        Assert.assertEquals(monitor.getAddressName(), "test");
        Assert.assertEquals(monitor.getAddressPort(), 1234);
    }

    @Test(priority = 2)
    public void testConnect() throws Exception {
        Monitor monitor = new Monitor();

        Assert.assertFalse(monitor.connect());

        monitor.setAddress("", 0);
        Assert.assertFalse(monitor.connect());

        monitor.setAddress("localhost", -2);
        Assert.assertFalse(monitor.connect());

        monitor.setAddress("localhost", 10000);
        Assert.assertTrue(monitor.connect());
        monitor.disconnect();
    }

    @Test(priority = 4)
    public void testSetFuzzingParameters() throws Exception {
        Monitor monitor = new Monitor();

        monitor.setAddress("localhost", 10000);
        monitor.connect();
        Map<String, String> map = new HashMap<>(1);

        map.put("testkey", "testvalue");
        monitor.setFuzzingParameters(map);
        Assert.assertEquals(monitor.getFuzzingParameter("testkey"), "testvalue");

        map.put("testkey", "");
        monitor.setFuzzingParameters(map);
        Assert.assertEquals(monitor.getFuzzingParameter("testkey"), "");

        monitor.disconnect();
    }

}
