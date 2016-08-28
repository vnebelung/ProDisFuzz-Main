/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import model.process.AbstractProcess.State;
import model.process.AbstractRunner.ExternalState;
import org.testng.Assert;
import org.testng.annotations.Test;
import support.ProcessMonitor;

import java.time.Instant;
import java.util.Observable;
import java.util.concurrent.TimeoutException;

import static java.time.temporal.ChronoField.MILLI_OF_SECOND;

@SuppressWarnings({"unused", "BusyWait"})
public class AbstractProcessTest {

    private static final int WORK_COUNT = 2;

    /**
     * Test priority has to be lower than all others because once the executor has set up a thread it is persistent
     * through the whole test run.
     */
    @Test(priority = 1)
    public void testSubmitToThreadPool() {
        BasicProcess process = new BasicProcess();
        AbstractRunner runner = new Runner();
        runner.addObserver(process);
        int threadCount = Thread.activeCount();
        process.submitToThreadPool(runner);
        Assert.assertEquals(Thread.activeCount(), threadCount + 1);
    }

    @Test(priority = 2)
    public void testGetEndTime() throws Exception {
        BasicProcess process = new BasicProcess();
        AbstractRunner runner = new Runner();
        runner.addObserver(process);
        ProcessMonitor monitor = new ProcessMonitor(process);
        Assert.assertNull(process.getEndTime());

        process.submitToThreadPool(runner);
        monitor.waitForFinishAndReset();
        Instant end = Instant.now();
        Assert.assertTrue(Math.abs(process.getEndTime().get(MILLI_OF_SECOND) - end.get(MILLI_OF_SECOND)) < 100);
    }

    @Test(priority = 2)
    public void testGetStartTime() throws Exception {
        BasicProcess process = new BasicProcess();
        AbstractRunner runner = new Runner();
        runner.addObserver(process);
        ProcessMonitor monitor = new ProcessMonitor(process);
        Assert.assertNull(process.getStartTime());

        Instant start = Instant.now();
        process.submitToThreadPool(runner);
        monitor.waitForFinishAndReset();
        Assert.assertTrue(Math.abs(process.getStartTime().get(MILLI_OF_SECOND) - start.get(MILLI_OF_SECOND)) < 100);

    }

    @Test(priority = 2)
    public void testReset() throws Exception {
        BasicProcess process = new BasicProcess();
        AbstractRunner runner = new Runner();
        runner.addObserver(process);
        ProcessMonitor monitor = new ProcessMonitor(process);
        process.submitToThreadPool(runner);
        monitor.waitForFinishAndReset();

        process.reset();
        Assert.assertEquals(process.getWorkDone(), 0);
        Assert.assertEquals(process.getTotalWork(), 0);
    }

    @Test(priority = 2)
    public void testStop() throws Exception {
        BasicProcess process = new BasicProcess();
        Runner runner = new Runner();
        runner.addObserver(process);
        process.submitToThreadPool(runner);
        Thread.sleep(750);
        process.stop();
        Thread.sleep(25);
        Assert.assertTrue(runner.isCancelled());
    }

    @Test(priority = 2)
    public void testGetWorkDone() throws InterruptedException, TimeoutException {
        WorkDoneCountProcess process = new WorkDoneCountProcess();
        AbstractRunner runner = new Runner();
        runner.addObserver(process);
        ProcessMonitor monitor = new ProcessMonitor(process);
        Assert.assertEquals(process.getWorkDone(), 0);

        process.submitToThreadPool(runner);
        monitor.waitForFinishAndReset();
    }

    @Test(priority = 2)
    public void testGetTotalWork() throws Exception {
        TotalWorkCountProcess process = new TotalWorkCountProcess();
        AbstractRunner runner = new Runner();
        runner.addObserver(process);
        ProcessMonitor monitor = new ProcessMonitor(process);
        Assert.assertEquals(process.getTotalWork(), 0);

        process.submitToThreadPool(runner);
        monitor.waitForFinishAndReset();
        Assert.assertEquals(process.getCount(), 3);
    }

    @Test(priority = 2)
    public void testIsComplete() throws InterruptedException, TimeoutException {
        CompleteProgress process = new CompleteProgress();
        AbstractRunner runner = new Runner();
        runner.addObserver(process);
        ProcessMonitor monitor = new ProcessMonitor(process);
        Assert.assertFalse(process.isComplete());

        process.submitToThreadPool(runner);
        monitor.waitForFinishAndReset();
        Assert.assertTrue(process.isComplete());
    }

    @Test(priority = 2)
    public void testSpreadUpdate() throws Exception {
        BasicProcess process = new BasicProcess();
        ProcessMonitor monitor = new ProcessMonitor(process);
        Assert.assertNull(monitor.getLastState());

        process.spreadUpdate(State.RUNNING);
        Assert.assertEquals(monitor.getLastState(), State.RUNNING);

        process.spreadUpdate(State.IDLE);
        Assert.assertEquals(monitor.getLastState(), State.IDLE);
    }

    private static class Runner extends AbstractRunner {

        private boolean isCancelled;

        public Runner() {
            super(WORK_COUNT);
        }

        public boolean isCancelled() {
            return isCancelled;
        }

        @Override
        public void run() {
            markStart();
            try {
                Thread.sleep(500);
                markProgress();
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
                isCancelled = true;
            }
            markFinish();
        }
    }

    private static class BasicProcess extends AbstractProcess {
        @Override
        public void update(Observable o, Object arg) {
            ExternalState state = (ExternalState) arg;
            switch (state) {
                case IDLE:
                    // fallthrough
                case FINISHED:
                    spreadUpdate(State.IDLE);
                    break;
                case RUNNING:
                    spreadUpdate(State.RUNNING);
                    break;
            }
        }
    }

    private static class WorkDoneCountProcess extends BasicProcess {
        private int count;

        @Override
        public void update(Observable o, Object arg) {
            super.update(o, arg);
            if (count >= 0 && count <= WORK_COUNT) {
                Assert.assertEquals(getWorkDone(), count);
            } else {
                Assert.fail();
            }
            count++;
        }
    }

    private static class TotalWorkCountProcess extends BasicProcess {
        private int count;

        public int getCount() {
            return count;
        }

        @Override
        public void update(Observable o, Object arg) {
            super.update(o, arg);
            if (count <= WORK_COUNT) {
                Assert.assertEquals(getTotalWork(), WORK_COUNT);
            } else {
                Assert.fail();
            }
            count++;
        }
    }

    private static class CompleteProgress extends BasicProcess {
        private int count;

        @Override
        public void update(Observable o, Object arg) {
            super.update(o, arg);
            if (count < WORK_COUNT) {
                Assert.assertFalse(isComplete());
            } else if (count == WORK_COUNT) {
                Assert.assertTrue(isComplete());
            } else {
                Assert.fail();
            }
            count++;
        }
    }
}
