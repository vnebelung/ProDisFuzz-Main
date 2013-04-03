/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.TempRecordFile;
import model.runnable.AbstractR;

import java.io.IOException;

/**
 * The Class FuzzingRecordC implements the functionality to record bytes to
 * temporary files in the system's temp directory.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class FuzzingRecordC extends AbstractC { // NOPMD

    /**
     * Instantiates a new fuzzing connection component.
     *
     * @param runnable the parent runnable
     */
    public FuzzingRecordC(final AbstractR runnable) {
        super(runnable);
    }

    /**
     * Writes bytes to a temporary file in the system's temp directory
     *
     * @param bytes   the bytes
     * @param isCrash the flag whether the recorded bytes represent a crash
     * @param time    the system time the file is recorded
     * @return the created temporary file
     * @throws IOException
     */
    public TempRecordFile write(final byte[] bytes, final boolean isCrash, final long time)
            throws IOException {
        runnable.setStateMessage("i:Saving data to file.",
                RunnableState.RUNNING);
        return new TempRecordFile(bytes, isCrash, time);
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.component.AbstractComponent#getTotalProgress()
     */
    @Override
    public int getTotalProgress() {
        return 0;
    }

}