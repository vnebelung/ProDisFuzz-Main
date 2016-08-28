/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import model.process.fuzzoptions.Process.RecordingMethod;
import org.testng.Assert;
import org.testng.annotations.Test;

public class RecordingMethodRunnerTest {

    @SuppressWarnings("EmptyMethod")
    @Test
    public void testGetRecordingMethod() throws Exception {
        // See testRun
    }

    @Test
    public void testRun() throws Exception {
        RecordingMethodRunner recordingMethodRunner = new RecordingMethodRunner(RecordingMethod.ALL);
        recordingMethodRunner.run();
        Assert.assertEquals(recordingMethodRunner.getRecordingMethod(), RecordingMethod.ALL);

        recordingMethodRunner = new RecordingMethodRunner(RecordingMethod.CRITICAL);
        recordingMethodRunner.run();
        Assert.assertEquals(recordingMethodRunner.getRecordingMethod(), RecordingMethod.CRITICAL);
    }
}
