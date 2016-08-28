/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.monitor.message;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class represents a message that is transmitted to the monitor component.
 */
public class TransmitMessage {

    private Command command;
    private byte[] body;

    /**
     * Constructs a new message that represents a message sent from the monitor to the main component.
     *
     * @param command the message's command
     * @param body    the message's body
     */
    public TransmitMessage(Command command, byte... body) {
        this.command = command;
        this.body = body;
    }

    /**
     * Constructs a new message that represents a message sent from the monitor to the main component.
     *
     * @param command the message's command
     * @param body    the message's body
     */
    public TransmitMessage(Command command, String body) {
        this.command = command;
        this.body = body.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Constructs a new message that represents a message sent from the monitor to the mai component. This message has
     * an empty body.
     *
     * @param command the message's command
     */
    public TransmitMessage(Command command) {
        this.command = command;
        //noinspection ZeroLengthArrayAllocation
        body = new byte[0];
    }

    /**
     * Constructs a new message that represents a message sent from the monitor to the mai component.
     *
     * @param command the message's command
     * @param body    the message's body
     */
    public TransmitMessage(Command command, Map<String, String> body) {
        this.command = command;
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, String> each : body.entrySet()) {
            stringBuilder.append(each.getKey()).append('=').append(each.getValue()).append(',');
        }
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        this.body = stringBuilder.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Returns the raw bytes of this message. The message has the following format: "aaa bb cccc…" with aaa = three
     * character command, bb = length of the cccc… block, and cccc… = body of variable length. The body can be empty
     *
     * @return the message in bytes
     */
    public byte[] getBytes() {
        byte[] header = (command.toString() + ' ' + body.length + ' ').getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[header.length + body.length];
        System.arraycopy(header, 0, result, 0, header.length);
        System.arraycopy(body, 0, result, header.length, body.length);
        return result;
    }

    /**
     * Returns the message's command.
     *
     * @return the message's command.
     */
    public Command getCommand() {
        return command;
    }

    public enum Command {AYT, SCO, SCP, CTT, SWA, CTF, RST, GCO, GWA}
}
