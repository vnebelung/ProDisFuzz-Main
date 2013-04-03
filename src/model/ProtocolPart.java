/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model;

import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * The Class ProtocolPart implements the structure of the learned protocol.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ProtocolPart {

    /**
     * The enum fuzzing Mode.
     */
    public static enum DataMode {
        RANDOM, LIBRARY
    }

    /**
     * The enum Type.
     */
    public static enum Type {
        VAR, FIXED
    }

    ;

    /**
     * The type.
     */
    private final Type type;

    /**
     * The minimum length.
     */
    private int minLength;

    /**
     * The maximum length.
     */
    private int maxLength;

    /**
     * All content consisting of every content of each possible value.
     */
    private final Set<List<Byte>> content;

    /**
     * The chosen fuzzing data mode for this part.
     */
    private DataMode dataMode;

    /**
     * The path of the library file for fuzzing.
     */
    private Path libraryPath;

    /**
     * The number of lines of the library file.
     */
    private int numOfLibraryLines;

    /**
     * Instantiates a new protocol part.
     *
     * @param type the type of the protocol part
     */
    public ProtocolPart(final Type type) {
        this.type = type;
        minLength = Integer.MAX_VALUE;
        maxLength = Integer.MIN_VALUE;
        content = new HashSet<List<Byte>>();
        dataMode = DataMode.RANDOM;
    }

    /**
     * Adds content in bytes.
     *
     * @param bytes the content in bytes
     */
    public void addContent(final List<Byte> bytes) {
        minLength = Math.min(minLength, bytes.size());
        maxLength = Math.max(maxLength, bytes.size());
        content.add(bytes);
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * Gets the minimum length.
     *
     * @return the minLength
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Gets the maximum length.
     *
     * @return the maxLength
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Gets the content.
     *
     * @return the content
     */
    public Set<List<Byte>> getContent() {
        return content;
    }

    /**
     * Gets the fuzzing data mode chosen for this part.
     *
     * @return the data mode
     */
    public DataMode getDataMode() {
        return dataMode;
    }

    /**
     * Sets the fuzzing data mode for this part.
     *
     * @param dataMode the fuzzing mode to set
     */
    public void setDataMode(final DataMode dataMode) {
        this.dataMode = dataMode;
    }

    /**
     * Sets the library file path.
     *
     * @param path the path to the library file
     * @return true if the library file has changed
     */
    public boolean setLibraryPath(final String path) {
        boolean hasChanged;
        final Path newPath = Paths.get(path).toAbsolutePath().normalize();
        // Update only if the new library file is not he same as the current one
        if (newPath.equals(libraryPath)) {
            hasChanged = false;
        } else {
            // Update only if the file is readable and in fact a file
            if (Files.isRegularFile(newPath) && Files.isReadable(newPath)) {
                libraryPath = newPath;
                setNumOfLibraryLines();
            } else {
                libraryPath = null; // NOPMD
                numOfLibraryLines = 0;
            }
            hasChanged = true;
        }
        return hasChanged;
    }

    /**
     * Sets the number of library lines.
     */
    private void setNumOfLibraryLines() {
        LineNumberReader lineNumberReader = null;
        // Go to the last line and read out its number
        try {
            lineNumberReader = new LineNumberReader(Files.newBufferedReader(
                    libraryPath, Charset.forName("UTF-8")));
            lineNumberReader.skip(Long.MAX_VALUE);
            numOfLibraryLines = lineNumberReader.getLineNumber() + 1;

        } catch (IOException e) {
            numOfLibraryLines = 0;
        } finally {
            try {
                if (lineNumberReader != null) {
                    lineNumberReader.close();
                }
            } catch (IOException e) { // NOPMD
                // Should not happen
            }
        }
    }

    /**
     * Gets the path of the library file.
     *
     * @return the path of the library file
     */
    public Path getLibraryPath() {
        return libraryPath;
    }

    /**
     * Gets the number of lines of the library file.
     *
     * @return the number of lines
     */
    public int getNumOfLibraryLines() {
        return numOfLibraryLines;
    }

    /**
     * Updates the number of lines.
     */
    public void updateNumOfLines() {
        setNumOfLibraryLines();
    }

    /**
     * Gets a random line of the library file.
     *
     * @return a random line
     */
    public String getRandomLibraryLine() {
        String line = "";
        LineNumberReader lineNumberReader = null;
        try {
            final Random random = new SecureRandom();
            final int lineNumber = random.nextInt(numOfLibraryLines);
            lineNumberReader = new LineNumberReader(Files.newBufferedReader(
                    libraryPath, Charset.forName("UTF-8")));
            while (lineNumberReader.getLineNumber() < lineNumber - 1) {
                lineNumberReader.readLine();
            }
            line = lineNumberReader.readLine();
            lineNumberReader.close();
        } catch (IOException e) {
            line = "";
        } finally {
            try {
                if (lineNumberReader != null) {
                    lineNumberReader.close();
                }
            } catch (IOException e) { // NOPMD
                // Should not happen
            }
        }
        return line;
    }

}
