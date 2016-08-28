/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import model.Model;
import model.process.AbstractRunner;
import model.process.fuzzoptions.Process.InjectionMethod;
import model.protocol.InjectedProtocolStructure;

import java.nio.file.Path;

/**
 * This class is the separate injection runnable, responsible for setting the data injection method to insert different
 * values into all variable protocol blocks in one fuzzing iteration.
 */
class InjectionMethodRunner extends AbstractRunner {

    private InjectedProtocolStructure injectedProtocolStructure;
    private InjectionMethod injectionMethod;

    /**
     * Constructs a new runner.
     *
     * @param injectedProtocolStructure the input injected protocol structure
     * @param injectionMethod           the injection method@param index the index of the variable protocol block
     */
    public InjectionMethodRunner(InjectedProtocolStructure injectedProtocolStructure, InjectionMethod injectionMethod) {
        super(1);
        this.injectedProtocolStructure = injectedProtocolStructure;
        this.injectionMethod = injectionMethod;
    }

    /**
     * Returns the injection method.
     *
     * @return the injection method
     */
    public InjectionMethod getInjectionMethod() {
        return injectionMethod;
    }

    @Override
    public void run() {
        markStart();

        // Start work unit
        switch (injectionMethod) {
            case SIMULTANEOUS:
                simultaneousInjection();
                break;
            case SEPARATE:
                separateInjection();
                break;
        }
        markFinish();

    }

    /**
     * Updates the injected protocol structure by switching to separate data injection.
     */
    private void separateInjection() {
        for (int i = 1; i < injectedProtocolStructure.getVarSize(); i++) {
            injectedProtocolStructure.getVarBlock(i).setRandomInjection();
            Model.INSTANCE.getLogger().info("Injection mode set to " + InjectionMethod.SEPARATE);
        }
    }

    /**
     * Updates the injected protocol structure by switching to simultaneous data injection.
     */
    private void simultaneousInjection() {
        switch (injectedProtocolStructure.getVarBlock(0).getDataInjection()) {
            case LIBRARY:
                Path library = injectedProtocolStructure.getVarBlock(0).getLibrary();
                for (int i = 1; i < injectedProtocolStructure.getVarSize(); i++) {
                    injectedProtocolStructure.getVarBlock(i).setLibraryInjection();
                    injectedProtocolStructure.getVarBlock(i).setLibrary(library);
                }
                if (injectedProtocolStructure.getVarBlock(0).getLibrary() != null) {
                    Model.INSTANCE.getLogger().info("Library file of all variable protocol blocks set to '" +
                            injectedProtocolStructure.getVarBlock(0).getLibrary() + '\'');
                }
                break;
            case RANDOM:
                for (int i = 1; i < injectedProtocolStructure.getVarSize(); i++) {
                    injectedProtocolStructure.getVarBlock(i).setRandomInjection();
                }
                break;
        }
    }

    /**
     * Returns the updated injected protocol structure.
     *
     * @return the injected protocol structure
     */
    public InjectedProtocolStructure getInjectedProtocolStructure() {
        return injectedProtocolStructure;
    }

}
