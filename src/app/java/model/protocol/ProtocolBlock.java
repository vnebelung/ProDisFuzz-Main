/*
 * This file is part of ProDisFuzz, modified on 31.03.14 18:37.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.protocol;

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
    public ProtocolBlock(Type type, Byte[] bytes) {
        this.type = type;
        minLength = bytes.length;
        maxLength = bytes.length;
        this.bytes = bytes;
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

    public static enum Type {FIX, VAR}
}
