/*
 * This file is part of ProDisFuzz, modified on 18.12.13 22:54.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
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

    /**
     * Gets the work progress of the thread.
     *
     * @return the thread progress
     */
    public abstract int getWorkProgress();

    /**
     * Gets the total amount of work executed by the thread.
     *
     * @return total amount of thread work or -1 if infinite
     */
    public abstract int getWorkTotal();

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
