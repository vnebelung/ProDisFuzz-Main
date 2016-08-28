/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import model.process.AbstractProcess;
import model.process.AbstractRunner;
import model.process.AbstractRunner.ExternalState;
import model.process.fuzzoptions.Process.InjectionMethod;
import model.process.fuzzoptions.Process.RecordingMethod;
import model.protocol.InjectedProtocolStructure;
import model.record.Recordings;

import java.net.InetSocketAddress;
import java.util.Observable;

/**
 * This class is the fuzzing process, responsible for executing the fuzz-testing.
 */
public class Process extends AbstractProcess {

    private Recordings recordings;

    /**
     * Constructs a new fuzzing process.
     */
    public Process() {
        super();
        recordings = new Recordings();
    }

    /**
     * Starts the fuzzing.
     *
     * @param injectedProtocolStructure the injected protocol structure
     * @param target                    the target
     * @param interval                  the fuzzing interval
     * @param timeout                   the fuzzing timeout
     * @param recordingMethod           the recording method
     * @param injectionMethod           the injection method
     */
    public void startFuzzing(InjectedProtocolStructure injectedProtocolStructure, InetSocketAddress target,
                             int interval, int timeout, RecordingMethod recordingMethod,
                             InjectionMethod injectionMethod) {
        recordings.clear();
        AbstractRunner runner =
                new Runner(injectionMethod, injectedProtocolStructure, target, timeout, interval, recordingMethod);
        runner.addObserver(this);
        submitToThreadPool(runner);
    }

    @Override
    public void reset() {
        super.reset();
        recordings.clear();
        spreadUpdate(State.IDLE);
    }

    @Override
    public void update(Observable o, Object arg) {
        ExternalState state = (ExternalState) arg;
        Runner runner = (Runner) o;
        switch (state) {
            case IDLE:
                spreadUpdate(State.IDLE);
                break;
            case RUNNING:
                recordings = runner.getRecordings();
                spreadUpdate(State.RUNNING);
                break;
            case FINISHED:
                recordings = runner.getRecordings();
                spreadUpdate(State.IDLE);
                break;
        }
    }

    /**
     * Gets the saved data files.
     *
     * @return the saved data files
     */
    public Recordings getRecordings() {
        return recordings;
    }

}
