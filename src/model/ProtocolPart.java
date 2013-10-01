/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:25.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProtocolPart {

    private final Type type;
    private final List<Byte> bytes;
    private final int minLength;
    private final int maxLength;

    /**
     * Instantiates a new protocol part.
     *
     * @param type  the type of the protocol part
     * @param bytes the content in bytes
     */
    public ProtocolPart(final Type type, final List<Byte> bytes) {
        this.type = type;
        minLength = bytes.size();
        maxLength = bytes.size();
        this.bytes = new ArrayList<>(bytes);
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets the minimum length.
     *
     * @return the minLength
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Gets the maximum length.
     *
     * @return the maxLength
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    public List<Byte> getBytes() {
        return Collections.unmodifiableList(bytes);
    }

    public static enum Type {
        FIX, VAR
    }
}
