package model.process.fuzzing;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

public class FuzzingReconnectCallableTest {

    private SimulatedServer simulatedServer;

    @BeforeClass
    public void setUp() throws Exception {
        simulatedServer = new SimulatedServer();
        simulatedServer.start();
    }

    @Test
    public void testCall() throws IOException {
        //noinspection HardCodedStringLiteral
        FuzzingReconnectCallable fuzzingReconnectCallable = new FuzzingReconnectCallable(new InetSocketAddress
                ("localhost", 10020), 500);
        Assert.assertTrue(fuzzingReconnectCallable.call());
        Assert.assertFalse(fuzzingReconnectCallable.call());
    }

    @AfterClass
    public void tearDown() throws Exception {
        simulatedServer.interrupt();
    }

}
