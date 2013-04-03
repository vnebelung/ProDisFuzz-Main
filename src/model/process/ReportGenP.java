/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.process;

import model.ProtocolPart;
import model.TempRecordFile;
import model.runnable.ReportGenR;

import javax.xml.datatype.Duration;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * The Class ReportGenP encapsulates the process of generating the report.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ReportGenP extends AbstractThreadP {

    /**
     * The recorded files.
     */
    private List<TempRecordFile> recordFiles;

    /**
     * The output path.
     */
    private Path outputPath;

    /**
     * The destination address and port.
     */
    private InetSocketAddress destination;

    /**
     * The fuzzing interval in milliseconds.
     */
    private int interval;

    /**
     * The protocol part list.
     */
    private List<ProtocolPart> parts;

    /**
     * The fuzzing duration.
     */
    private Duration duration;

    /**
     * The amount of total fuzzing steps.
     */
    private int fuzzingTotalProgress; // NOPMD

    /**
     * The number of executed fuzzing steps.
     */
    private int fuzzingProgress;

    /**
     * The save communication flag.
     */
    private boolean saveCommunication;

    /**
     * The connection timeout in milliseconds.
     */
    private int timeout;

    /**
     * Instantiates a new XML generation process.
     */
    public ReportGenP() {
        super();
        recordFiles = new ArrayList<TempRecordFile>();
        outputPath = null; // NOPMD
    }

    /**
     * Resets all variables to the default value and notifies all observers.
     */
    public void reset() {
        try {
            for (int i = 0; i < recordFiles.size(); i++) {
                Files.delete(recordFiles.get(i).getFilePath());
            }
        } catch (IOException e) { // NOPMD
            // Should not happen
        }
        recordFiles.clear();
        outputPath = null; // NOPMD
        super.reset();
    }

    /**
     * Starts the collect thread.
     */
    public void start() {
        super.start(new ReportGenR(recordFiles, outputPath, duration,
                destination, interval, parts, fuzzingProgress,
                fuzzingTotalProgress, saveCommunication, timeout));
    }

    /**
     * Gets the output path representing the directory the report will be written into. Can be null if it is not initialized yet.
     *
     * @return the output path
     */
    public Path getOutputPath() {
        return outputPath;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable observable, final Object arg) {
        super.update(observable, arg);
    }

    /**
     * Sets the output path of the directory where the report will be saved.
     *
     * @param path the output path
     */
    public void setOutputPath(final String path) {
        final Path newPath = Paths.get(path).toAbsolutePath().normalize();
        if (!newPath.equals(outputPath)) {
            if (Files.isDirectory(newPath) && Files.isWritable(newPath)) {
                outputPath = newPath;
            } else {
                outputPath = null; // NOPMD
            }
            spreadUpdate(false);
        }
    }

    /**
     * Initiates all for the report necessary variables.
     *
     * @param recordFiles          the recorded files
     * @param duration             the fuzzing duration
     * @param destination          the destination
     * @param interval             the fuzzing interval
     * @param parts                the protocol parts
     * @param fuzzingProgress      the fuzzing progress
     * @param fuzzingTotalProgress the fuzzing total work
     * @param saveCommunication    flag that indicates whether all communication shall be
     *                             recorded
     * @param timeout              the connection timeout
     */
    public void initResults(final List<TempRecordFile> recordFiles,
                            final Duration duration, final InetSocketAddress destination,
                            final int interval, final List<ProtocolPart> parts,
                            final int fuzzingProgress, final int fuzzingTotalProgress, // NOPMD
                            final boolean saveCommunication, final int timeout) {
        this.recordFiles = recordFiles;
        this.duration = duration;
        this.destination = destination;
        this.interval = interval;
        this.parts = parts;
        this.fuzzingProgress = fuzzingProgress;
        this.fuzzingTotalProgress = fuzzingTotalProgress;
        this.saveCommunication = saveCommunication;
        this.timeout = timeout;
    }

}
