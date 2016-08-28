/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.monitor;

public class MonitorTest {
    //    TODO: Remove comment
    //    private SimulatedMonitor simulatedMonitor;
    //
    //    @BeforeClass
    //    public void setUp() {
    //        simulatedMonitor = new SimulatedMonitor(10000);
    //        simulatedMonitor.start();
    //    }
    //
    //    @AfterClass
    //    public void tearDown() {
    //        simulatedMonitor.interrupt();
    //    }
    //
    //    @Test(priority = 1)
    //    public void testSetAddress() throws Exception {
    //        Monitor monitor = new Monitor();
    //
    //        monitor.setAddress("test", 1234);
    //        Assert.assertEquals(monitor.getAddressName(), "test");
    //        Assert.assertEquals(monitor.getAddressPort(), 1234);
    //    }
    //
    //    @Test(priority = 2)
    //    public void testConnect() throws Exception {
    //        Monitor monitor = new Monitor();
    //
    //        Assert.assertFalse(monitor.connect());
    //
    //        monitor.setAddress("", 0);
    //        Assert.assertFalse(monitor.connect());
    //
    //        monitor.setAddress("localhost", -2);
    //        Assert.assertFalse(monitor.connect());
    //
    //        monitor.setAddress("localhost", 10000);
    //        Assert.assertTrue(monitor.connect());
    //        monitor.disconnect();
    //    }
    //
    //    @Test(priority = 4)
    //    public void testSetParameters() throws Exception {
    //        Monitor monitor = new Monitor();
    //
    //        monitor.setAddress("localhost", 10000);
    //        monitor.connect();
    //        Map<String, String> map = new HashMap<>(1);
    //
    //        map.put("testkey1", "testvalue1");
    //        map.put("testkey2", "testvalue2");
    //        monitor.setParameters(map);
    //        Assert.assertEquals(monitor.getParameters("testkey1,testkey2,testkey3"), map);
    //
    //        map.put("testkey1", "");
    //        monitor.setParameters(map);
    //        Assert.assertEquals(monitor.getParameters("testkey"), new HashMap<String, String>(0));
    //
    //        monitor.disconnect();
    //    }

}
