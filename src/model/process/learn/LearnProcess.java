/*
 * This file is part of ProDisFuzz, modified on 07.02.14 00:21.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.Model;
import model.ProtocolFile;
import model.ProtocolPart;
import model.process.AbstractThreadProcess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LearnProcess extends AbstractThreadProcess {

    private List<ProtocolFile> files;
    private List<ProtocolPart> parts;
    private LearnRunnable runnable;
    private Future learnFuture;

    /**
     * Instantiates a new process responsible for learning the protocol structure.
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
        spreadUpdate();
    }

    @Override
    public void init() {
        files = new ArrayList<>(Model.INSTANCE.getCollectProcess().getSelectedFiles());
        runnable = new LearnRunnable(files);
        runnable.addObserver(this);
        spreadUpdate();
    }

    /**
     * Returns the learned protocol parts.
     *
     * @return the protocol parts
     */
    public List<ProtocolPart> getProtocolParts() {
        return Collections.unmodifiableList(parts);
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
        if (learnFuture.isDone()) {
            return;
        }
        learnFuture.cancel(true);
        Model.INSTANCE.getLogger().warning("Learn process interrupted");
        spreadUpdate();
    }

    @Override
    public void start() {
        Model.INSTANCE.getLogger().info("Learn process started");
        learnFuture = EXECUTOR.submit(runnable);
        spreadUpdate();
    }

    @Override
    protected void complete() {
        try {
            learnFuture.get();
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
        return learnFuture != null && !learnFuture.isDone();
    }

    @Override
    public void update(Observable o, Object arg) {
        LearnRunnable runnable = (LearnRunnable) o;
        if (runnable.isFinished()) {
            complete();
        }
        parts = new ArrayList<>(runnable.getProtocolParts());
        spreadUpdate();
    }
}
