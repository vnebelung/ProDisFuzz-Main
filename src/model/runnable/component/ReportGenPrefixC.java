/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;
import model.runnable.ReportGenR;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The Class ReportGenPrefixC implements the functionality to find an prefix for
 * the report output files that is not already in use.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ReportGenPrefixC extends AbstractC {

    /**
     * The report output path.
     */
    private final Path outputPath;

    /**
     * Instantiates a new report generation prefix component.
     *
     * @param runnable   the runnable
     * @param outputPath the output path
     */
    public ReportGenPrefixC(final AbstractR runnable, final Path outputPath) {
        super(runnable);
        this.outputPath = outputPath;
    }

    /**
     * Finds an output name for the file and directory of the report, that is
     * not already in use.
     *
     * @return the name of the file and directory
     */
    public String find() {
        String output = "results";
        Path file;
        Path directory;
        // Try to find a file and folder name that does not exists yet
        int postfix = 0;
        do {
            if (postfix > 0) {
                output = "results(" + postfix + ")";
            }
            runnable.setStateMessage("i:Trying '" + output
                    + "' as an output file and directory name ...",
                    RunnableState.RUNNING);
            file = Paths.get(outputPath.toString(), output + ".html");
            directory = Paths.get(outputPath.toString(), output
                    + ReportGenR.FOLDER_POSTFIX);
            if (Files.exists(file) || Files.isDirectory(directory)) {
                runnable.setStateMessage("w:Either '" + file.getFileName()
                        + "' or '" + directory.getFileName()
                        + "' does already exists.", RunnableState.RUNNING);
                runnable.sleep(AbstractR.SLEEPING_TIME);
            }
            postfix++;
        } while ((Files.exists(file) || Files.isDirectory(directory))
                && !runnable.isInterrupted());
        runnable.increaseProgress("s:done.", RunnableState.RUNNING);
        return output;
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.component.AbstractComponent#getTotalProgress()
     */
    @Override
    public int getTotalProgress() {
        return 1;
    }

}
