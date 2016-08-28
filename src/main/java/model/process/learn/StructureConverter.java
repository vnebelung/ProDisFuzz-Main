/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.protocol.ProtocolStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class is the structure converter callable, responsible for creating protocol blocks out of a sequence.
 */
class StructureConverter implements Callable<ProtocolStructure> {

    private List<Byte> sequence;

    /**
     * Constructs the callable.
     *
     * @param sequence the input sequence
     */
    public StructureConverter(List<Byte> sequence) {
        this.sequence = new ArrayList<>(sequence);
    }

    @Override
    public ProtocolStructure call() {
        ProtocolStructure result = new ProtocolStructure();
        boolean var = sequence.get(0) == null;
        List<Byte> content = new ArrayList<>();
        for (Byte each : sequence) {
            // If the type is equal to the preceding type this byte belongs to the same protocol block
            if (var != (each == null)) {
                // If the types do not match the preceding block is written into the protocol block list and a
                // new content list is initialized
                result.addBlock(content);
                content = new ArrayList<>();
                var = each == null;
            }
            content.add(each);
        }
        // At the end the last (and not yet written) block is added to the protocol block list
        result.addBlock(content);
        return result;
    }
}
