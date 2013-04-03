/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.process;

import model.RunnableThread;
import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;

import java.util.Observable;
import java.util.Observer;

/**
 * The abstract Class AbstractThreadP implements an abstract process class with
 * a thread.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public abstract class AbstractThreadP extends AbstractP implements Observer {

    /**
     * The collect thread.
     */
    protected RunnableThread thread = null;

    /**
     * The total number of working steps.
     */
    protected int totalProgress = 0;

    /**
     * The current progress already done.
     */
    protected int progress = 0;

    /**
     * The current state message process.
     */
    protected String stateMessage = null;

    /**
     * Starts the thread.
     */
    protected void start(final AbstractR runnable) {
        runnable.addObserver(this);
        thread = new RunnableThread(runnable);
        thread.start();
    }

    /**
     * Gets the state of the fuzzing runnable.
     *
     * @return DONE if the runnable is not running, STUCK if the runnable is
     *         stuck, RUN if the runnable is running, CANCELED if the runnable
     *         is canceled, FINISHED if the runnable has finished
     */
    public RunnableState getThreadState() {
        return (thread == null ? RunnableState.FINISHED : thread
                .getRunnableState());
    }

    /**
     * Stops the collect thread.
     */
    public void stop() {
        thread.interrupt();
    }

    /**
     * Resets the process to its default values.
     */
    protected void reset() {
        thread = null; // NOPMD
        totalProgress = 0;
        progress = 0;
        stateMessage = null; // NOPMD
        spreadUpdate(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable observable, final Object arg) {
        final AbstractR data = (AbstractR) observable;
        thread.setRunnableState((RunnableState) arg);
        stateMessage = data.getStateMessage();
        totalProgress = data.getTotalProgress();
        progress = data.getProgress();
        // If the thread was canceled, the progress is reseted to 0
        if (thread.getRunnableState() == RunnableState.CANCELED) {
            progress = 0;
        }
        // If the thread is canceled or finished, the thread can die
        if (thread.getRunnableState() == RunnableState.FINISHED
                || thread.getRunnableState() == RunnableState.CANCELED) {
            try {
                thread.join();
            } catch (InterruptedException e) {
            }
        }
        spreadUpdate(false);
    }

    /**
     * Gets the number of total working steps. If the runnable is running infinite times, -1 is returned.
     *
     * @return the number of total working steps
     */
    public int getTotalProgress() {
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
     * Indicates whether the thread is running
     *
     * @return true, if the state is RUNNING or STUCK
     */
    public boolean isThreadRunning() {
        boolean isRunning = false;
        if (thread != null) {
            isRunning = (thread.getRunnableState() == RunnableState.RUNNING || thread
                    .getRunnableState() == RunnableState.STUCK);
        }
        return isRunning;
    }

}