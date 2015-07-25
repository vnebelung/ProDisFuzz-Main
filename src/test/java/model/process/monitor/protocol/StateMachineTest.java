/*
 * This file is part of ProDisFuzz, modified on 25.07.15 21:43.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.monitor.protocol;

import model.process.monitor.protocol.StateMachine.Command;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StateMachineTest {

    private StateMachine stateMachine;

    @BeforeMethod
    public void setUp() throws Exception {
        stateMachine = new StateMachine();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateWithOne() throws IllegalStateException {
        stateMachine.updateWith(Command.SFP);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateWithTwo() throws IllegalStateException {
        stateMachine.updateWith(Command.GFP);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateWithThree() throws IllegalStateException {
        stateMachine.updateWith(Command.CTD);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateWithFour() throws IllegalStateException {
        stateMachine.updateWith(Command.AYT);
        stateMachine.updateWith(Command.AYT);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateWithFive() throws IllegalStateException {
        stateMachine.updateWith(Command.GFP);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateWithSix() throws IllegalStateException {
        stateMachine.updateWith(Command.CTD);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateWithSeven() throws IllegalStateException {
        stateMachine.updateWith(Command.SFP);
        stateMachine.updateWith(Command.AYT);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateWithEight() throws IllegalStateException {
        stateMachine.updateWith(Command.CTD);
        stateMachine.updateWith(Command.AYT);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateWithNine() throws IllegalStateException {
        stateMachine.updateWith(Command.SFP);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateWithTen() throws IllegalStateException {
        stateMachine.updateWith(Command.GFP);
    }

    @Test
    public void testIsAllowedAtCurrentState() throws IllegalStateException {
        stateMachine.updateWith(Command.RST);
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.RST));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.AYT));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.SFP));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.GFP));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.CTD));

        stateMachine.updateWith(Command.AYT);
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.RST));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.AYT));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.SFP));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.GFP));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.CTD));

        stateMachine.updateWith(Command.SFP);
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.RST));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.AYT));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.SFP));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.GFP));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.CTD));

        stateMachine.updateWith(Command.CTD);
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.RST));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.AYT));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.SFP));
        Assert.assertFalse(stateMachine.isAllowedAtCurrentState(Command.GFP));
        Assert.assertTrue(stateMachine.isAllowedAtCurrentState(Command.CTD));
    }
}
