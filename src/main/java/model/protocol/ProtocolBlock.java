/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.protocol;

import java.util.Arrays;

public class ProtocolBlock {

    private final Type type;
    private final Byte[] bytes;
    private final int minLength;
    private final int maxLength;

    /**
     * Instantiates a new protocol block that is responsible for defining a data block of the protocol structure.
     *
     * @param type  the type of the protocol block
     * @param bytes the content in bytes
     */
    public ProtocolBlock(Type type, Byte... bytes) {
        // TODO: Refactor to two constructors, one with primitive array, the other without byte array for null values
        this.type = type;
        minLength = bytes.length;
        maxLength = bytes.length;
        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }

    /**
     * Returns the type that indicates what data this block is containing.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns the minimum length of this block.
     *
     * @return the minLength
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Returns the maximum length of this block.
     *
     * @return the maxLength
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Returns the content of this block.
     *
     * @return the content in bytes
     */
    public Byte[] getBytes() {
        return bytes.clone();
    }

    public enum Type {FIX, VAR}
}
