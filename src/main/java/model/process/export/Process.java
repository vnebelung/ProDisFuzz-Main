/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.export;

import model.process.AbstractProcess;
import model.process.AbstractRunner;
import model.process.AbstractRunner.ExternalState;
import model.protocol.ProtocolStructure;

import java.nio.file.Path;
import java.util.Observable;

/**
 * This class represents the export process, responsible for exporting the protocol structure to a XML file.
 */
public class Process extends AbstractProcess {

    private ProtocolStructure protocolStructure;

    /**
     * Constructs a new export process.
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
     * Initializes the export process.
     *
     * @param protocolStructure the protocol structure
     */
    public void init(ProtocolStructure protocolStructure) {
        this.protocolStructure = protocolStructure;
        spreadUpdate(State.IDLE);
    }

    /**
     * Starts the process for exporting the protocol structure to the given XML file.
     *
     * @param path the export file
     */
    public void exportProtocolStructure(Path path) {
        AbstractRunner runner = new Runner(path, protocolStructure);
        runner.addObserver(this);
        submitToThreadPool(runner);
    }

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
