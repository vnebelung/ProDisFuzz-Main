/*
 * This file is part of ProDisFuzz, modified on 11.10.13 21:33.
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
     * Instantiates a new protocol part that is responsible for defining a block of the protocol structure.
     *
     * @param t the type of the protocol part
     * @param b the content in bytes
     */
    public ProtocolPart(final Type t, final List<Byte> b) {
        type = t;
        minLength = b.size();
        maxLength = b.size();
        bytes = new ArrayList<>(b);
    }

    /**
     * Returns the type that indicates what data this part is containing.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the minimum length of this part.
     *
     * @return the minLength
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Returns the maximum length o this part.
     *
     * @return the maxLength
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Returns the content of this part.
     *
     * @return the content in bytes
     */
    public List<Byte> getBytes() {
        return Collections.unmodifiableList(bytes);
    }

    public static enum Type {FIX, VAR}
}
