/*
 * This file is part of ProDisFuzz, modified on 03.04.14 20:36.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import model.Model;
import model.process.AbstractThreadProcess;
import model.process.fuzzoptions.FuzzOptionsProcess.CommunicationSave;
import model.process.fuzzoptions.FuzzOptionsProcess.InjectionMethod;
import model.protocol.InjectedProtocolStructure;
import model.record.Recordings;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.Observable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FuzzingProcess extends AbstractThreadProcess {

    private InjectedProtocolStructure injectedProtocolStructure;
    private Recordings recordings;
    private FuzzingRunnable runnable;
    @SuppressWarnings("rawtypes")
    private Future fuzzingFuture;

    /**
     * Instantiates a new process responsible for executing the fuzz-testing.
     */
    public FuzzingProcess() {
        super();
        injectedProtocolStructure = new InjectedProtocolStructure();
        recordings = new Recordings();
    }

    /**
     * Initiates the fuzzing process.
     *
     * @param injectedProtocolStructure the injected protocol structure
     * @param target                    the target
     * @param interval                  the fuzzing interval
     * @param timeout                   the fuzzing timeout
     * @param saveCommunication         the save communication
     * @param injectionMethod           the injection method
     */
    public void init(InjectedProtocolStructure injectedProtocolStructure, InetSocketAddress target, int interval, int
            timeout, CommunicationSave saveCommunication, InjectionMethod
            injectionMethod) {
        this.injectedProtocolStructure = injectedProtocolStructure;
        recordings.clear();
        runnable = new FuzzingRunnable(injectionMethod, injectedProtocolStructure, target, timeout, interval,
                saveCommunication);
        runnable.addObserver(this);
        spreadUpdate();
    }

    @Override
    public int getWorkProgress() {
        return (runnable == null) ? 0 : runnable.getWorkProgress();
    }

    @Override
    public int getWorkTotal() {
        return (runnable == null) ? 0 : runnable.getWorkTotal();
    }

    @Override
    public void interrupt() {
        if (fuzzingFuture.isDone()) {
            return;
        }
        fuzzingFuture.cancel(true);
        Model.INSTANCE.getLogger().warning("Fuzzing process interrupted");
        spreadUpdate();
    }

    @Override
    public void start() {
        Model.INSTANCE.getLogger().info("Fuzzing process started");
        fuzzingFuture = AbstractThreadProcess.EXECUTOR.submit(runnable);
        spreadUpdate();
    }

    @Override
    protected void complete() {
        try {
            fuzzingFuture.get();
        } catch (CancellationException | InterruptedException ignored) {
            interrupt();
            return;
        } catch (ExecutionException e) {
            Model.INSTANCE.getLogger().error(e);
            interrupt();
            return;
        }
        Model.INSTANCE.getLogger().info("Learn process successfully completed");
    }

    @Override
    public boolean isRunning() {
        return (fuzzingFuture != null) && !fuzzingFuture.isDone();
    }

    @Override
    public void reset() {
        injectedProtocolStructure.clear();
        recordings.clear();
        spreadUpdate();
    }

    /**
     * Gets the time the fuzzing was started.
     *
     * @return the start time of the fuzzing process, or -1 if the process has not been started after the last reset
     */
    public Temporal getStartTime() {
        return runnable.getStartTime();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (runnable.isFinished()) {
            complete();
        }
        recordings = runnable.getRecordings();
        spreadUpdate();
    }

    /**
     * Gets the saved data files.
     *
     * @return the saved data files
     */
    public Recordings getRecordings() {
        return recordings;
    }

    /**
     * Gets the fuzzing duration.
     *
     * @return the fuzzing duration
     */
    public Duration getDuration() {
        return runnable.getDuration();
    }

}
