/*
 * This file is part of ProDisFuzz, modified on 13.03.14 22:09.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import java.util.Observable;

public abstract class AbstractRunnable extends Observable implements Runnable {
    private boolean finished;
    private int workTotal;
    private int workProgress;
    private Runnable updateRunnable;

    /**
     * Instantiates a new abstract runnable responsible for setting a basic structure for a runnable..
     */
    protected AbstractRunnable() {
        workTotal = -1;
        finished = false;
        updateRunnable = () -> {
            setChanged();
            notifyObservers();
        };
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
     * Gets the total amount of work executed by the thread.
     *
     * @return total amount of thread work or -1 if infinite
     */
    public int getWorkTotal() {
        return workTotal;
    }

    /**
     * Sets the work total to the given value. Shall be -1 if the work is infinite.
     *
     * @param i the work total
     */
    protected void setWorkTotal(int i) {
        workTotal = i;
    }

    /**
     * Increases the work progress counter by one so that every finished step of the thread can be noticed by
     * observers.
     *
     * @throws InterruptedException
     */
    protected void increaseWorkProgress() throws InterruptedException {
        workProgress++;
        spreadUpdate();
    }

    /**
     * Resets the work progress to 0.
     *
     * @throws InterruptedException
     */
    protected void resetWorkProgress() throws InterruptedException {
        workProgress = 0;
        spreadUpdate();
    }

    /**
     * Notifies all observers about an update and sleeps for 250 ms to give observers time to update themselves before
     * proceeding.
     *
     * @throws InterruptedException
     */
    private void spreadUpdate() throws InterruptedException {
        AbstractThreadProcess.EXECUTOR.execute(updateRunnable);
        Thread.sleep(250);
    }

    /**
     * Returns whether the runnable has successfully finished.
     *
     * @return true if the runnable has finished without errors or interruptions
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Sets the finish status of the runnable.
     *
     * @param b true, if the runnable has finished its work
     * @throws InterruptedException
     */
    protected void setFinished(boolean b) throws InterruptedException {
        if (finished == b) {
            return;
        }
        finished = b;
        spreadUpdate();
    }


}
