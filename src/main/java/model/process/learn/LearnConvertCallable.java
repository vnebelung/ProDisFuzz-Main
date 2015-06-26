/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.ProtocolFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class LearnConvertCallable implements Callable<List<List<Byte>>> {
    private final ProtocolFile[] files;

    /**
     * Instantiates a new callable responsible for converting the content of files to sequences.
     *
     * @param files the protocol files
     */
    public LearnConvertCallable(ProtocolFile... files) {
        super();
        this.files = Arrays.copyOf(files, files.length);
    }

    @Override
    public List<List<Byte>> call() {
        List<List<Byte>> result = new ArrayList<>(files.length);
        for (ProtocolFile eachFile : files) {
            if ((eachFile.getContent().length > 0) && !Thread.currentThread().isInterrupted()) {
                result.add(new ArrayList<>(eachFile.getContent().length));
                for (byte eachByte : eachFile.getContent()) {
                    result.get(result.size() - 1).add(eachByte);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }
}
