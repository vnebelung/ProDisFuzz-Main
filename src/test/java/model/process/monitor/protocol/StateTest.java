/*
 * This file is part of ProDisFuzz, modified on 25.07.15 21:43.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.monitor.protocol;

import model.process.monitor.protocol.StateMachine.Command;
import model.process.monitor.protocol.StateMachine.StateType;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StateTest {

    private State state;

    @BeforeMethod
    public void setUp() throws Exception {
        state = new State();
        state.addTransition(Command.RST, StateType.NEW);
        state.addTransition(Command.CTD, StateType.CONFIGURED);
    }

    @Test
    public void testGetNextStateFor() throws IllegalArgumentException {
        Assert.assertEquals(state.getNextStateFor(Command.RST), StateType.NEW);
        Assert.assertEquals(state.getNextStateFor(Command.CTD), StateType.CONFIGURED);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void exceptionTestOne() throws IllegalArgumentException {
        state.getNextStateFor(Command.AYT);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void exceptionTestTwo() throws IllegalArgumentException {
        state.getNextStateFor(Command.SFP);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void exceptionTestThree() throws IllegalArgumentException {
        state.getNextStateFor(Command.GFP);
    }
}
