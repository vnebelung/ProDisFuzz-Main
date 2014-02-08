/*
 * This file is part of ProDisFuzz, modified on 08.02.14 23:36.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model;

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
     * Instantiates a new injected protocol part. The instance consists of a protocol part and information about the
     * selected data injection method.
     *
     * @param protocolPart the protocol part
     */
    public InjectedProtocolPart(ProtocolPart protocolPart) {
        this.protocolPart = protocolPart;
        if (protocolPart.getType() == ProtocolPart.Type.VAR) {
            dataInjectionMethod = DataInjectionMethod.RANDOM;
        }
    }

    /**
     * Returns the data injection method indicating what data will be injected during the fuzzing process.
     *
     * @return the data injection method, null for not variable protocol parts
     */
    public DataInjectionMethod getDataInjectionMethod() {
        return protocolPart.getType() == ProtocolPart.Type.VAR ? dataInjectionMethod : null;
    }

    /**
     * Returns the path to the library file that contains the data input for fuzzing.
     *
     * @return the path to the library file, null for not variable protocol parts or not library-based parts
     */
    public Path getLibrary() {
        return protocolPart.getType() != ProtocolPart.Type.VAR || library == null ? null : Paths.get(library.toString
                ());

    }

    /**
     * Sets the path to the library file used for data injection while fuzzing.
     *
     * @param p the path to the library file
     */
    public void setLibrary(Path p) {
        if (protocolPart.getType() != ProtocolPart.Type.VAR) {
            return;
        }
        if (p == null) {
            library = null;
            return;
        }
        Path newLibrary = p.toAbsolutePath().normalize();
        // Update only if the path is a file
        if (!Files.isRegularFile(newLibrary)) {
            Model.INSTANCE.getLogger().error("'" + newLibrary.toString() + "' is not a regular file");
            library = null;
            return;
        }
        // Update only if the file is readable
        if (!Files.isReadable(newLibrary)) {
            Model.INSTANCE.getLogger().error("'" + newLibrary.toString() + "' is not a readable");
            library = null;
            return;
        }
        library = newLibrary;
    }

    /**
     * Sets the injection data to library-based, that means the injected data is read from a file.
     */
    public void setLibraryInjection() {
        if (protocolPart.getType() != ProtocolPart.Type.VAR || dataInjectionMethod == DataInjectionMethod.LIBRARY) {
            return;
        }
        dataInjectionMethod = DataInjectionMethod.LIBRARY;
        library = null;
    }

    /**
     * Sets the injection data to random-based, that means the injected data is randomly generated.
     */
    public void setRandomInjection() {
        if (protocolPart.getType() != ProtocolPart.Type.VAR || dataInjectionMethod == DataInjectionMethod.RANDOM) {
            return;
        }
        dataInjectionMethod = DataInjectionMethod.RANDOM;
        library = null;
    }

    /**
     * Returns the protocol part that defines the content.
     *
     * @return the protocol part
     */
    public ProtocolPart getProtocolPart() {
        return protocolPart;
    }

    /**
     * Returns the number of lines of the library file.
     *
     * @return the number of lines of the defined library file or 0 if data injection method is not library-based
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
            Model.INSTANCE.getLogger().error(e);
        }
        return numOfLines;
    }

    /**
     * Returns the given line of the library file.
     *
     * @param lineNo the line number to return
     * @return the library line or null in case of an error
     */
    public List<Byte> getLibraryLine(int lineNo) {
        List<Byte> bytes = new ArrayList<>();
        try (LineNumberReader lineNumberReader = new LineNumberReader(Files.newBufferedReader(library,
                Charset.forName("UTF-8")))) {
            for (int i = 0; i < lineNo; i++) {
                lineNumberReader.readLine();
            }
            String line = lineNumberReader.readLine();
            if (line == null) { //TODO: Remove workaround. Last line can be empty -> null
                return bytes;
            }
            for (byte each : line.getBytes()) {
                bytes.add(each);
            }
            return bytes;
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error(e);
            return null;
        }
    }

    /**
     * Returns a random line of the library file.
     *
     * @return the random library line or null in case of an error
     */
    public List<Byte> getRandomLibraryLine() {
        int rnd = RandomPool.getInstance().nextInt(getNumOfLibraryLines());
        return getLibraryLine(rnd);
    }

    public static enum DataInjectionMethod {RANDOM, LIBRARY}
}
