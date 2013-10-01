/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:27.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractThreadProcess extends AbstractProcess implements Observer {
    public final static ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    protected int workProgress;
    protected int workTotal;

    /**
     * Increases the work progress counter so that every finished step of the thread can be noticed.
     */
    public void increaseWorkProgress() {
        workProgress++;
        spreadUpdate();
    }

    /**
     * Gets the work progress of the thread.
     *
     * @return the thread progress
     */
    public int getWorkProgress() {
        return workProgress;
    }

    /**
     * Gets the total amount of work executed by the thread or -1 if the amount can not be determined.
     *
     * @return total amount of thread work
     */
    public int getWorkTotal() {
        return workTotal;
    }

    /**
     * Interrupts the thread.
     */
    public abstract void interrupt();

    /**
     * Starts the thread.
     */
    public abstract void start();

    /**
     * Completes the thread.
     */
    protected abstract void complete();

    /**
     * Gets the status of the thread.
     *
     * @return true if the thread is alive
     */
    public abstract boolean isRunning();
}
