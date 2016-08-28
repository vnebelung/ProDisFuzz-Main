/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import model.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the library checker callable, responsible for verifying that a file-based library for library-based
 * data injection is well formatted.
 */
class LibraryChecker implements Callable<Boolean> {

    private static final Pattern HEX_BIN = Pattern.compile("[0-9a-f]{2}+");
    private Path library;

    /**
     * Constructs the callable.
     *
     * @param library the path to the library
     */
    public LibraryChecker(Path library) {
        this.library = library;
    }

    @Override
    public Boolean call() {
        if (library == null) {
            return false;
        }
        if (!Files.isRegularFile(library)) {
            return false;
        }
        if (!Files.isReadable(library)) {
            return false;
        }
        try (BufferedReader reader = Files.newBufferedReader(library)) {
            String line = reader.readLine();
            if (line == null) {
                return false;
            }
            do {
                Matcher matcher = HEX_BIN.matcher(line);
                if (!matcher.matches()) {
                    return false;
                }
            } while ((line = reader.readLine()) != null);
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error(e);
            return false;
        }
        return true;
    }
}
