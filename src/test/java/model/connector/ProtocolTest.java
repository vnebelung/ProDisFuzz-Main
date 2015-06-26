/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.connector;

import model.util.Constants;
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
