/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.logger;

import java.time.Instant;

/**
 * This class represents a single log entry.
 */
public class Entry {

    private final String text;
    private final Instant time;
    private final Type type;

    /**
     * Constructs a new message entry.
     *
     * @param text the message text
     * @param type the message type
     */
    public Entry(String text, Type type) {
        super();
        this.text = text;
        this.type = type;
        time = Instant.now();
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
     * @return the time the message was created
     */
    public Instant getTime() {
        return time;
    }

    /**
     * Gets the type of the entry: INFO, ERROR, FINE, WARNING.
     *
     * @return the log type
     */
    public Type getType() {
        return type;
    }

    public enum Type {INFO, ERROR, FINE, WARNING}

}
