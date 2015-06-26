/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.util;

import model.Model;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;

public enum Keys {
    ;

    private static final byte[] UPDATE_PUBLIC_KEY = {48, -126, 1, 34, 48, 13, 6, 9, 42, -122, 72, -122, -9, 13, 1, 1,
            1, 5, 0, 3, -126, 1, 15, 0, 48, -126, 1, 10, 2, -126, 1, 1, 0, -25, 112, -25, 83, 50, -127, 85, -83, -53,
            -56, 58, -68, -103, -48, -14, -67, 40, -115, 92, 101, 115, -15, -63, 20, 1, 60, 38, 54, -15, -3, -57,
            -110, 79, -118, -128, 104, -111, 90, -28, 24, 71, -16, 1, -103, 43, -86, -43, -54, 59, -81, -64, -87,
            -115, -123, 4, 113, 112, 25, 116, 88, 113, -120, 1, 84, 109, 100, -112, -103, -21, -12, -81, -37, -79, 6,
            -109, -111, -13, 108, 69, -88, 115, -52, 8, -69, -119, -71, -72, -100, 122, 8, 57, -110, -35, 18, 73,
            -72, -95, -16, 123, -121, 120, 53, 6, -49, 70, 69, -82, -65, -33, -107, 84, 43, 41, 119, -21, 110, -93,
            127, 26, 71, 31, -68, 89, 15, 24, 116, -48, 10, 41, 35, 110, 49, 113, -14, -31, 32, -46, 119, 115, -81,
            -51, -75, 62, -35, -86, -115, 4, -23, 72, 87, -24, -1, -62, 7, -39, 109, -76, -41, -79, 76, 26, 123, 1,
            5, 35, -111, -108, 126, 118, -116, 86, -124, 60, 99, 57, 21, 99, -126, 119, -67, -49, 41, 85, -29, 53,
            -42, 7, 17, -107, -31, -93, 86, 79, -26, 105, 99, -22, -118, -5, 74, -6, -31, 5, 101, -123, -11, -25, 13,
            -80, -123, -44, 86, -105, -24, 22, 50, 5, 58, -100, 42, -59, 34, -57, 92, 112, 40, -125, 102, -33, -126,
            -106, 81, 115, -27, 14, 99, 57, -38, -102, -5, -26, -26, -35, 93, 37, -44, -64, 45, -102, -10, 67, -92,
            7, -37, 12, -11, 2, 3, 1, 0, 1};

    /**
     * Returns the public key of the update information stored on prodisfuzz.net.
     *
     * @return the public key or null in case of an error
     */
    public static PublicKey getUpdatePublicKey() {
        KeySpec spec = new X509EncodedKeySpec(UPDATE_PUBLIC_KEY);
        try {
            //noinspection HardCodedStringLiteral
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            // Should not happen
            Model.INSTANCE.getLogger().error(e);
            return null;
        }
    }
}
