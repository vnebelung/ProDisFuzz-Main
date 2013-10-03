/*
 * This file is part of ProDisFuzz, modified on 03.10.13 22:24.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import model.Model;
import model.ProtocolFile;
import model.ProtocolPart;
import model.runnable.LearnRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LearnProcess extends AbstractThreadProcess {

    private List<ProtocolFile> files;
    private List<ProtocolPart> parts;
    private LearnRunnable runnable;
    private Future learnFuture;

    /**
     * Instantiates a new learn process.
     */
    public LearnProcess() {
        super();
        files = new ArrayList<>();
        parts = new ArrayList<>();
    }

    @Override
    public void reset() {
        files.clear();
        parts.clear();
        workProgress = 0;
        workTotal = 0;
        spreadUpdate();
    }

    @Override
    public void init() {
        files = new ArrayList<>(Model.INSTANCE.getCollectProcess().getSelectedFiles());
        // Convert + Select + Hirschberg + Parts + Clean + Parts
        workTotal = 1 + files.size() - 1 + files.size() - 1 + files.size() - 1 + 1 + 1;
        workProgress = 0;
        runnable = new LearnRunnable(files);
        runnable.addObserver(this);
        spreadUpdate();
    }

    /**
     * Gets the learned protocol parts.
     *
     * @return the protocol parts
     */
    public List<ProtocolPart> getProtocolParts() {
        return Collections.unmodifiableList(parts);
    }

    @Override
    public void interrupt() {
        if (learnFuture.isDone()) {
            return;
        }
        learnFuture.cancel(true);
        Model.INSTANCE.getLogger().warning("Learn process interrupted");
        workProgress = 0;
        spreadUpdate();
    }

    @Override
    public void start() {
        Model.INSTANCE.getLogger().info("Learn process started");
        workProgress = 0;
        learnFuture = EXECUTOR.submit(runnable);
        spreadUpdate();
    }

    @Override
    protected void complete() {
        try {
            learnFuture.get();
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
        return learnFuture != null && !learnFuture.isDone();
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final LearnRunnable data = (LearnRunnable) o;
        if (data.isFinished()) {
            complete();
        }
        parts = new ArrayList<>(data.getParts());
        increaseWorkProgress();
    }
}
