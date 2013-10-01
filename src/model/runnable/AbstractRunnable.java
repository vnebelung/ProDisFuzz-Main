/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:25.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.runnable;

import model.process.AbstractThreadProcess;

import java.util.Observable;

public abstract class AbstractRunnable extends Observable implements Runnable {
    private final Runnable update;
    protected boolean finished;

    /**
     * Instantiates a new runnable.
     */
    AbstractRunnable() {
        super();
        update = new Runnable() {
            @Override
            public void run() {
                setChanged();
                notifyObservers();
            }
        };
    }

    /**
     * Notifies all observers about an update and sleeps for 250 ms.
     *
     * @throws InterruptedException thrown when the thread is interrupted while sleeping
     */
    protected final void spreadUpdate() throws InterruptedException {
        AbstractThreadProcess.EXECUTOR.execute(update);
        Thread.sleep(250);
    }

    /**
     * Returns whether the runnable has successfully finished
     *
     * @return true if the runnable has finished without errors or interruptions
     */
    public final boolean isFinished() {
        return finished;
    }
}
