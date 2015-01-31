package model.connector;

import model.helper.Constants;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ProtocolTest {

    private SimulatedMonitor simulatedMonitor;

    @BeforeClass
    public void setUp() throws Exception {
        simulatedMonitor = new SimulatedMonitor(10001);
        simulatedMonitor.start();
    }

    @AfterClass
    public void tearDown() throws Exception {
        simulatedMonitor.interrupt();
    }

    @Test(priority = 1)
    public void testAyt() throws Exception {
        Socket socket = new Socket("localhost", 10001);
        Protocol protocol = new Protocol(socket.getInputStream(), socket.getOutputStream());
        Assert.assertEquals(protocol.ayt(), Constants.RELEASE_NUMBER);
        socket.close();
    }

    @Test(priority = 2)
    public void testSfp() throws Exception {
        Socket socket = new Socket("localhost", 10001);
        Protocol protocol = new Protocol(socket.getInputStream(), socket.getOutputStream());
        protocol.ayt();
        Map<String, String> parameter = new HashMap<>(1);
        parameter.put("testkey", "testvalue");
        Assert.assertTrue(protocol.sfp(parameter));
        socket.close();
    }

    @Test(priority = 4)
    public void testCtd() throws Exception {
        Socket socket = new Socket("localhost", 10001);
        Protocol protocol = new Protocol(socket.getInputStream(), socket.getOutputStream());
        protocol.ayt();
        Map<String, String> parameter = new HashMap<>(1);
        parameter.put("testkey", "testvalue");
        protocol.sfp(parameter);
        Assert.assertFalse(protocol.ctd(new byte[]{'a'}));
        Assert.assertTrue(protocol.ctd(new byte[]{'a', 'a'}));
        socket.close();
    }
}
