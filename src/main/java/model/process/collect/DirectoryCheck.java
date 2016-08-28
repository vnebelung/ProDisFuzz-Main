/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.collect;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * This class is a callable responsible for checking whether a directory is existing on the file system and readable, so
 * that the protocol files in this directory can be read.
 */
class DirectoryCheck implements Callable<Path> {

    private String directory;

    /**
     * Constructs a new callable.
     *
     * @param directory the directory path that contains the protocol files or null if the path is not valid.
     */
    public DirectoryCheck(String directory) {
        this.directory = directory;
    }

    @Override
    public Path call() {
        if (directory.isEmpty()) {
            //noinspection ReturnOfNull
            return null;
        }
        Path path = Paths.get(directory).toAbsolutePath().normalize();
        if (!Files.isDirectory(path)) {
            //noinspection ReturnOfNull
            return null;
        }
        if (!Files.isReadable(path)) {
            //noinspection ReturnOfNull
            return null;
        }
        return path;
    }
}
