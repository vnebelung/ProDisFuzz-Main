/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * This is the base class for runnables, responsible for setting a basic structure for a runnable.
 */
public abstract class AbstractRunner extends Observable implements Runnable {

    private static final ExecutorService EXECUTOR =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private InternalState currentState;
    private int totalWork;
    private int workDone;
    private Instant startTime;
    private Instant endTime;

    /**
     * Constructs a new abstract runnable.
     *
     * @param totalWork the amount of total work the runner is executing
     */
    protected AbstractRunner(int totalWork) {
        this.totalWork = totalWork;
        workDone = 0;
        currentState = InternalState.IDLE;
    }

    /**
     * ubmits a value-returning task for execution and returns a Future representing the pending results of the task.
     * The Future's get method will return the task's result upon successful completion.
     *
     * @param callable the callable to submit
     * @param <V>      the type of the callable's result
     * @return a Future representing pending completion of the task
     */
    public static <V> Future<V> submitToThreadPool(Callable<V> callable) {
        return EXECUTOR.submit(callable);
    }

    /**
     * Marks the runnable as started and busy doing its work. This resets the work progress to 0 and updates the
     * internal state to indicate that the runnable is running. All observers will be notified about the change if
     * necessary.
     */
    protected void markStart() {
        if (currentState != InternalState.IDLE) {
            throw new IllegalStateException(
                    "IllegalStateException: Runner cannot be started while state is '" + currentState + '\'');
        }
        currentState = InternalState.RUNNING;
        workDone = 0;
        startTime = Instant.now();
        spreadUpdate(ExternalState.RUNNING);
    }

    /**
     * Increases the work progress counter by 1. If the counter is less than the total work the internal state is
     * updated to indicate that the process is running. All observers will be notified about the change if necessary.
     */
    protected void markProgress() {
        if (currentState != InternalState.RUNNING) {
            throw new IllegalStateException(
                    "IllegalStateException: Work counter cannot increase while state is '" + currentState + '\'');
        }
        if (totalWork > -1 && workDone >= totalWork - 1) {
            throw new IllegalStateException(
                    "IllegalStateException: Work counter cannot increase furthermore at this " + "point");
        }
        workDone++;
        currentState = InternalState.RUNNING;
        spreadUpdate(ExternalState.RUNNING);
    }

    /**
     * Increases the work progress counter by 1 and marks the runnable as finished. All observers will be notified about
     * the change if necessary.
     */
    protected void markFinish() {
        if (currentState != InternalState.RUNNING) {
            throw new IllegalStateException(
                    "IllegalStateException: Runner cannot finish while state is '" + currentState + '\'');
        }
        if (totalWork > -1 && workDone != totalWork - 1) {
            throw new IllegalStateException(
                    "IllegalStateException: Work counter cannot increase one last time at " + "this point");
        }
        workDone++;
        currentState = InternalState.IDLE;
        endTime = Instant.now();
        spreadUpdate(ExternalState.FINISHED);
    }

    /**
     * Marks the process as cancelled. Observers will be notified about the change.
     */
    protected void markCancel() {
        if (currentState != InternalState.RUNNING) {
            throw new IllegalStateException(
                    "IllegalStateException: Runner cannot be cancelled while state is '" + currentState + '\'');
        }
        currentState = InternalState.IDLE;
        endTime = Instant.now();
        spreadUpdate(ExternalState.IDLE);
    }

    /**
     * Returns the total amount of work that is executed by this runnable. The amount of work is the sum of all of the
     * runnable's callables work.
     *
     * @return total amount of work
     */
    public int getTotalWork() {
        return totalWork;
    }

    /**
     * Returns the amount of work that is currently already done by this runnable.
     *
     * @return total amount of work
     */
    public int getWorkDone() {
        return workDone;
    }

    /**
     * Notifies all observers in a about a change.
     *
     * @param externalState the state that will be propagated to listeners.
     */
    private void spreadUpdate(ExternalState externalState) {
        setChanged();
        notifyObservers(externalState);
    }

    /**
     * Returns the time the runner has ended.
     *
     * @return the end time
     */
    public Temporal getEndTime() {
        return endTime;
    }

    /**
     * Returns the time the runner was started.
     *
     * @return the start time
     */
    public Temporal getStartTime() {
        return startTime;
    }

    public enum ExternalState {IDLE, RUNNING, FINISHED}

    private enum InternalState {IDLE, RUNNING}

}
