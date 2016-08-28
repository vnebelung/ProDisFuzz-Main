/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import model.Model;
import model.process.AbstractRunner;
import model.process.fuzzoptions.Process.InjectionMethod;
import model.protocol.InjectedProtocolBlock.DataInjection;
import model.protocol.InjectedProtocolStructure;

/**
 * This class is the data injection runnable, responsible for setting the injection data for variable protocol blocks.
 */
class InjectionDataRunner extends AbstractRunner {

    private InjectedProtocolStructure injectedProtocolStructure;
    private DataInjection dataInjection;
    private InjectionMethod injectionMethod;
    private int index;

    /**
     * Constructs a new runner.
     *
     * @param index                     the index of the variable protocol block
     * @param dataInjection             the data injection method
     * @param injectedProtocolStructure the input injected protocol structure
     * @param injectionMethod           the injection method
     */
    protected InjectionDataRunner(InjectedProtocolStructure injectedProtocolStructure, DataInjection dataInjection,
                                  InjectionMethod injectionMethod, int index) {
        super(1);
        this.injectedProtocolStructure = injectedProtocolStructure;
        this.dataInjection = dataInjection;
        this.injectionMethod = injectionMethod;
        this.index = index;
    }

    @Override
    public void run() {
        markStart();

        // Start work unit
        switch (dataInjection) {
            case RANDOM:
                randomDataInjection();
                break;
            case LIBRARY:
                libraryInjection();
                break;
        }
        markFinish();
    }

    /**
     * Updates the injected protocol structure by switching on or more protocol blocks to library-based injection.
     */
    private void libraryInjection() {
        switch (injectionMethod) {
            case SIMULTANEOUS:
                for (int i = 0; i < injectedProtocolStructure.getVarSize(); i++) {
                    injectedProtocolStructure.getVarBlock(i).setLibraryInjection();
                }
                Model.INSTANCE.getLogger()
                        .info("Data Injection method of all variable protocol blocks set to " + DataInjection.LIBRARY);
                break;
            case SEPARATE:
                injectedProtocolStructure.getVarBlock(index).setLibraryInjection();
                Model.INSTANCE.getLogger()
                        .info("Data Injection method of variable protocol block #" + index + " set to " +
                                DataInjection.LIBRARY);
                break;
        }
    }

    /**
     * Updates the injected protocol structure by switching on or more protocol blocks to random-based injection.
     */
    private void randomDataInjection() {
        switch (injectionMethod) {
            case SIMULTANEOUS:
                for (int i = 0; i < injectedProtocolStructure.getVarSize(); i++) {
                    injectedProtocolStructure.getVarBlock(i).setRandomInjection();
                }
                Model.INSTANCE.getLogger()
                        .info("Data Injection method of all variable protocol blocks set to " + DataInjection.RANDOM);
                break;
            case SEPARATE:
                injectedProtocolStructure.getVarBlock(index).setRandomInjection();
                Model.INSTANCE.getLogger()
                        .info("Data Injection method of variable protocol block #" + index + " set to " +
                                DataInjection.RANDOM);
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
