/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.monitor.protocol;

import model.monitor.message.TransmitMessage.Command;
import model.monitor.protocol.StateMachine.StateType;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StateTest {

    private State state;

    @BeforeMethod
    public void setUp() {
        state = new State();
        state.addTransition(Command.RST, StateType.NEW);
        state.addTransition(Command.CTF, StateType.CONNECTOR_READY);
    }

    @Test
    public void testGetNextStateFor() {
        Assert.assertEquals(state.getNextStateFor(Command.RST), StateType.NEW);
        Assert.assertEquals(state.getNextStateFor(Command.CTF), StateType.CONNECTOR_READY);
        Assert.assertNull(state.getNextStateFor(Command.CTT));
        Assert.assertNull(state.getNextStateFor(Command.AYT));
        Assert.assertNull(state.getNextStateFor(Command.SCO));
        Assert.assertNull(state.getNextStateFor(Command.SCP));
        Assert.assertNull(state.getNextStateFor(Command.SWA));
        Assert.assertNull(state.getNextStateFor(Command.GWA));
    }
}
