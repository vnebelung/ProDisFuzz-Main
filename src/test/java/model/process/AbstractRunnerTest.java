/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import model.process.AbstractRunner.ExternalState;
import org.testng.Assert;
import org.testng.annotations.Test;
import support.RunnerMonitor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AbstractRunnerTest {

    @Test
    public void testMarkStart() throws Exception {
        Runner runner = new Runner(2);
        RunnerMonitor monitor = new RunnerMonitor();
        runner.addObserver(monitor);

        runner.markStart();
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.RUNNING);
        Assert.assertEquals(runner.getWorkDone(), 0);

        runner.markProgress();
        try {
            runner.markStart();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.RUNNING);
        Assert.assertEquals(runner.getWorkDone(), 1);

        runner.markFinish();
        runner.markStart();
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.RUNNING);
        Assert.assertEquals(runner.getWorkDone(), 0);

        runner.markProgress();
        runner.markCancel();
        runner.markStart();
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.RUNNING);
        Assert.assertEquals(runner.getWorkDone(), 0);
    }

    @Test
    public void testMarkProgress() throws Exception {

        // Finite total work

        Runner runner = new Runner(2);
        RunnerMonitor monitor = new RunnerMonitor();
        runner.addObserver(monitor);

        try {
            runner.markProgress();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        Assert.assertNull(monitor.getCurrentState());
        Assert.assertEquals(runner.getWorkDone(), 0);

        runner.markStart();
        runner.markProgress();
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.RUNNING);
        Assert.assertEquals(runner.getWorkDone(), 1);

        try {
            runner.markProgress();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.RUNNING);
        Assert.assertEquals(runner.getWorkDone(), 1);

        runner.markFinish();
        try {
            runner.markProgress();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.FINISHED);
        Assert.assertEquals(runner.getWorkDone(), 2);

        // Infinite total work

        runner = new Runner(-1);
        monitor = new RunnerMonitor();
        runner.addObserver(monitor);

        try {
            runner.markProgress();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        Assert.assertNull(monitor.getCurrentState());
        Assert.assertEquals(runner.getWorkDone(), 0);

        runner.markStart();
        runner.markProgress();
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.RUNNING);
        Assert.assertEquals(runner.getWorkDone(), 1);

        runner.markFinish();
        try {
            runner.markProgress();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.FINISHED);
        Assert.assertEquals(runner.getWorkDone(), 2);
    }

    @Test
    public void testMarkFinish() throws Exception {

        // Finite total work

        Runner runner = new Runner(2);
        RunnerMonitor monitor = new RunnerMonitor();
        runner.addObserver(monitor);

        try {
            runner.markFinish();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        Assert.assertNull(monitor.getCurrentState());
        Assert.assertEquals(runner.getWorkDone(), 0);

        runner.markStart();
        try {
            runner.markFinish();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.RUNNING);
        Assert.assertEquals(runner.getWorkDone(), 0);

        runner.markProgress();
        runner.markFinish();
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.FINISHED);
        Assert.assertEquals(runner.getWorkDone(), 2);

        try {
            runner.markFinish();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.FINISHED);
        Assert.assertEquals(runner.getWorkDone(), 2);

        // Infinitive total work

        runner = new Runner(-1);
        monitor = new RunnerMonitor();
        runner.addObserver(monitor);

        try {
            runner.markFinish();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        Assert.assertNull(monitor.getCurrentState());
        Assert.assertEquals(runner.getWorkDone(), 0);

        runner.markStart();
        runner.markProgress();
        runner.markFinish();
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.FINISHED);
        Assert.assertEquals(runner.getWorkDone(), 2);

        try {
            runner.markFinish();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.FINISHED);
        Assert.assertEquals(runner.getWorkDone(), 2);
    }

    @Test
    public void testMarkCancel() throws Exception {
        Runner runner = new Runner(2);
        RunnerMonitor monitor = new RunnerMonitor();
        runner.addObserver(monitor);

        try {
            runner.markCancel();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        Assert.assertNull(monitor.getCurrentState());
        Assert.assertEquals(runner.getWorkDone(), 0);

        runner.markStart();
        try {
            runner.markCancel();
        } catch (IllegalStateException ignored) {
            Assert.fail();
        }
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.IDLE);
        Assert.assertEquals(runner.getWorkDone(), 0);

        runner.markStart();
        runner.markProgress();
        try {
            runner.markCancel();
        } catch (IllegalStateException ignored) {
            Assert.fail();
        }
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.IDLE);
        Assert.assertEquals(runner.getWorkDone(), 1);

        runner.markStart();
        runner.markProgress();
        runner.markFinish();
        try {
            runner.markCancel();
            Assert.fail();
        } catch (IllegalStateException ignored) {
        }
        Assert.assertEquals(monitor.getCurrentState(), ExternalState.FINISHED);
        Assert.assertEquals(runner.getWorkDone(), 2);

    }

    @Test
    public void testGetTotalWork() throws Exception {

        // Finite total work

        Runner runner = new Runner(2);
        Assert.assertEquals(runner.getTotalWork(), 2);
        runner.markStart();
        Assert.assertEquals(runner.getTotalWork(), 2);
        runner.markProgress();
        Assert.assertEquals(runner.getTotalWork(), 2);
        runner.markFinish();
        Assert.assertEquals(runner.getTotalWork(), 2);

        // Infinite total work

        runner = new Runner(-1);
        Assert.assertEquals(runner.getTotalWork(), -1);
        runner.markStart();
        Assert.assertEquals(runner.getTotalWork(), -1);
        runner.markProgress();
        Assert.assertEquals(runner.getTotalWork(), -1);
        runner.markFinish();
        Assert.assertEquals(runner.getTotalWork(), -1);
    }

    @Test
    public void testGetWorkDone() throws Exception {
        Runner runner = new Runner(2);
        Assert.assertEquals(runner.getWorkDone(), 0);
        runner.markStart();
        Assert.assertEquals(runner.getWorkDone(), 0);
        runner.markProgress();
        Assert.assertEquals(runner.getWorkDone(), 1);
        runner.markFinish();
        Assert.assertEquals(runner.getWorkDone(), 2);
    }

    @Test
    public void testSubmitToThreadPool() throws InterruptedException, ExecutionException {
        Future<ExternalState> future = AbstractRunner.submitToThreadPool(() -> ExternalState.RUNNING);
        Assert.assertEquals(future.get(), ExternalState.RUNNING);
    }

    private static class Runner extends AbstractRunner {

        public Runner(int totalWork) {
            super(totalWork);
        }

        @Override
        public void run() {
        }
    }
}
