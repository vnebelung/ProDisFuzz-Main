/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.record;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Recordings {

    private List<RecordedFile> recordedFiles;
    private List<RecordedFile> crashRecordedFiles;

    /**
     * Creates new recordings responsible for handling recorded data files.
     */
    public Recordings() {
        recordedFiles = new ArrayList<>();
        crashRecordedFiles = new ArrayList<>();
    }

    /**
     * Adds a new data file to the list of recordings.
     *
     * @param content the sent message
     * @param crash   true, if the message has caused a crash of the target
     * @param time    the time in milliseconds the crash occurred
     */
    public void addRecording(byte[] content, boolean crash, Instant time) {
        RecordedFile recordedFile = new RecordedFile(content, crash, time);
        recordedFiles.add(recordedFile);
        if (crash) {
            crashRecordedFiles.add(recordedFile);
        }
    }

    /**
     * Returns the number of records.
     *
     * @return the number of records
     */
    public int getSize() {
        return recordedFiles.size();
    }

    /**
     * Returns the record at the given index.
     *
     * @param index the index
     * @return the record
     */
    public RecordedFile getRecord(int index) {
        return recordedFiles.get(index);
    }

    /**
     * Deletes all recorded data files.
     */
    public void clear() {
        recordedFiles.forEach(RecordedFile::delete);
        recordedFiles.clear();
        crashRecordedFiles.clear();
    }

    /**
     * Returns the number of records that had caused a crash of the target.
     *
     * @return the number of crash-causing records
     */
    public int getCrashSize() {
        int result = 0;
        for (RecordedFile each : recordedFiles) {
            if (each.isCrash()) {
                result++;
            }
        }
        return result;
    }

    /**
     * Returns the crash-causing record with the given index.
     *
     * @param index the index
     * @return the crash-causing record
     */
    public RecordedFile getCrashRecord(int index) {
        return crashRecordedFiles.get(index);
    }
}
