/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.collect;

import model.process.AbstractRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class is the callable responsible for setting selection flags of (potential) protocol files.
 */
class FileRunner extends AbstractRunner {

    private Map<String, Boolean> fileSelections;
    private String fileName;
    private boolean selection;

    /**
     * Constructs the callable.
     *
     * @param fileSelections the map of file names with their selection flags
     * @param fileName       the file name
     * @param selection      true, if the file shall be selected
     */
    public FileRunner(Map<String, Boolean> fileSelections, String fileName, boolean selection) {
        super(1);
        this.fileSelections = new HashMap<>(fileSelections);
        this.fileName = fileName;
        this.selection = selection;
    }

    /**
     * Returns whether the file list contains an element with the given file name
     *
     * @return true, if the file list contains a file with the current saved name
     */
    private boolean isFileNameInList() {
        for (Entry<String, Boolean> each : fileSelections.entrySet()) {
            if (each.getKey().equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the file selection flags.
     *
     * @return the file selections
     */
    public Map<String, Boolean> getFileSelections() {
        return Collections.unmodifiableMap(fileSelections);
    }

    @Override
    public void run() {
        markStart();

        if (isFileNameInList() && selection != fileSelections.get(fileName)) {
            fileSelections.put(fileName, selection);
        }
        markFinish();
    }
}
