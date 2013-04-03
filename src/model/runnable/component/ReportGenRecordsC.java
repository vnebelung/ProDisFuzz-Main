/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.TempRecordFile;
import model.runnable.AbstractR;
import model.runnable.ReportGenR;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * The Class ReportGenRecordsC implements the functionality to set the output
 * path for all recorded temporary files so they can be saved to permanent
 * files.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ReportGenRecordsC extends AbstractC {

    /**
     * The recorded files.
     */
    private final List<TempRecordFile> recordFiles;

    /**
     * The report output path.
     */
    private final Path outputPath;

    /**
     * Instantiates a new report generation crashes component.
     *
     * @param runnable    the runnable
     * @param recordFiles the recorded files
     * @param outputPath  the output path
     */
    public ReportGenRecordsC(final AbstractR runnable,
                             final List<TempRecordFile> recordFiles, final Path outputPath) {
        super(runnable);
        this.recordFiles = recordFiles;
        this.outputPath = outputPath;
    }

    /**
     * Sets the path of the output file of every crash.
     *
     * @param output the new path
     */
    public void setPath(final String output) {
        // Set the file path for each detected crash
        int messageIteration = 0;
        int messageCount = 0;
        for (int i = 0; i < recordFiles.size(); i++) {
            runnable.setStateMessage(
                    "i:Setting the output name of recorded file #" + (i + 1)
                            + " ...", RunnableState.RUNNING);
            recordFiles.get(i)
                    .setOutputPath(
                            Paths.get(outputPath.toString(), output
                                    + ReportGenR.FOLDER_POSTFIX, "record"
                                    + messageIteration + "-" + messageCount
                                    + ".bytes"));
            if (recordFiles.get(i).isCrash() == (messageCount == 0)) {
                messageIteration++;
                messageCount = 0;
            } else {
                messageCount = 1;
            }
            runnable.increaseProgress("s:done.", RunnableState.RUNNING);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.component.AbstractComponent#getTotalProgress()
     */
    @Override
    public int getTotalProgress() {
        return recordFiles.size();
    }

}
