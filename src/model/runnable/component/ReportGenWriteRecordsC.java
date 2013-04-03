/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.TempRecordFile;
import model.runnable.AbstractR;
import model.runnable.ReportGenR;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * The Class ReportGenWriteRecordsC implements the functionality to write all
 * crashes to files.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ReportGenWriteRecordsC extends AbstractC { // NOPMD

    /**
     * The output path.
     */
    private final Path outputPath;

    /**
     * The recorded files.
     */
    private final List<TempRecordFile> recordFiles;

    /**
     * Instantiates a new report generation write crashes component.
     *
     * @param runnable    the runnable
     * @param outputPath  the output path
     * @param recordFiles the recorded files
     */
    public ReportGenWriteRecordsC(final AbstractR runnable,
                                  final Path outputPath, final List<TempRecordFile> recordFiles) {
        super(runnable);
        this.outputPath = outputPath;
        this.recordFiles = recordFiles;
    }

    /**
     * Writes the crashes to files.
     *
     * @param output the output name
     * @throws TransformerFactoryConfigurationError
     *
     * @throws TransformerException
     * @throws IOException
     */
    public void write(final String output)
            throws TransformerFactoryConfigurationError, TransformerException,
            IOException {
        runnable.setStateMessage("i:Create record folder ...",
                RunnableState.RUNNING);
        final Path directory = outputPath.resolve(output
                + ReportGenR.FOLDER_POSTFIX);
        // Create the directory
        Files.createDirectory(directory);
        runnable.increaseProgress("s:done.", RunnableState.RUNNING);
        runnable.sleep(AbstractR.SLEEPING_TIME);
        // Write every recorded file in a file in the specified directory
        for (int i = 0; i < recordFiles.size(); i++) {
            runnable.setStateMessage("i:Create record file #" + (i + 1)
                    + " ...", RunnableState.RUNNING);
            // Move the temporary file to the output directory
            Files.move(recordFiles.get(i).getFilePath(), recordFiles.get(i)
                    .getOutputPath());
            runnable.increaseProgress("s:done.", RunnableState.RUNNING);
            if (i < recordFiles.size() - 1) {
                runnable.sleep(AbstractR.SLEEPING_TIME);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.component.AbstractComponent#getTotalProgress()
     */
    @Override
    public int getTotalProgress() {
        return 1 + recordFiles.size();
    }

}
