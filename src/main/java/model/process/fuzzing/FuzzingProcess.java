/*
 * This file is part of ProDisFuzz, modified on 03.04.14 20:36.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import model.Model;
import model.SavedDataFile;
import model.process.AbstractThreadProcess;
import model.process.fuzzOptions.FuzzOptionsProcess;
import model.protocol.InjectedProtocolStructure;

import javax.xml.datatype.Duration;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FuzzingProcess extends AbstractThreadProcess {

    private InjectedProtocolStructure injectedProtocolStructure;
    private List<SavedDataFile> savedDataFiles;
    private FuzzingRunnable runnable;
    private Future fuzzingFuture;

    /**
     * Instantiates a new process responsible for executing the fuzz-testing.
     */
    public FuzzingProcess() {
        super();
        injectedProtocolStructure = new InjectedProtocolStructure();
        savedDataFiles = new ArrayList<>();
    }

    @Override
    public void init() {
        injectedProtocolStructure = Model.INSTANCE.getFuzzOptionsProcess().getInjectedProtocolStructure();
        savedDataFiles = new ArrayList<>();
        InetSocketAddress target = Model.INSTANCE.getFuzzOptionsProcess().getTarget();
        int interval = Model.INSTANCE.getFuzzOptionsProcess().getInterval();
        int timeout = Model.INSTANCE.getFuzzOptionsProcess().getTimeout();
        FuzzOptionsProcess.CommunicationSave saveCommunication = Model.INSTANCE.getFuzzOptionsProcess()
                .getSaveCommunication();
        FuzzOptionsProcess.InjectionMethod injectionMethod = Model.INSTANCE.getFuzzOptionsProcess()
                .getInjectionMethod();
        runnable = new FuzzingRunnable(injectionMethod, injectedProtocolStructure, target, timeout, interval,
                saveCommunication);
        runnable.addObserver(this);
        spreadUpdate();
    }

    @Override
    public int getWorkProgress() {
        return runnable == null ? 0 : runnable.getWorkProgress();
    }

    @Override
    public int getWorkTotal() {
        return runnable == null ? 0 : runnable.getWorkTotal();
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
        fuzzingFuture = EXECUTOR.submit(runnable);
        spreadUpdate();
    }

    @Override
    protected void complete() {
        try {
            fuzzingFuture.get();
        } catch (CancellationException | InterruptedException e) {
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
        return fuzzingFuture != null && !fuzzingFuture.isDone();
    }

    @Override
    public void reset() {
        injectedProtocolStructure.clear();
        try {
            for (SavedDataFile each : savedDataFiles) {
                Files.delete(each.getFilePath());
            }
        } catch (IOException ignored) {
        }
        savedDataFiles.clear();
        spreadUpdate();
    }

    /**
     * Gets the time the fuzzing was started.
     *
     * @return the start time of the fuzzing process, or -1 if the process has not been started after the last reset
     */
    public long getStartTime() {
        return runnable.getStartTime();
    }

    @Override
    public void update(Observable o, Object arg) {
        FuzzingRunnable runnable = (FuzzingRunnable) o;
        if (runnable.isFinished()) {
            complete();
        }
        savedDataFiles = new ArrayList<>(runnable.getSavedDataFiles());
        spreadUpdate();
    }

    /**
     * Gets the saved data files.
     *
     * @return the saved data files
     */
    public List<SavedDataFile> getSavedDataFiles() {
        return Collections.unmodifiableList(savedDataFiles);
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
