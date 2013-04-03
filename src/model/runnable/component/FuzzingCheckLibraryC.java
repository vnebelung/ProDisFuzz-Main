/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.ProtocolPart;
import model.ProtocolPart.DataMode;
import model.ProtocolPart.Type;
import model.RunnableThread.RunnableState;
import model.process.OptionsP.Mode;
import model.runnable.AbstractR;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class FuzzingCheckLibraryC implements the functionality to check all
 * library files for correct syntax.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class FuzzingCheckLibraryC extends AbstractC {

    /**
     * The protocol parts.
     */
    private final List<ProtocolPart> parts;

    /**
     * The fuzzing mode.
     */
    private final Mode mode;

    /**
     * Instantiates a new fuzzing check library component.
     *
     * @param runnable the parent runnable
     * @param parts    the protocol parts
     */
    public FuzzingCheckLibraryC(final AbstractR runnable,
                                final List<ProtocolPart> parts, final Mode mode) {
        super(runnable);
        this.mode = mode;
        this.parts = parts;
    }

    /**
     * Checks whether the library files have a valid structure, that means they
     * contain only byte values separated through a space per line.
     *
     * @throws IOException           Signals that an I/O exception has occurred.
     * @throws NumberFormatException the number format exception
     */
    public void check() throws IOException, NumberFormatException {
        final List<ProtocolPart> varLibraryParts = getVarLibraryParts();
        if (!varLibraryParts.isEmpty()) {
            if (mode == Mode.SIMULTANEOUS) {
                // Check only the library file of the first part because it is
                // the same at all parts
                varLibraryParts.get(0).updateNumOfLines();
                checkLibraryFile(varLibraryParts.get(0).getLibraryPath(),
                        varLibraryParts.get(0).getNumOfLibraryLines());
            } else {
                // Check every single library file for the correct syntax
                for (int i = 0; i < varLibraryParts.size()
                        && !runnable.isInterrupted(); i++) {
                    varLibraryParts.get(i).updateNumOfLines();
                    checkLibraryFile(varLibraryParts.get(i).getLibraryPath(),
                            varLibraryParts.get(i).getNumOfLibraryLines());
                    if (i < varLibraryParts.size() - 1) {
                        runnable.sleep(AbstractR.SLEEPING_TIME);
                    }
                }
            }
        }
    }

    /**
     * Checks whether the library file has a valid structure.
     *
     * @param libraryPath the path of the library file to check
     * @param numOfLines  the number of library lines
     * @throws IOException
     */
    private void checkLibraryFile(final Path libraryPath, final int numOfLines)
            throws IOException, NumberFormatException {
        runnable.setStateMessage(
                "i:Checking library file '" + libraryPath.getFileName()
                        + "' ...", RunnableState.RUNNING);
        BufferedReader bufferedReader = null;
        String line;
        try {
            bufferedReader = Files.newBufferedReader(libraryPath,
                    Charset.forName("UTF-8"));
            for (int i = 0; i < numOfLines; i++) {
                line = bufferedReader.readLine();
                if (line == null || line.isEmpty()) {
                    throw new NumberFormatException(
                            "Line "
                                    + (i + 1)
                                    + " appears to be empty. Please remove all empty lines and try again.");
                }
            }
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        runnable.increaseProgress("s:done.", RunnableState.RUNNING);
    }

    /**
     * Returns only the protocol parts that have the Type VAR and the data mode
     * LIBRARY.
     *
     * @return the variable protocol parts
     */
    private List<ProtocolPart> getVarLibraryParts() {
        final List<ProtocolPart> varParts = new ArrayList<ProtocolPart>();
        for (ProtocolPart part : parts) {
            if (part.getType() == Type.VAR
                    && part.getDataMode() == DataMode.LIBRARY) {
                varParts.add(part);
            }
        }
        return varParts;
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.component.AbstractComponent#getTotalProgress()
     */
    @Override
    public int getTotalProgress() {
        int steps = 0;
        if (mode == Mode.SIMULTANEOUS) {
            steps++;
        } else {
            final List<ProtocolPart> varLibraryParts = getVarLibraryParts();
            steps = varLibraryParts.size();
        }
        return steps;
    }

}