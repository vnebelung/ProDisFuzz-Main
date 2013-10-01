/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:28.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model;

import model.logger.Logger;

import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class InjectedProtocolPart {

    private final ProtocolPart protocolPart;
    private Path library;
    private DataInjectionMethod dataInjectionMethod;

    /**
     * Instantiates a new injected protocol part. The class consists of a protocol part and information about the
     * selected data injection method.
     *
     * @param protocolPart the protocol part
     */
    public InjectedProtocolPart(final ProtocolPart protocolPart) {
        this.protocolPart = protocolPart;
        if (protocolPart.getType() == ProtocolPart.Type.VAR) {
            dataInjectionMethod = DataInjectionMethod.RANDOM;
        }
    }

    /**
     * Gets the data injection method.
     *
     * @return the data injection method, null for not variable protocol parts
     */
    public DataInjectionMethod getDataInjectionMethod() {
        return protocolPart.getType() == ProtocolPart.Type.VAR ? dataInjectionMethod : null;
    }

    /**
     * Gets the library path.
     *
     * @return the path to the library file, null for not variable protocol parts
     */
    public Path getLibrary() {
        return protocolPart.getType() != ProtocolPart.Type.VAR || library == null ? null : Paths.get(library.toString
                ());

    }

    /**
     * Sets the library file.
     *
     * @param path the path to the library file
     */
    public void setLibrary(final String path) {
        if (protocolPart.getType() != ProtocolPart.Type.VAR) {
            return;
        }
        final Path newLibrary = Paths.get(path).toAbsolutePath().normalize();
        if (path.isEmpty()) {
            library = null;
            return;
        }
        // Update only if the path is a file
        if (!Files.isRegularFile(newLibrary)) {
            Logger.getInstance().error("'" + newLibrary.toString() + "' is not a regular file");
            library = null;
            return;
        }
        // Update only if the file is readable
        if (!Files.isReadable(newLibrary)) {
            Logger.getInstance().error("'" + newLibrary.toString() + "' is not a readable");
            library = null;
            return;
        }
        library = newLibrary;
    }

    /**
     * Sets the injection data to library-based, that means the injected data is read from a file.
     *
     * @return true if the injection method has changed
     */
    public boolean setLibraryInjection() {
        if (protocolPart.getType() != ProtocolPart.Type.VAR || dataInjectionMethod == DataInjectionMethod.LIBRARY) {
            return false;
        }
        dataInjectionMethod = DataInjectionMethod.LIBRARY;
        return true;
    }

    /**
     * Sets the injection data to random-based, that means the injected data is randomly generated.
     *
     * @return true if the injection method has changed
     */
    public boolean setRandomInjection() {
        if (protocolPart.getType() != ProtocolPart.Type.VAR || dataInjectionMethod == DataInjectionMethod.RANDOM) {
            return false;
        }
        dataInjectionMethod = DataInjectionMethod.RANDOM;
        return true;
    }

    /**
     * Returns the protocol part.
     *
     * @return the protocol part
     */
    public ProtocolPart getProtocolPart() {
        return protocolPart;
    }

    /**
     * Returns the number of lines of the library file. If the data injection method for this part is not set to
     * LIBRARY the return value will be 0.
     *
     * @return the number of lines of the defined library file
     */
    public int getNumOfLibraryLines() {
        if (dataInjectionMethod != DataInjectionMethod.LIBRARY) {
            return 0;
        }
        int numOfLines = 0;
        // Go to the last line and read out its number
        try (LineNumberReader lineNumberReader = new LineNumberReader(Files.newBufferedReader(library,
                Charset.forName("UTF-8")))) {
            lineNumberReader.skip(Integer.MAX_VALUE);
            numOfLines = lineNumberReader.getLineNumber() + 1;

        } catch (IOException e) {
            Logger.getInstance().error(e);
        }
        return numOfLines;
    }

    /**
     * Returns the given line of the library file.
     *
     * @param lineNo the line number to return
     * @return the library line or null in case of an error
     */
    public List<Byte> getLibraryLine(final int lineNo) {
        final List<Byte> bytes = new ArrayList<>();
        try (LineNumberReader lineNumberReader = new LineNumberReader(Files.newBufferedReader(library,
                Charset.forName("UTF-8")))) {
            for (int i = 0; i < lineNo; i++) {
                lineNumberReader.readLine();
            }
            final String line = lineNumberReader.readLine();
            for (final byte b : line.getBytes()) {
                bytes.add(b);
            }
            return bytes;
        } catch (IOException e) {
            Logger.getInstance().error(e);
            return null;
        }
    }

    /**
     * Returns a random line of the library file.
     *
     * @return the random library line or null in case of an error
     */
    public List<Byte> getRandomLibraryLine() {
        final int rnd = RandomPool.getInstance().nextInt(getNumOfLibraryLines());
        return getLibraryLine(rnd);
    }

    public static enum DataInjectionMethod {RANDOM, LIBRARY}
}
