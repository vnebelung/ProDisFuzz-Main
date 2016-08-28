/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.monitor.message;

import model.monitor.message.ReceiveMessage.Command;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;

@SuppressWarnings("HardCodedStringLiteral")
public class ReceiveMessageTest {

    @Test
    public void testGetCommand() throws Exception {
        ReceiveMessage receiveMessage = new ReceiveMessage(Command.ERR, "abc123".getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals(receiveMessage.getCommand(), Command.ERR);
    }

    @Test
    public void testGetBody() throws Exception {
        ReceiveMessage receiveMessage = new ReceiveMessage(Command.ERR, "abc123öäü".getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals(receiveMessage.getBody(), "abc123öäü".getBytes(StandardCharsets.UTF_8));
    }
}
