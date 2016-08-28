/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.report;

import model.Model;
import model.process.AbstractProcess;
import model.process.AbstractRunner;
import model.process.AbstractRunner.ExternalState;
import model.process.fuzzoptions.Process.RecordingMethod;
import model.protocol.InjectedProtocolStructure;
import model.record.Recordings;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Observable;

/**
 * This class is the report process, responsible for generating the final report with all fuzzing results.
 */
public class Process extends AbstractProcess {

    @SuppressWarnings("HardCodedStringLiteral")
    public static final String NAMESPACE = "http://www.w3.org/1999/xhtml";

    /**
     * Starts the process for generating and saving the report.
     *
     * @param recordings                the communication recordings
     * @param duration                  the fuzzing duration
     * @param target                    the tested target
     * @param interval                  the fuzzing interval
     * @param injectedProtocolStructure the injected protocol structure used for fuzzing
     * @param workProgress              the processed fuzzign iterations
     * @param workTotal                 the total amount of fuzzing iterations
     * @param recordingMethod           the method used for recordings
     * @param timeout                   the timeout of a successful crash
     * @param outputDirectory           the directory the report will be saved to
     */
    public void save(Recordings recordings, Duration duration, InetSocketAddress target, int interval,
                     InjectedProtocolStructure injectedProtocolStructure, int workProgress, int workTotal,
                     RecordingMethod recordingMethod, int timeout, Path outputDirectory) {
        AbstractRunner runner =
                new Runner(recordings, duration, target, interval, injectedProtocolStructure, workProgress, workTotal,
                        recordingMethod, timeout, outputDirectory);
        runner.addObserver(this);
        submitToThreadPool(runner);
        Model.INSTANCE.getLogger().info("Generating report");
    }

    @Override
    public void reset() {
        super.reset();
        spreadUpdate(State.IDLE);
    }

    @Override
    public void update(Observable o, Object arg) {
        ExternalState state = (ExternalState) arg;
        switch (state) {
            case IDLE:
                // fallthrough
            case FINISHED:
                spreadUpdate(State.IDLE);
                break;
            case RUNNING:
                spreadUpdate(State.RUNNING);
                break;
        }
    }
}
