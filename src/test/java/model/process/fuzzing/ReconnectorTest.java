/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import support.SimulatedServer;
import support.SimulatedServer.Mode;

import java.io.IOException;
import java.net.InetSocketAddress;

@SuppressWarnings("HardCodedStringLiteral")
public class ReconnectorTest {

    private SimulatedServer simulatedServer = new SimulatedServer(Mode.STABLE);

    @BeforeClass
    public void setUp() {
        simulatedServer.start();
    }

    @AfterClass
    public void tearDown() {
        simulatedServer.interrupt();
    }

    @Test
    public void testCall1() throws IOException, InterruptedException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", simulatedServer.getPort());
        Reconnector reconnector = new Reconnector(inetSocketAddress, 50);
        Assert.assertTrue(reconnector.call());
    }

    @Test
    public void testCall2() throws IOException, InterruptedException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("dummy", simulatedServer.getPort());
        Reconnector reconnector = new Reconnector(inetSocketAddress, 50);
        Assert.assertFalse(reconnector.call());
    }

    @Test
    public void testCall3() throws IOException, InterruptedException {
        InetSocketAddress inetSocketAddress = new InetSocketAddress("localhost", simulatedServer.getPort() + 1);
        Reconnector reconnector = new Reconnector(inetSocketAddress, 50);
        Assert.assertFalse(reconnector.call());
    }
}
