/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package support;


import model.process.AbstractProcess;
import model.process.AbstractProcess.State;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeoutException;

public class ProcessMonitor implements Observer {

    private List<State> states = new ArrayList<>();
    private Observable o;
    private AbstractProcess process;
    private static final int MAX_WAITING_SECONDS = 15;

    public ProcessMonitor(AbstractProcess process) {
        this.process = process;
        process.addObserver(this);
    }

    @Override
    public void update(Observable o, Object arg) {
        this.o = o;
        states.add((State) arg);
    }

    public boolean areStatesCompleteAndCorrect() {
        if (states.size() != process.getTotalWork() + 1) {
            return false;
        }
        for (int i = 0; i < states.size() - 1; i++) {
            if (states.get(i) != State.RUNNING) {
                return false;
            }
        }
        return states.get(states.size() - 1) == State.IDLE;
    }

    public Observable getObservable() {
        return o;
    }

    public void waitForFinish() throws InterruptedException, TimeoutException {
        Instant start = Instant.now();
        while (states.isEmpty() || states.get(states.size() - 1) != State.IDLE) {
            //noinspection BusyWait
            Thread.sleep(50);
            if (Duration.between(start, Instant.now()).getSeconds() > MAX_WAITING_SECONDS) {
                throw new TimeoutException("Process monitor is waiting more than " + MAX_WAITING_SECONDS + " sec.");
            }
        }
    }

    public void waitForFinishAndReset() throws InterruptedException, TimeoutException {
        Instant start = Instant.now();
        while (states.isEmpty() || states.get(states.size() - 1) != State.IDLE) {
            //noinspection BusyWait
            Thread.sleep(50);
            if (Duration.between(start, Instant.now()).getSeconds() > MAX_WAITING_SECONDS) {
                throw new TimeoutException("Process monitor is waiting more than " + MAX_WAITING_SECONDS + " sec.");
            }
        }
        reset();
    }

    public void reset() {
        states.clear();
    }

    public State getLastState() {
        if (states.isEmpty()) {
            //noinspection ReturnOfNull
            return null;
        }
        return states.get(states.size() - 1);
    }
}
