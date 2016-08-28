/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.monitor.message;

import model.monitor.message.TransmitMessage.Command;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("HardCodedStringLiteral")
public class TransmitMessageTest {

    @Test
    public void testGetBytes() throws Exception {
        TransmitMessage transmitMessage =
                new TransmitMessage(Command.CTT, "abcdef123456öäü".getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals(transmitMessage.getBytes(), "CTT 18 abcdef123456öäü".getBytes(StandardCharsets.UTF_8));

        transmitMessage = new TransmitMessage(Command.CTT, "abcdef123456öäü");
        Assert.assertEquals(transmitMessage.getBytes(), "CTT 18 abcdef123456öäü".getBytes(StandardCharsets.UTF_8));

        transmitMessage = new TransmitMessage(Command.CTT);
        Assert.assertEquals(transmitMessage.getBytes(), "CTT 0 ".getBytes(StandardCharsets.UTF_8));

        Map<String, String> map = new HashMap<>(3);
        map.put("testkey1", "testvalue1");
        map.put("testkey2", "");
        map.put("testkey3", "testvalue3");
        transmitMessage = new TransmitMessage(Command.CTT, map);
        Assert.assertEquals(new String(transmitMessage.getBytes(), StandardCharsets.UTF_8),
                "CTT 49 " + "testkey3=testvalue3,testkey2=,testkey1=testvalue1");
    }

    @Test
    public void testGetCommand() throws Exception {
        TransmitMessage transmitMessage = new TransmitMessage(Command.RST);
        Assert.assertEquals(transmitMessage.getCommand(), Command.RST);
    }
}
