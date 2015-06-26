/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.logger;

import java.util.Date;

public class Message {

    private final String text;
    private final Date time;
    private final Type type;

    /**
     * Instantiates a new message entry.
     *
     * @param text the message text
     * @param type the message type
     */
    public Message(String text, Type type) {
        super();
        this.text = text;
        this.type = type;
        time = new Date();
    }

    /**
     * Gets the message text.
     *
     * @return the message text
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the time the entry was recorded.
     *
     * @return the message time
     */
    public Date getTime() {
        return new Date(time.getTime());
    }

    /**
     * Gets the type of the entry: INFO, ERROR, FINE, WARNING
     *
     * @return the log type
     */
    public Type getType() {
        return type;
    }

    public enum Type {INFO, ERROR, FINE, WARNING}

}
