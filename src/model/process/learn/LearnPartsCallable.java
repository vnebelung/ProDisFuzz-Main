/*
 * This file is part of ProDisFuzz, modified on 08.02.14 23:13.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.ProtocolPart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

class LearnPartsCallable implements Callable<List<ProtocolPart>> {

    private final List<Byte> sequence;

    /**
     * Instantiates a new callable responsible for creating protocol parts out of the learned sequence.
     *
     * @param sequence the input sequence
     */
    public LearnPartsCallable(List<Byte> sequence) {
        this.sequence = sequence;
    }

    @Override
    public List<ProtocolPart> call() throws Exception {
        List<ProtocolPart> result = new ArrayList<>();
        ProtocolPart.Type type = getType(sequence.get(0));
        List<Byte> content = new ArrayList<>();
        for (Byte each : sequence) {
            // If the type is equal to the preceding type this byte belongs to the same protocol part
            if (type != getType(each)) {
                // If the types do not match the preceding part is written into the protocol part list and a
                // new content list is initialized
                result.add(new ProtocolPart(type, content));
                content = new ArrayList<>();
                type = getType(each);
            }
            content.add(each);
        }
        // At the end the last (and not yet written) part is added to the protocol part list
        result.add(new ProtocolPart(type, content));
        return Collections.unmodifiableList(result);
    }

    /**
     * Returns the type of a given byte based on its value: null is considered as a variable part,
     * other values are considered as a fix part.
     *
     * @param b a single byte
     * @return the type of the byte
     */
    private ProtocolPart.Type getType(Byte b) {
        return b == null ? ProtocolPart.Type.VAR : ProtocolPart.Type.FIX;
    }

}
