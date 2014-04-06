/*
 * This file is part of ProDisFuzz, modified on 28.02.14 22:01.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model;

import model.helper.Constants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SavedDataFile {

    private final long time;
    private final boolean crash;
    private Path filePath;
    private Path outputPath;

    /**
     * Instantiates a new data file responsible for saving exchanged fuzzing data. The file will be located in the
     * system defined temp directory.
     *
     * @param content the saved data
     * @param crash   true if the data lead to a crash
     * @param time    the system time the data was created
     */
    public SavedDataFile(byte[] content, boolean crash, long time) {
        this.crash = crash;
        this.time = time;
        try {
            filePath = Files.createTempFile(Constants.FILE_PREFIX, null);
            // Write the bytes into the file
            Files.write(filePath, content);
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error(e);
        }

    }

    /**
     * Returns the file path located in the system's temp directory.
     *
     * @return the file path
     */
    public Path getFilePath() {
        return Paths.get(filePath.toString());
    }

    /**
     * Checks whether the recorded data led to a crash during fuzzing.
     *
     * @return true, if this data led to a crash
     */
    public boolean isCrash() {
        return crash;
    }

    /**
     * Returns the time the file was generated.
     *
     * @return the generation time in milliseconds
     */
    public long getSavedTime() {
        return time;
    }

    /**
     * Returns the output path, that is the path the temporary file will be copied to for permanent saving.
     *
     * @return the output path
     */
    public Path getOutputPath() {
        return Paths.get(outputPath.toString());
    }

    /**
     * Sets the output path, that is the path the temporary file will be copied to for permanent saving.
     *
     * @param p the output path
     */
    public void setOutputPath(Path p) {
        if (p != null) {
            outputPath = p.toAbsolutePath().normalize();
        }
    }

}
