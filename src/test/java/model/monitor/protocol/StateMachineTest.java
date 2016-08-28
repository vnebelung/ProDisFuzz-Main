/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.monitor.protocol;

import model.monitor.message.TransmitMessage.Command;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StateMachineTest {

    private StateMachine stateMachine;

    @BeforeMethod
    public void setUp() throws Exception {
        stateMachine = new StateMachine();
    }

    @Test
    public void testUpdateWithNew() {
        try {
            stateMachine.updateWith(Command.CTF);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.CTT);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.RST);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.SCO);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.SCP);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.SWA);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.GWA);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.AYT);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
    }

    @Test
    public void testUpdateWithMonitorSet() {
        try {
            stateMachine.updateWith(Command.AYT);
            stateMachine.updateWith(Command.GCO);
            stateMachine.updateWith(Command.RST);
            stateMachine.updateWith(Command.AYT);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }

        try {
            stateMachine.updateWith(Command.SCP);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.AYT);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.CTF);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.CTT);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.SWA);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.GWA);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.SCO);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
    }

    @Test
    public void testUpdateWithConnectorSet() {
        try {
            stateMachine.updateWith(Command.AYT);
            stateMachine.updateWith(Command.SCO);
            stateMachine.updateWith(Command.RST);
            stateMachine.updateWith(Command.AYT);
            stateMachine.updateWith(Command.SCO);
            stateMachine.updateWith(Command.SCO);
            stateMachine.updateWith(Command.SCP);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }

        try {
            stateMachine.updateWith(Command.AYT);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.GCO);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.CTF);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.SWA);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.GWA);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.CTT);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
    }

    @Test
    public void testUpdateWithConnectorReady() {
        try {
            stateMachine.updateWith(Command.AYT);
            stateMachine.updateWith(Command.SCO);
            stateMachine.updateWith(Command.CTT);
            stateMachine.updateWith(Command.RST);
            stateMachine.updateWith(Command.AYT);
            stateMachine.updateWith(Command.SCO);
            stateMachine.updateWith(Command.CTT);
            stateMachine.updateWith(Command.SCP);
            stateMachine.updateWith(Command.CTT);
            stateMachine.updateWith(Command.SCO);
            stateMachine.updateWith(Command.CTT);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }

        try {
            stateMachine.updateWith(Command.AYT);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.CTF);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.CTT);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.GCO);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.GWA);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }

        try {
            stateMachine.updateWith(Command.SWA);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
    }

    @Test
    public void testUpdateWithWatcherSet() {
        try {
            stateMachine.updateWith(Command.AYT);
            stateMachine.updateWith(Command.SCO);
            stateMachine.updateWith(Command.CTT);
            stateMachine.updateWith(Command.SWA);
            stateMachine.updateWith(Command.RST);
            stateMachine.updateWith(Command.AYT);
            stateMachine.updateWith(Command.SCO);
            stateMachine.updateWith(Command.CTT);
            stateMachine.updateWith(Command.SWA);
            stateMachine.updateWith(Command.SWA);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }

        try {
            stateMachine.updateWith(Command.AYT);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.GCO);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.CTT);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.SCO);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.SCP);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.GWA);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.CTF);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
    }

    @Test
    public void testUpdateWithFuzzing() {
        try {
            stateMachine.updateWith(Command.AYT);
            stateMachine.updateWith(Command.SCO);
            stateMachine.updateWith(Command.CTT);
            stateMachine.updateWith(Command.SWA);
            stateMachine.updateWith(Command.CTF);
            stateMachine.updateWith(Command.CTF);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }

        try {
            stateMachine.updateWith(Command.AYT);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.GCO);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.CTT);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.SCO);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.SCP);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.SWA);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.GWA);
            Assert.fail();
        } catch (ProtocolStateException ignored) {
        }

        try {
            stateMachine.updateWith(Command.CTF);
            stateMachine.updateWith(Command.RST);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
    }

    @Test
    public void testIsAllowedAtCurrentState() {
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.AYT));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.CTF));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.CTT));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.GCO));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.SCO));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.SCP));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.GWA));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.SWA));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.RST));

        try {
            stateMachine.updateWith(Command.AYT);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.AYT));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.CTF));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.CTT));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.GCO));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.SCO));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.SCP));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.GWA));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.SWA));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.RST));

        try {
            stateMachine.updateWith(Command.SCO);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.AYT));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.CTF));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.CTT));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.GCO));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.SCO));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.SCP));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.GWA));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.SWA));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.RST));

        try {
            stateMachine.updateWith(Command.CTT);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.AYT));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.CTF));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.CTT));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.GCO));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.SCO));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.SCP));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.GWA));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.SWA));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.RST));

        try {
            stateMachine.updateWith(Command.SWA);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.AYT));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.CTF));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.CTT));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.GCO));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.SCO));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.SCP));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.GWA));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.SWA));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.RST));

        try {
            stateMachine.updateWith(Command.CTF);
        } catch (ProtocolStateException ignored) {
            Assert.fail();
        }
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.AYT));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.CTF));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.CTT));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.GCO));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.SCO));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.SCP));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.GWA));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.SWA));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.RST));
    }
}
