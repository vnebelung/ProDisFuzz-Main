/*
 * This file is part of ProDisFuzz, modified on 03.04.14 20:36.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.protocol;

import model.Model;
import model.RandomPool;

import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InjectedProtocolBlock extends ProtocolBlock {

    private Path library;
    private DataInjectionMethod dataInjectionMethod;

    /**
     * Instantiates a new protocol block that is responsible for defining a data block of the protocol structure. The
     * default data injection method is RANDOM.
     *
     * @param type  the type of the protocol block
     * @param bytes the content in bytes
     */
    public InjectedProtocolBlock(Type type, Byte[] bytes) {
        super(type, bytes);
        if (type == Type.VAR) {
            dataInjectionMethod = DataInjectionMethod.RANDOM;
        }
    }

    /**
     * Returns the data injection method indicating what data will be injected during the fuzzing process.
     *
     * @return the data injection method, null for not variable protocol blocks
     */
    public DataInjectionMethod getDataInjectionMethod() {
        return getType() == Type.VAR ? dataInjectionMethod : null;
    }

    /**
     * Returns the path to the library file that contains the data input for fuzzing.
     *
     * @return the path to the library file, null for not variable protocol blocks or not library-based blocks
     */
    public Path getLibrary() {
        return getType() != Type.VAR || library == null ? null : Paths.get(library.toString());

    }

    /**
     * Sets the path to the library file used for data injection while fuzzing.
     *
     * @param path the path to the library file
     */
    public void setLibrary(Path path) {
        if (getType() != Type.VAR) {
            return;
        }
        if (path == null) {
            library = null;
            return;
        }
        Path newLibrary = path.toAbsolutePath().normalize();
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
        if (getType() != Type.VAR || dataInjectionMethod == DataInjectionMethod.LIBRARY) {
            return;
        }
        dataInjectionMethod = DataInjectionMethod.LIBRARY;
        library = null;
    }

    /**
     * Sets the injection data to random-based, that means the injected data is randomly generated.
     */
    public void setRandomInjection() {
        if (getType() != Type.VAR || dataInjectionMethod == DataInjectionMethod.RANDOM) {
            return;
        }
        dataInjectionMethod = DataInjectionMethod.RANDOM;
        library = null;
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
        int result = 0;
        // Go to the last line and read out its number
        try (LineNumberReader lineNumberReader = new LineNumberReader(Files.newBufferedReader(library,
                Charset.forName("UTF-8")))) {
            String line;
            while ((line = lineNumberReader.readLine()) != null) {
                if (!line.isEmpty()) {
                    result++;
                }
            }
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error(e);
        }
        return result;
    }

    /**
     * Returns the given line of the library file.
     *
     * @param lineNo the line number to return
     * @return the library line or null in case of an error
     */
    public byte[] getLibraryLine(int lineNo) {
        try (LineNumberReader lineNumberReader = new LineNumberReader(Files.newBufferedReader(library,
                Charset.forName("UTF-8")))) {
            int count = 0;
            String result;
            do {
                result = lineNumberReader.readLine();
                if (!result.isEmpty()) {
                    count++;
                }
            } while (count - 1 != lineNo);
            return result.getBytes();
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
    public byte[] getRandomLibraryLine() {
        int rnd = RandomPool.getInstance().nextInt(getNumOfLibraryLines());
        return getLibraryLine(rnd);
    }

    public static enum DataInjectionMethod {RANDOM, LIBRARY}
}
