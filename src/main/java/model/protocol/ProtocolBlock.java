/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.protocol;

import java.util.Arrays;

/**
 * This class is a protocol block, responsible for defining a data block of the protocol structure.
 */
public class ProtocolBlock {

    private final Type type;
    private final Byte[] bytes;
    private final int minLength;
    private final int maxLength;

    /**
     * Constructs a new protocol block.
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
        Byte[] result = new Byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i] == null ? null : Byte.valueOf(bytes[i]);
        }
        return result;
    }

    public enum Type {FIX, VAR}
}
