/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import model.Model;
import model.process.AbstractRunner;
import model.process.fuzzoptions.Process.RecordingMethod;

/**
 * This class is the recording method runnable, responsible for setting the option to either record all communication
 * exchanged between ProDisFuzz and the target, no matter whether the data triggers a crash, or only critical
 * communication, that is only data that leads to a crash in the target program.
 */
class RecordingMethodRunner extends AbstractRunner {

    private RecordingMethod recordingMethod;

    /**
     * Constructs a new runner.
     *
     * @param recordingMethod the recording method
     */
    protected RecordingMethodRunner(RecordingMethod recordingMethod) {
        super(1);
        this.recordingMethod = recordingMethod;
    }

    /**
     * Returns the recording method.
     *
     * @return the recording method
     */
    public RecordingMethod getRecordingMethod() {
        return recordingMethod;
    }

    @Override
    public void run() {
        markStart();

        // Start work unit
        Model.INSTANCE.getLogger().info("Recording " + recordingMethod + " communication");
        markFinish();
    }


}
