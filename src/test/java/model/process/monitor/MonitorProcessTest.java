package model.process.monitor;

import model.connector.SimulatedMonitor;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MonitorProcessTest {
    private SimulatedMonitor simulatedMonitor;

    @BeforeClass
    public void setUp() throws Exception {
        simulatedMonitor = new SimulatedMonitor(10040);
        simulatedMonitor.start();
    }

    @AfterClass
    public void tearDown() throws Exception {
        simulatedMonitor.interrupt();
    }

    @Test(priority = 1)
    public void testReset() throws Exception {
        MonitorProcess monitorProcess = new MonitorProcess();
        monitorProcess.setMonitor("localhost", 10040);
        Assert.assertTrue(monitorProcess.isMonitorReachable());
        monitorProcess.reset();
        Assert.assertFalse(monitorProcess.isMonitorReachable());
        Assert.assertEquals(monitorProcess.getMonitor().getAddressName(), "");
        Assert.assertEquals(monitorProcess.getMonitor().getAddressPort(), -1);
        monitorProcess.getMonitor().disconnect();
    }

    @Test(priority = 2)
    public void testSetMonitor() throws Exception {
        MonitorProcess monitorProcess = new MonitorProcess();
        monitorProcess.setMonitor("localhost", 10040);
        Assert.assertEquals(monitorProcess.getMonitor().getAddressName(), "localhost");
        Assert.assertEquals(monitorProcess.getMonitor().getAddressPort(), 10040);

        monitorProcess.setMonitor("example.net", 9);
        Assert.assertEquals(monitorProcess.getMonitor().getAddressName(), "example.net");
        Assert.assertEquals(monitorProcess.getMonitor().getAddressPort(), 9);
        monitorProcess.getMonitor().disconnect();
    }

    @Test(priority = 3)
    public void testIsMonitorReachable() throws Exception {
        MonitorProcess monitorProcess = new MonitorProcess();

        Assert.assertFalse(monitorProcess.isMonitorReachable());

        monitorProcess.setMonitor("localhost", 10040);
        Assert.assertTrue(monitorProcess.isMonitorReachable());
    }
}
