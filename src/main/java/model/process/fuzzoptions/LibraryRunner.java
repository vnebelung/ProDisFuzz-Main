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
import model.protocol.InjectedProtocolStructure;

import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This class is the library runnable, responsible for setting the library of an injected protocol block.
 */
class LibraryRunner extends AbstractRunner {

    private InjectedProtocolStructure injectedProtocolStructure;
    private InjectionMethod injectionMethod;
    private int index;
    private Path library;

    /**
     * Constructs a new runner.
     *
     * @param index the index of the variable protocol block
     */
    public LibraryRunner(InjectedProtocolStructure injectedProtocolStructure, InjectionMethod injectionMethod,
                         int index, Path library) {
        super(1);
        this.injectedProtocolStructure = injectedProtocolStructure;
        this.injectionMethod = injectionMethod;
        this.index = index;
        this.library = library;
    }

    @Override
    public void run() {
        try {
            markStart();

            // Start work unit
            Path newLibrary = library;
            //noinspection TypeMayBeWeakened
            LibraryChecker libraryChecker = new LibraryChecker(newLibrary);
            Future<Boolean> futureLibraryChecker = submitToThreadPool(libraryChecker);
            boolean validLibrary = futureLibraryChecker.get();
            if (!validLibrary) {
                newLibrary = null;
                Model.INSTANCE.getLogger()
                        .info("Library file is not valid (empty or the lines do not contain hex " + "binary strings)");
            }
            switch (injectionMethod) {
                case SIMULTANEOUS:
                    for (int i = 0; i < injectedProtocolStructure.getVarSize(); i++) {
                        injectedProtocolStructure.getVarBlock(i).setLibrary(newLibrary);
                    }
                    Model.INSTANCE.getLogger().info("Library file of all variable protocols set to '" +
                            injectedProtocolStructure.getVarBlock(index).getLibrary() + '\'');
                    break;
                case SEPARATE:
                    injectedProtocolStructure.getVarBlock(index).setLibrary(newLibrary);
                    Model.INSTANCE.getLogger().info("Library file of variable protocol block #" + index + " set to '" +
                            injectedProtocolStructure.getVarBlock(index).getLibrary() + '\'');
                    break;
            }
            markFinish();
        } catch (InterruptedException ignored) {
            Model.INSTANCE.getLogger().info("Reading directory cancelled");
            markCancel();
        } catch (ExecutionException e) {
            // Should not happen
            Model.INSTANCE.getLogger().error(e);
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
