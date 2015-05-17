package model.connector;

import model.utilities.Constants;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"HardCodedStringLiteral", "resource", "SocketOpenedButNotSafelyClosed"})
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

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Test(priority = 1)
    public void testAyt() throws IOException {
        Socket socket = new Socket("localhost", 10001);
        Protocol protocol = new Protocol(socket.getInputStream(), socket.getOutputStream());
        Assert.assertEquals(protocol.ayt(), Constants.RELEASE_NUMBER);
        socket.close();
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Test(priority = 2)
    public void testSfp() throws IOException {
        Socket socket = new Socket("localhost", 10001);
        Protocol protocol = new Protocol(socket.getInputStream(), socket.getOutputStream());
        protocol.ayt();
        Map<String, String> parameter = new HashMap<>(1);
        parameter.put("testkey", "testvalue");
        Assert.assertTrue(protocol.sfp(parameter));
        socket.close();
    }

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Test(priority = 4)
    public void testCtd() throws IOException {
        Socket socket = new Socket("localhost", 10001);
        Protocol protocol = new Protocol(socket.getInputStream(), socket.getOutputStream());
        protocol.ayt();
        Map<String, String> parameter = new HashMap<>(1);
        parameter.put("testkey", "testvalue");
        protocol.sfp(parameter);
        Assert.assertFalse(protocol.ctd((byte) 'a'));
        Assert.assertTrue(protocol.ctd((byte) 'a', (byte) 'a'));
        socket.close();
    }
}
