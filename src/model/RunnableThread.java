/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model;

/**
 * The Class RunnableThread implements the standard thread functionality plus an
 * extra indicator for the internal state of the runnable.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class RunnableThread extends Thread {

    /**
     * The internal state of the runnable: stuck, running, finished or canceled.
     */
    public static enum RunnableState {
        STUCK, RUNNING, FINISHED, CANCELED
    }

    /**
     * The current runnable state.
     */
    private RunnableState runnableState;

    /**
     * Initiates a new runnable thread.
     *
     * @param target the target runnable
     */
    public RunnableThread(final Runnable target) {
        super(target);
        runnableState = RunnableState.FINISHED;
    }

    /**
     * Sets the runnable to STUCK, RUNNING or FINISHED
     *
     * @param runnableState the runnable state
     */
    public void setRunnableState(final RunnableState runnableState) {
        if (runnableState == RunnableState.STUCK
                || runnableState == RunnableState.RUNNING
                || runnableState == RunnableState.FINISHED) {
            this.runnableState = runnableState;
        }
    }

    /**
     * Gets the current runnable state
     *
     * @return the runnable state
     */
    public RunnableState getRunnableState() {
        return runnableState;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Thread#start()
     */
    @Override
    public void start() {
        runnableState = RunnableState.RUNNING;
        super.start();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Thread#interrupt()
     */
    @Override
    public void interrupt() {
        runnableState = RunnableState.CANCELED;
        super.interrupt();
    }

}