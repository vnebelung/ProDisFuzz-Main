/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.collect;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryCheckTest {

    @Test
    public void testCall() throws URISyntaxException {
        DirectoryCheck directoryCheck = new DirectoryCheck(getClass().getResource("/").toURI().getPath());
        Path result = directoryCheck.call();
        Assert.assertEquals(result,
                Paths.get(getClass().getResource("/").toURI().getPath()).toAbsolutePath().normalize());

        //noinspection HardCodedStringLiteral
        directoryCheck = new DirectoryCheck(getClass().getResource("/").getPath() + "dummy/");
        result = directoryCheck.call();
        Assert.assertNull(result);
    }
}
