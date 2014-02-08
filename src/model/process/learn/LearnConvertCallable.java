/*
 * This file is part of ProDisFuzz, modified on 08.02.14 23:13.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.ProtocolFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

class LearnConvertCallable implements Callable<List<List<Byte>>> {
    private final List<ProtocolFile> files;

    /**
     * Instantiates a new callable responsible for converting the content of files to sequences.
     *
     * @param files the protocol files
     */
    public LearnConvertCallable(List<ProtocolFile> files) {
        this.files = files;
    }

    @Override
    public List<List<Byte>> call() throws Exception {
        List<List<Byte>> result = new ArrayList<>(files.size());
        for (ProtocolFile eachFile : files) {
            if (eachFile.getContent().length > 0 && !Thread.currentThread().isInterrupted()) {
                result.add(new ArrayList<Byte>(eachFile.getContent().length));
                for (byte eachByte : eachFile.getContent()) {
                    result.get(result.size() - 1).add(eachByte);
                }
            }
        }
        return Collections.unmodifiableList(result);
    }
}
