/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

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
