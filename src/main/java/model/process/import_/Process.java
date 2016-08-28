/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.import_;

import model.process.AbstractProcess;
import model.process.AbstractRunner;
import model.process.AbstractRunner.ExternalState;
import model.protocol.ProtocolStructure;

import java.nio.file.Path;
import java.util.Observable;

/**
 * This class is the import process, responsible for importing the XML file to generate the protocol structure.
 */
public class Process extends AbstractProcess {

    private ProtocolStructure protocolStructure;

    /**
     * Constructs a new import process.
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
     * Imports an XML file containing the protocol structure.
     *
     * @param path the path to the XML file
     */
    public void importProtocolStructure(Path path) {
        AbstractRunner runner = new Runner(path);
        runner.addObserver(this);
        submitToThreadPool(runner);
    }

    /**
     * Returns the protocol structure imported from an XML file.
     *
     * @return the protocol structure
     */
    public ProtocolStructure getProtocolStructure() {
        return protocolStructure;
    }

    @Override
    public void update(Observable o, Object arg) {
        Runner runner = (Runner) o;
        ExternalState state = (ExternalState) arg;
        protocolStructure = runner.getProtocolStructure();
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
