/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The Class TempRecordFile represents a temporary file that is being recorded
 * during the fuzzing process and saved for later use.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class TempRecordFile {

    /**
     * The time at which this file was recorded.
     */
    private final long time;

    /**
     * Boolean flag to indicate whether this file is a recorded crash.
     */
    private final boolean isCrash; // NOPMD

    /**
     * The file path.
     */
    private final Path filePath;

    /**
     * The output path.
     */
    private Path outputPath;

    /**
     * Instantiates a new crash.
     *
     * @param content the file content
     * @param isCrash flag that indicates whether the record lead to a crash
     * @param time    the system time the file is recorded
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public TempRecordFile(final byte[] content, final boolean isCrash,
                          final long time) throws IOException {
        this.isCrash = isCrash;
        this.time = time;
        filePath = Files.createTempFile("prodisfuzz_", null);
        // Write the bytes into the file
        Files.write(filePath, content);
    }

    /**
     * Gets the file path.
     *
     * @return the file path
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Sets the file path.
     *
     * @return true, if this file is a recorded crash
     */
    public boolean isCrash() {
        return isCrash;
    }

    /**
     * Gets the time the file was recorded.
     *
     * @return the time in milliseconds
     */
    public long getTime() {
        return time;
    }

    /**
     * Gets the output path.
     *
     * @return the output path
     */
    public Path getOutputPath() {
        return outputPath;
    }

    /**
     * Sets the output path.
     *
     * @param outputPath the new output path
     */
    public void setOutputPath(final Path outputPath) {
        this.outputPath = outputPath;
    }

}
