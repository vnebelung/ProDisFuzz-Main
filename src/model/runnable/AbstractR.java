/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable;

import model.RunnableThread.RunnableState;
import org.eclipse.swt.widgets.Display;

import java.util.Observable;

/**
 * The abstract Class AbstractR implements all basic functionality of a runnable
 * class.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public abstract class AbstractR extends Observable implements Runnable {

    /**
     * The sleeping time between two working steps.
     */
    public final static int SLEEPING_TIME = 10;

    /**
     * The total number of working steps.
     */
    protected int totalProgress = Integer.MIN_VALUE;

    /**
     * The current progress already done.
     */
    protected int progress = 0;

    /**
     * The current state message process.
     */
    protected String stateMessage;

    /**
     * Notifies all observers about changes.
     *
     * @param state DONE if the runnable is not running, STUCK if the runnable is
     *              stuck, RUN if the runnable is running, CANCELED if the
     *              runnable is canceled, FINISHED if the runnable has finished
     *              normally
     */
    protected void spreadUpdate(final RunnableState state) {
        final Runnable sync = new Runnable() {
            @Override
            public void run() {
                setChanged();
                notifyObservers(state);
            }
        };
        if (state == RunnableState.FINISHED || state == RunnableState.CANCELED) {
            Display.getDefault().asyncExec(sync);
        } else {
            Display.getDefault().syncExec(sync);
        }
    }

    /**
     * Increases the progress counter and notifies all observers.
     *
     * @param state the internal program state
     */
    public void increaseProgress(final RunnableState state) {
        progress++;
        spreadUpdate(state);
    }

    /**
     * Increases the progress counter, sets a status message and notifies all
     * observers.
     *
     * @param message the state message
     * @param state   the internal program state
     */
    public void increaseProgress(final String message, final RunnableState state) {
        progress++;
        stateMessage = message;
        spreadUpdate(state);
    }

    /**
     * Sets a status message and notifies all observers.
     *
     * @param message the state message
     * @param state   the internal program state
     */
    public void setStateMessage(final String message, final RunnableState state) {
        stateMessage = message;
        spreadUpdate(state);
    }

    /**
     * Gets the number of total working steps. If the runnable is running infinite times, -1 is returned.
     *
     * @return the number of total working steps
     */
    public int getTotalProgress() {
        if (totalProgress == Integer.MIN_VALUE) {
            setTotalProgress();
        }
        return totalProgress;
    }

    /**
     * Gets the progress.
     *
     * @return the progress
     */
    public int getProgress() {
        return progress;
    }

    /**
     * Gets the state message.
     *
     * @return the state message
     */
    public String getStateMessage() {
        return stateMessage;
    }

    /**
     * Lets the thread sleep for the given interval.
     *
     * @param sleepTime to sleep in milliseconds
     */
    public void sleep(final long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            interrupt("w:Interrupted by user.");
        }
    }

    /**
     * Interrupt the parent thread because of the occurrence of an e.
     *
     * @param message the e message
     */
    protected void interrupt(final String message) {
        setStateMessage(message, RunnableState.RUNNING);
        Thread.currentThread().interrupt();
    }

    /**
     * Returns whether the thread is interrupted.
     *
     * @return true, if the thread is interrupted
     */
    public boolean isInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

    /**
     * Sets the total working steps by collecting all working steps from the
     * particular components of the runnable.
     */
    protected abstract void setTotalProgress();

}