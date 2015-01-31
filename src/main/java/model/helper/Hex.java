/*
 * This file is part of ProDisFuzz, modified on 10.02.14 22:42.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.helper;

public abstract class Hex {

    private final static char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    /**
     * Returns the hex value for a given byte.
     *
     * @param b the byte
     * @return the hex value as a string
     */
    public static String byte2Hex(byte b) {
        return String.valueOf(HEX_CHARS[(b & 0xF0) >>> 4]) + String.valueOf(HEX_CHARS[b & 0x0F]);
    }

    /**
     * Returns the byte value for a given hex string.
     *
     * @param s the hex value as a string
     * @return the byte
     */
    public static byte hex2Byte(String s) {
        return (byte) Integer.parseInt(s, 16);
    }

}
