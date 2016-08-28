/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.report;

import model.record.Recordings;
import model.util.Constants;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * This class is a callable responsible for finding a valid name for the report file and the record directory.
 */
class NameFinder implements Callable<String> {

    private Path path;
    private Recordings recordings;

    /**
     * Constructs the callable.
     *
     * @param path       the directory that will contain the file and the record directory
     * @param recordings the recordings
     */
    public NameFinder(Path path, Recordings recordings) {
        this.path = path;
        this.recordings = recordings;
    }

    /**
     * Finds an output name for the report and its record directory that is not already in use.
     *
     * @param path the original output directory
     * @return the name of the file and directory
     */
    private static String findOutputName(Path path) {
        String result;
        int postfix = 0;
        Path reportFile;
        Path recordsDirectory;
        do {
            //noinspection HardCodedStringLiteral
            result = (postfix == 0) ? "results" : ("results(" + postfix + ')');
            //noinspection HardCodedStringLiteral
            reportFile = path.resolve(result + ".html");
            //noinspection StringConcatenationMissingWhitespace
            recordsDirectory = path.resolve(result + Constants.RECORDINGS_DIRECTORY_POSTFIX);
            postfix++;
        } while (Files.exists(reportFile) || Files.isDirectory(recordsDirectory));
        return result;
    }

    @Override
    public String call() {
        String result = findOutputName(path);
        setOutputPaths(result);
        return result;
    }

    /**
     * Sets the output path of every record file based on the given output name.
     *
     * @param outputName the output file and directory name
     */
    private void setOutputPaths(String outputName) {
        // Set the file path for each detected crash
        int messageIteration = 0;
        int messageCount = 0;
        for (int i = 0; i < recordings.getSize(); i++) {
            //noinspection HardCodedStringLiteral,StringConcatenationMissingWhitespace
            Path outputPath = path.resolve(outputName + Constants.RECORDINGS_DIRECTORY_POSTFIX)
                    .resolve("record-" + messageIteration + '-' + messageCount + ".bytes");
            recordings.getRecord(i).setOutputPath(outputPath);
            if (recordings.getRecord(i).isCrash() == (messageCount == 0)) {
                messageIteration++;
                messageCount = 0;
            } else {
                messageCount = 1;
            }
        }
    }

}
