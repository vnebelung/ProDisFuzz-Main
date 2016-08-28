/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URISyntaxException;
import java.nio.file.Paths;

@SuppressWarnings("HardCodedStringLiteral")
public class LibraryCheckerTest {

    @Test
    public void testCall() throws URISyntaxException {
        LibraryChecker libraryChecker = new LibraryChecker(Paths.get(getClass().getResource("/library1.txt").toURI()));
        Assert.assertTrue(libraryChecker.call());

        libraryChecker = new LibraryChecker(null);
        Assert.assertFalse(libraryChecker.call());

        libraryChecker = new LibraryChecker(Paths.get(getClass().getResource("/").toURI()).resolve("dummy"));
        Assert.assertFalse(libraryChecker.call());

        libraryChecker = new LibraryChecker(Paths.get(getClass().getResource("/library2.txt").toURI()));
        Assert.assertFalse(libraryChecker.call());

        libraryChecker = new LibraryChecker(Paths.get(getClass().getResource("/library3.txt").toURI()));
        Assert.assertFalse(libraryChecker.call());

        libraryChecker = new LibraryChecker(Paths.get(getClass().getResource("/library4.txt").toURI()));
        Assert.assertFalse(libraryChecker.call());
    }
}
