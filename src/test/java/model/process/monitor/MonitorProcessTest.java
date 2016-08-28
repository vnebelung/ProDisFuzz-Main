/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.monitor;

public class MonitorProcessTest {
    //    TODO: Remove comment
    //    private SimulatedMonitor simulatedMonitor;
    //
    //    @BeforeClass
    //    public void setUp() throws Exception {
    //        simulatedMonitor = new SimulatedMonitor(10040);
    //        simulatedMonitor.start();
    //    }
    //
    //    @AfterClass
    //    public void tearDown() throws Exception {
    //        simulatedMonitor.interrupt();
    //    }
    //
    //    @Test(priority = 1)
    //    public void testReset() throws Exception {
    //        MonitorProcess monitorProcess = new MonitorProcess();
    //        monitorProcess.setMonitor("localhost", 10040);
    //        Assert.assertTrue(monitorProcess.isMonitorReachable());
    //        monitorProcess.reset();
    //        Assert.assertFalse(monitorProcess.isMonitorReachable());
    //        Assert.assertEquals(monitorProcess.getMonitor().getAddressName(), "");
    //        Assert.assertEquals(monitorProcess.getMonitor().getAddressPort(), -1);
    //        monitorProcess.getMonitor().disconnect();
    //    }
    //
    //    @Test(priority = 2)
    //    public void testSetMonitor() throws Exception {
    //        MonitorProcess monitorProcess = new MonitorProcess();
    //        monitorProcess.setMonitor("localhost", 10040);
    //        Assert.assertEquals(monitorProcess.getMonitor().getAddressName(), "localhost");
    //        Assert.assertEquals(monitorProcess.getMonitor().getAddressPort(), 10040);
    //
    //        monitorProcess.setMonitor("example.net", 9);
    //        Assert.assertEquals(monitorProcess.getMonitor().getAddressName(), "example.net");
    //        Assert.assertEquals(monitorProcess.getMonitor().getAddressPort(), 9);
    //        monitorProcess.getMonitor().disconnect();
    //    }
    //
    //    @Test(priority = 3)
    //    public void testIsMonitorReachable() throws Exception {
    //        MonitorProcess monitorProcess = new MonitorProcess();
    //
    //        Assert.assertFalse(monitorProcess.isMonitorReachable());
    //
    //        monitorProcess.setMonitor("localhost", 10040);
    //        Assert.assertTrue(monitorProcess.isMonitorReachable());
    //    }
}
