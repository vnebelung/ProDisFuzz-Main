/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Observable;
import java.util.Observer;

@SuppressWarnings({"unused", "AnonymousInnerClassMayBeStatic"})
public class AbstractProcessTest implements Observer {
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private boolean isUpdated;

    @Test
    public void testSpreadUpdate() {
        AbstractProcess abstractProcess = new AbstractProcess() {
            @Override
            public void reset() {
            }
        };
        abstractProcess.addObserver(this);
        isUpdated = false;
        abstractProcess.spreadUpdate();
        Assert.assertTrue(isUpdated);
    }

    @Override
    public void update(Observable o, Object arg) {
        isUpdated = true;
    }
}
