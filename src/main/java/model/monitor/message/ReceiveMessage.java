/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.monitor.message;

/**
 * This class represents a message that is received from the monitor.
 */
public class ReceiveMessage {

    private Command command;
    private byte[] body;

    /**
     * Constructs a message received from the monitor component.
     *
     * @param command the message's command
     * @param body    the message's body
     */
    public ReceiveMessage(Command command, byte... body) {
        this.command = command;
        this.body = body.clone();
    }

    /**
     * Returns the message's command
     *
     * @return the message's command
     */
    public Command getCommand() {
        return command;
    }

    /**
     * Returns the message's body
     *
     * @return the message's body
     */
    public byte[] getBody() {
        return body.clone();
    }

    public enum Command {ROK, ERR}

}
