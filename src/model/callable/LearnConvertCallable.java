/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:40.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.callable;

import model.ProtocolFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class LearnConvertCallable implements Callable<List<List<Byte>>> {
    private final List<ProtocolFile> files;

    /**
     * Instantiates a new callable responsible for converting the content of files to sequences.
     *
     * @param files the protocol files
     */
    public LearnConvertCallable(final List<ProtocolFile> files) {
        this.files = files;
    }

    @Override
    public List<List<Byte>> call() throws Exception {
        final List<List<Byte>> sequences = new ArrayList<>(files.size());
        for (final ProtocolFile eachFile : files) {
            if (eachFile.getContent().length > 0 && !Thread.currentThread().isInterrupted()) {
                sequences.add(new ArrayList<Byte>(eachFile.getContent().length));
                for (final byte eachByte : eachFile.getContent()) {
                    sequences.get(sequences.size() - 1).add(eachByte);
                }
            }
        }
        return Collections.unmodifiableList(sequences);
    }
}
