/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.Model;
import model.process.AbstractProcess;
import model.process.AbstractRunner;
import model.process.AbstractRunner.ExternalState;
import model.protocol.ProtocolFile;
import model.protocol.ProtocolStructure;

import java.util.Observable;
import java.util.Set;

/**
 * This class is the learn process, responsible for learning the protocol structure.
 */
public class Process extends AbstractProcess {

    private ProtocolStructure protocolStructure;

    /**
     * Constructs a learn process.
     */
    public Process() {
        super();
        protocolStructure = new ProtocolStructure();
    }

    @Override
    public void reset() {
        super.reset();
        protocolStructure.clear();
        spreadUpdate(State.IDLE);
    }

    /**
     * Starts the process for learning a protocol structure.
     *
     * @param protocolFiles the protocol files used to learn the protocol structure
     */
    public void learnProtocolStructure(Set<ProtocolFile> protocolFiles) {
        AbstractRunner runner = new Runner(protocolFiles);
        runner.addObserver(this);
        submitToThreadPool(runner);
        Model.INSTANCE.getLogger().info("Learn process started");
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
    public void update(Observable o, Object arg) {
        ExternalState state = (ExternalState) arg;
        Runner runner = (Runner) o;
        switch (state) {
            case IDLE:
                spreadUpdate(State.IDLE);
                break;
            case RUNNING:
                protocolStructure = runner.getProtocolStructure();
                spreadUpdate(State.RUNNING);
                break;
            case FINISHED:
                protocolStructure = runner.getProtocolStructure();
                spreadUpdate(State.IDLE);
                break;
        }
    }

}
