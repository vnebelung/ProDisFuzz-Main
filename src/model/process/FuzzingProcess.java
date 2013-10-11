/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:35.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import model.InjectedProtocolPart;
import model.Model;
import model.SavedDataFile;
import model.runnable.FuzzingRunnable;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FuzzingProcess extends AbstractThreadProcess {

    private List<InjectedProtocolPart> injectedProtocolParts;
    private List<SavedDataFile> savedDataFiles;
    private Duration duration;
    private long startTime;
    private FuzzingRunnable runnable;
    private FuzzOptionsProcess.InjectionMethod injectionMethod;
    private Future fuzzingFuture;

    /**
     * Instantiates a new process responsible for controlling the fuzzing.
     */
    public FuzzingProcess() {
        super();
        injectedProtocolParts = new ArrayList<>();
        savedDataFiles = new ArrayList<>();
    }

    @Override
    public void init() {
        injectedProtocolParts = new ArrayList<>(Model.INSTANCE.getFuzzOptionsProcess().getInjectedProtocolParts());
        savedDataFiles = new ArrayList<>();
        final InetSocketAddress target = Model.INSTANCE.getFuzzOptionsProcess().getTarget();
        final int interval = Model.INSTANCE.getFuzzOptionsProcess().getInterval();
        final int timeout = Model.INSTANCE.getFuzzOptionsProcess().getTimeout();
        final FuzzOptionsProcess.CommunicationSave saveCommunication = Model.INSTANCE.getFuzzOptionsProcess()
                .getSaveCommunication();
        injectionMethod = Model.INSTANCE.getFuzzOptionsProcess().getInjectionMethod();
        // Work steps depending on the previous chosen fuzz options
        workTotal = calcWorkTotal();
        workProgress = 0;
        startTime = -1;
        try {
            duration = DatatypeFactory.newInstance().newDuration(0);
        } catch (DatatypeConfigurationException e) {
            Model.INSTANCE.getLogger().error(e);
        }
        runnable = new FuzzingRunnable(injectionMethod, injectedProtocolParts, target, timeout, interval,
                saveCommunication);
        runnable.addObserver(this);
        spreadUpdate();
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
        workProgress = 0;
        fuzzingFuture = EXECUTOR.submit(runnable);
        spreadUpdate();
    }

    @Override
    protected void complete() {
        try {
            fuzzingFuture.get();
        } catch (InterruptedException e) {
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
        injectedProtocolParts.clear();
        try {
            for (final SavedDataFile savedDataFile : savedDataFiles) {
                Files.delete(savedDataFile.getFilePath());
            }
        } catch (IOException ignored) {
        }
        savedDataFiles.clear();
        startTime = -1;
        try {
            duration = DatatypeFactory.newInstance().newDuration(0);
        } catch (DatatypeConfigurationException e) {
            Model.INSTANCE.getLogger().error(e);
        }
        spreadUpdate();
    }

    /**
     * Calculates the amount of work for this process.
     *
     * @return the number of work steps
     */
    private int calcWorkTotal() {
        switch (injectionMethod) {
            case SEPARATE:
                return calcWorkTotalSeparate();
            case SIMULTANEOUS:
                return calcWorkTotalSimultaneous();
            default:
                return 0;
        }
    }

    /**
     * Calculates the amount of work for separate data injections.
     *
     * @return the number of work steps, -1 for infinite work
     */
    private int calcWorkTotalSeparate() {
        int result = 1;
        for (final InjectedProtocolPart injectedProtocolPart : Model.INSTANCE.getFuzzOptionsProcess().filterVarParts
                (injectedProtocolParts)) {
            switch (injectedProtocolPart.getDataInjectionMethod()) {
                case LIBRARY:
                    result += injectedProtocolPart.getNumOfLibraryLines();
                    break;
                case RANDOM:
                    return -1;
                default:
                    return 0;
            }
        }
        return result;
    }

    /**
     * Calculates the amount of work for simultaneous data injections.
     *
     * @return the number of work steps, -1 for infinite work
     */
    private int calcWorkTotalSimultaneous() {
        final InjectedProtocolPart injectedProtocolPart = Model.INSTANCE.getFuzzOptionsProcess().filterVarParts
                (injectedProtocolParts).get(0);
        switch (injectedProtocolPart.getDataInjectionMethod()) {
            case LIBRARY:
                return injectedProtocolPart.getNumOfLibraryLines() + 1;
            case RANDOM:
                return -1;
            default:
                return 0;
        }
    }

    /**
     * Gets the time the fuzzing was started.
     *
     * @return the start time of the fuzzing process
     */
    public long getStartTime() {
        return startTime;
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final FuzzingRunnable data = (FuzzingRunnable) o;
        if (data.isFinished()) {
            complete();
        }
        startTime = data.getStartTime();
        try {
            duration = DatatypeFactory.newInstance().newDuration(data.getDuration());
        } catch (DatatypeConfigurationException e) {
            Model.INSTANCE.getLogger().error(e);
        }
        savedDataFiles = new ArrayList<>(data.getSavedDataFiles());
        increaseWorkProgress();
    }

    /**
     * Gets the number of recorded data files.
     *
     * @return the number of recorded data files
     */
    public int getNumOfRecords() {
        return savedDataFiles.size();
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
        return duration;
    }

}
