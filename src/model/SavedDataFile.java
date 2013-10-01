/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:27.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model;

import model.logger.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SavedDataFile {

    private final long time;
    private final boolean crash;
    private Path filePath;
    private Path outputPath;
    private static final String PREFIX = "prodisfuzz_";

    /**
     * Instantiates a new recorded data.
     *
     * @param content the saved data
     * @param crash   true if the data lead to a crash
     * @param time    the system time the data was created
     */
    public SavedDataFile(final byte[] content, final boolean crash, final long time) {
        this.crash = crash;
        this.time = time;
        try {
            filePath = Files.createTempFile(PREFIX, null);
            // Write the bytes into the file
            Files.write(filePath, content);
        } catch (IOException e) {
            Logger.getInstance().error(e);
        }

    }

    /**
     * Gets the file path.
     *
     * @return the file path
     */
    public Path getFilePath() {
        return Paths.get(filePath.toString());
    }

    /**
     * Tests whether the recorded data lead to a crash.
     *
     * @return true, if this data lead to a crash
     */
    public boolean isCrash() {
        return crash;
    }

    /**
     * Gets the time the file was recorded.
     *
     * @return the time in milliseconds
     */
    public long getSavedTime() {
        return time;
    }

    /**
     * Gets the output path.
     *
     * @return the output path
     */
    public Path getOutputPath() {
        return Paths.get(outputPath.toString());
    }

    /**
     * Sets the output path.
     *
     * @param outputPath the new output path
     */
    public void setOutputPath(final String outputPath) {
        this.outputPath = Paths.get(outputPath).toAbsolutePath().normalize();
    }

}
