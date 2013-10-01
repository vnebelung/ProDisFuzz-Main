/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:25.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.callable;

import model.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

public class FuzzingCheckLibraryCallable implements Callable<Boolean> {

    private final Path file;

    /**
     * Instantiates a new callable.
     *
     * @param file the file path
     */
    public FuzzingCheckLibraryCallable(final Path file) {
        this.file = file;
    }

    @Override
    public Boolean call() {
        try (BufferedReader bufferedReader = Files.newBufferedReader(file, Charset.forName("UTF-8"))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) {
                    return false;
                }
            }
        } catch (IOException e) {
            Logger.getInstance().error(e);
            return false;
        }
        return true;
    }
}
