/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Hex {
    ;

    private static final Pattern HEXBIN = Pattern.compile("^([0-9a-f]{2})*$");

    @SuppressWarnings("HardCodedStringLiteral")
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    /**
     * Returns the hex value(s) for a given byte or the given bytes.
     *
     * @param bytes the input byte(s)
     * @return the hex value(s) as a string
     */
    public static String byte2HexBin(byte... bytes) {
        StringBuilder stringBuilder = new StringBuilder(bytes.length * 2);
        for (byte each : bytes) {
            stringBuilder.append(HEX_CHARS[(each & 0xF0) >>> 4]);
            stringBuilder.append(HEX_CHARS[each & 0x0F]);
        }
        return stringBuilder.toString();
    }

    /**
     * Returns the byte values for a given hex string.
     *
     * @param hexbin the hex value(s) as a string
     * @return the byte(s) in an array or an empty array if the input is not in hex binary form
     */
    public static byte[] hexBin2Byte(String hexbin) {
        Matcher matcher = HEXBIN.matcher(hexbin);
        if (!matcher.matches()) {
            //noinspection ZeroLengthArrayAllocation
            return new byte[0];
        }
        byte[] result = new byte[hexbin.length() / 2];
        for (int i = 0; i < hexbin.length() / 2; i++) {
            //noinspection NumericCastThatLosesPrecision
            result[i] = (byte) Integer.parseInt(hexbin.substring(i * 2, i * 2 + 2), 16);
        }
        return result;
    }
}
