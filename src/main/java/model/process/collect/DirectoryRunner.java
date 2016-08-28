/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.collect;

import model.Model;
import model.process.AbstractRunner;
import model.protocol.ProtocolFile;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This class is the runnable responsible for handling the reading all protocol files of a directory in a separate
 * thread.
 */
class DirectoryRunner extends AbstractRunner {

    private String directory;
    private List<ProtocolFile> protocolFiles;
    private boolean isDirectoryValid;

    /**
     * Constructs a new directory reader.
     *
     * @param directory the input directory that contains the files to be read.
     */
    public DirectoryRunner(String directory) {
        super(2);
        this.directory = directory;
        protocolFiles = new ArrayList<>(0);
        isDirectoryValid = false;
    }

    /**
     * Returns the protocol files that were read from the directory.
     *
     * @return the protocol files
     */
    public List<ProtocolFile> getProtocolFiles() {
        return Collections.unmodifiableList(protocolFiles);
    }

    /**
     * Returns whether the directory is valid or not.
     *
     * @return true, if the directory is valid
     */
    public boolean isDirectoryValid() {
        return isDirectoryValid;
    }

    @Override
    public void run() {
        try {
            markStart();

            isDirectoryValid = false;
            // Start new work unit
            //noinspection TypeMayBeWeakened
            DirectoryCheck directoryCheck = new DirectoryCheck(directory);
            Future<Path> directoryCheckFuture = submitToThreadPool(directoryCheck);
            Path newDirectory = directoryCheckFuture.get();
            if (newDirectory == null) {
                isDirectoryValid = false;
                Model.INSTANCE.getLogger().error("Directory for collecting protocol files invalid");
                markCancel();
                return;
            }
            isDirectoryValid = true;
            Model.INSTANCE.getLogger().info("Reading directory '" + newDirectory + '\'');
            markProgress();

            // Start new work unit
            Callable<List<ProtocolFile>> fileReader = new FileReader(newDirectory);
            Future<List<ProtocolFile>> fileReaderFuture = submitToThreadPool(fileReader);
            protocolFiles = fileReaderFuture.get();
            markFinish();

        } catch (InterruptedException ignored) {
            Model.INSTANCE.getLogger().info("Reading directory cancelled");
            markCancel();
        } catch (ExecutionException e) {
            // Should not happen
            Model.INSTANCE.getLogger().error(e);
        }
    }
}
