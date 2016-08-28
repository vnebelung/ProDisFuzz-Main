/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.collect;

import model.Model;
import model.protocol.ProtocolFile;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class is the callable responsible for reading protocol files from a directory.
 */
class FileReader implements Callable<List<ProtocolFile>> {

    private Path directory;

    /**
     * Constructs a new callable.
     *
     * @param directory the directory path that contains the protocol files.
     */
    public FileReader(Path directory) {
        this.directory = directory;
    }

    @Override
    public List<ProtocolFile> call() {
        List<ProtocolFile> result = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path each : stream) {
                // Sort out any files that are not an actual file, are not readable or are hidden
                if (!Files.isRegularFile(each) || !Files.isReadable(each) || Files.isHidden(each)) {
                    continue;
                }
                ProtocolFile file = new ProtocolFile(each);
                result.add(file);
            }
            Collections.sort(result);
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error(e);
        }
        return result;
    }
}
