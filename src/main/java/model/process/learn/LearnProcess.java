/*
 * This file is part of ProDisFuzz, modified on 31.03.14 19:11.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.Model;
import model.ProtocolFile;
import model.process.AbstractThreadProcess;
import model.protocol.ProtocolStructure;

import java.util.Observable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LearnProcess extends AbstractThreadProcess {

    private ProtocolStructure protocolStructure;
    private LearnRunnable runnable;
    private Future learnFuture;

    /**
     * Instantiates a new process responsible for learning the protocol structure.
     */
    public LearnProcess() {
        super();
        protocolStructure = new ProtocolStructure();
    }

    @Override
    public void reset() {
        protocolStructure.clear();
        spreadUpdate();
    }

    public void init(ProtocolFile[] files) {
        runnable = new LearnRunnable(files);
        runnable.addObserver(this);
        spreadUpdate();
    }

    /**
     * Returns the learned protocol blocks.
     *
     * @return the protocol blocks
     */
    public ProtocolStructure getProtocolStructure() {
        return protocolStructure;
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
        protocolStructure = runnable.getProtocolStructure();
        spreadUpdate();
    }
}
