/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import java.time.temporal.Temporal;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This class is the base class for process classes, responsible for handling basic class features. All processes are
 * executed in an own thread.
 */
public abstract class AbstractProcess extends Observable implements Observer {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private AbstractRunner activeRunner;
    private Future<?> activeFuture;

    /**
     * Sets the active runner.
     *
     * @param runner the runner
     */
    protected void submitToThreadPool(AbstractRunner runner) {
        activeRunner = runner;
        activeFuture = EXECUTOR.submit(activeRunner);
    }

    /**
     * Resets all variables to the default value.
     */
    public void reset() {
        if (activeFuture != null) {
            activeFuture.cancel(true);
        }
        activeRunner = null;
    }

    /**
     * Stops the process at the next interruption point. This will not reset the process to its default values but
     * keeping all values at their current state.
     */
    public void stop() {
        activeFuture.cancel(true);
    }

    /**
     * Returns the work done by this callable.
     *
     * @return the work progress
     */
    public int getWorkDone() {
        return activeRunner == null ? 0 : activeRunner.getWorkDone();
    }

    /**
     * Returns the total amount of work executed by this process.
     *
     * @return total amount of work if the total work is finite, or -1 if infinite
     */
    public int getTotalWork() {
        return activeRunner == null ? 0 : activeRunner.getTotalWork();
    }

    /**
     * Returns whether the process has successfully completed all of its work.
     *
     * @return true, if the process has finished successfully
     */
    public boolean isComplete() {
        if (activeRunner == null) {
            return false;
        }
        if (activeRunner.getTotalWork() == 0) {
            return false;
        }
        return activeRunner.getWorkDone() == activeRunner.getTotalWork();
    }

    /**
     * Notifies all observers in a about a change.
     *
     * @param state the state that will be propagated to listeners.
     */
    protected void spreadUpdate(State state) {
        setChanged();
        notifyObservers(state);
    }

    /**
     * Returns the time the process has ended.
     *
     * @return the end time or null, if it has not been successfully completed
     */
    public Temporal getEndTime() {
        if (activeRunner == null) {
            //noinspection ReturnOfNull
            return null;
        }
        return activeRunner.getEndTime();
    }

    /**
     * Returns the time the process was started.
     *
     * @return the start time or null, if it has not been started
     */
    public Temporal getStartTime() {
        if (activeRunner == null) {
            //noinspection ReturnOfNull
            return null;
        }
        return activeRunner.getStartTime();
    }

    public enum State {IDLE, RUNNING}

}
