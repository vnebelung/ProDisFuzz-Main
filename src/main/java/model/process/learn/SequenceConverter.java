/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.protocol.ProtocolFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class is the sequence converter callable, responsible for converting the content of a file to byte sequences
 * that can be used to learn the protocol structure.
 */
class SequenceConverter implements Callable<List<Byte>> {

    private ProtocolFile file;

    /**
     * Constructs a new callable.
     *
     * @param file the protocol file
     */
    public SequenceConverter(ProtocolFile file) {
        this.file = file;
    }

    @Override
    public List<Byte> call() {
        List<Byte> result = new ArrayList<>();
        for (byte each : file.getContent()) {
            result.add(each);
        }
        return result;
    }
}
