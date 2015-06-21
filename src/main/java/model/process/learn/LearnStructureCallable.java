/*
 * This file is part of ProDisFuzz, modified on 03.04.14 19:54.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.protocol.ProtocolStructure;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class LearnStructureCallable implements Callable<ProtocolStructure> {

    private final List<Byte> sequence;

    /**
     * Instantiates a new callable responsible for creating protocol blocks out of the learned sequence.
     *
     * @param sequence the input sequence
     */
    public LearnStructureCallable(List<Byte> sequence) {
        super();
        this.sequence = new ArrayList<>(sequence);
    }

    @SuppressWarnings("ElementOnlyUsedFromTestCode")
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
