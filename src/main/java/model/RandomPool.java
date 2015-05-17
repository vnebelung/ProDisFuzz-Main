/*
 * This file is part of ProDisFuzz, modified on 08.02.14 22:39.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("DeserializableClassInSecureContext")
public class RandomPool extends Random {

    @SuppressWarnings("UnsecureRandomNumberGeneration")
    private static final RandomPool INSTANCE = new RandomPool();

    /**
     * Instantiates a new singleton pool for getting random data.
     */
    private RandomPool() {
        super();
    }

    /**
     * Returns the only instance.
     *
     * @return the singleton random source
     */
    public static RandomPool getInstance() {
        return INSTANCE;
    }

    /**
     * Generates an amount of random bytes within a range from 0 to the given length x 10000.
     *
     * @param length the maximum length of the bytes to generated
     * @return the random bytes
     */
    public List<Byte> nextBloatBytes(int length) {
        // Generate random bytes according to the maximum length of the given protocol block
        int fuzzDataLength = nextInt((length * 10000) + 1);
        byte[] bytes = new byte[fuzzDataLength];
        nextBytes(bytes);
        List<Byte> result = new ArrayList<>(bytes.length);
        for (byte each : bytes) {
            result.add(each);
        }
        return result;
    }
}
