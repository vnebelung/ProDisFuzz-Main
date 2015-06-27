/*
 * This file is part of ProDisFuzz, modified on 28.06.15 01:22.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import model.RandomPool;
import model.process.fuzzOptions.FuzzOptionsProcess.InjectionMethod;
import model.protocol.InjectedProtocolBlock.DataInjectionMethod;
import model.protocol.InjectedProtocolStructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

public class FuzzingMessageCallable implements Callable<byte[]> {

    private final InjectedProtocolStructure injectedProtocolStructure;
    private final InjectionMethod injectionMethod;
    private int currentBlock;
    private int currentLibraryLine;

    /**
     * Instantiates a new callable that is responsible for generating fuzzed messages.
     *
     * @param injectedProtocolStructure the injected protocol blocks that define the protocol structure
     * @param injectionMethod           the injection method the user-chosen injection method
     */
    public FuzzingMessageCallable(InjectedProtocolStructure injectedProtocolStructure, InjectionMethod
            injectionMethod) {
        super();
        this.injectedProtocolStructure = injectedProtocolStructure;
        this.injectionMethod = injectionMethod;
        currentBlock = 0;
        currentLibraryLine = 0;
    }

    @Override
    public byte[] call() {
        List<Byte> bytes = new ArrayList<>();
        if (finiteIterations()) {
            switch (injectionMethod) {
                case SEPARATE:
                    bytes = sepFinMessage();
                    break;
                case SIMULTANEOUS:
                    bytes = simFinMessage();
                    break;
            }
        } else {
            switch (injectionMethod) {
                case SEPARATE:
                    bytes = sepInfMessage();
                    break;
                case SIMULTANEOUS:
                    bytes = simInfMessage();
                    break;
            }
        }
        if (bytes == null) {
            //noinspection ReturnOfNull
            return null;
        }
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }
        return result;
    }

    /**
     * Generates a fuzzed message. All variable protocol blocks will get separate random data.
     *
     * @return the generated fuzzed message
     */
    private List<Byte> sepInfMessage() {
        List<Byte> result = new ArrayList<>();
        // Generates the fuzzed string separate for every single protocol block
        for (int i = 0; i < injectedProtocolStructure.getSize(); i++) {
            switch (injectedProtocolStructure.getBlock(i).getType()) {
                case FIX:
                    result.addAll(Arrays.asList(injectedProtocolStructure.getBlock(i).getBytes()));
                    break;
                case VAR:
                    //noinspection NestedSwitchStatement
                    switch (injectedProtocolStructure.getBlock(i).getDataInjectionMethod()) {
                        case LIBRARY:
                            for (byte each : injectedProtocolStructure.getBlock(i).getRandomLibraryLine()) {
                                result.add(each);
                            }
                            break;
                        case RANDOM:
                            result.addAll(RandomPool.getInstance().nextBloatBytes(injectedProtocolStructure.getBlock
                                    (i).getMaxLength()));
                            break;
                    }
                    break;
            }
        }
        return result;
    }

    /**
     * Generates a fuzzed message. All variable protocol blocks will get the same random data.
     *
     * @return the generated message
     */
    private List<Byte> simInfMessage() {
        List<Byte> result = new ArrayList<>();
        // Generate the random bytes
        int maxLength = 0;
        for (int i = 0; i < injectedProtocolStructure.getSize(); i++) {
            maxLength = Math.max(maxLength, injectedProtocolStructure.getBlock(i).getMaxLength());
        }
        List<Byte> rndBytes = RandomPool.getInstance().nextBloatBytes(maxLength);
        // Apply the bytes for each VAR block
        for (int i = 0; i < injectedProtocolStructure.getSize(); i++) {
            switch (injectedProtocolStructure.getBlock(i).getType()) {
                case FIX:
                    result.addAll(Arrays.asList(injectedProtocolStructure.getBlock(i).getBytes()));
                    break;
                case VAR:
                    result.addAll(rndBytes);
                    break;
            }
        }
        return result;
    }

    /**
     * Generates a fuzzed message. All variable protocol blocks will get separate data of a library file.
     *
     * @return the generated message or null if all iterations are done
     */
    private List<Byte> sepFinMessage() {
        if (currentLibraryLine >= injectedProtocolStructure.getVarBlock(currentBlock).getNumOfLibraryLines()) {
            currentLibraryLine = 0;
            currentBlock++;
        }
        if (currentBlock >= injectedProtocolStructure.getVarSize()) {
            return null;
        }
        List<Byte> result = new ArrayList<>();
        // For every protocol block other than the current read a random line of its library file
        for (int i = 0; i < injectedProtocolStructure.getSize(); i++) {
            switch (injectedProtocolStructure.getBlock(i).getType()) {
                case FIX:
                    result.addAll(Arrays.asList(injectedProtocolStructure.getBlock(i).getBytes()));
                    break;
                case VAR:
                    if (injectedProtocolStructure.getBlock(i).equals(injectedProtocolStructure.getVarBlock
                            (currentBlock))) {
                        for (byte each : injectedProtocolStructure.getBlock(i).getLibraryLine(currentLibraryLine)) {
                            result.add(each);
                        }
                    } else {
                        for (byte each : injectedProtocolStructure.getBlock(i).getRandomLibraryLine()) {
                            result.add(each);
                        }
                    }
                    break;
            }
        }
        currentLibraryLine++;
        return result;
    }

    /**
     * Generates a fuzzed message. All variable protocol blocks will get the same data of a library file.
     *
     * @return the generated message or null if all iterations are done
     */
    private List<Byte> simFinMessage() {
        if (currentLibraryLine >= injectedProtocolStructure.getVarBlock(0).getNumOfLibraryLines()) {
            return null;
        }
        List<Byte> result = new ArrayList<>();
        byte[] line = injectedProtocolStructure.getVarBlock(0).getLibraryLine(currentLibraryLine);
        for (int i = 0; i < injectedProtocolStructure.getSize(); i++) {
            switch (injectedProtocolStructure.getBlock(i).getType()) {
                case FIX:
                    result.addAll(Arrays.asList(injectedProtocolStructure.getBlock(i).getBytes()));
                    break;
                case VAR:
                    for (byte each : line) {
                        result.add(each);
                    }
                    break;
            }
        }
        currentLibraryLine++;
        return result;
    }

    /**
     * Checks whether the number of fuzzing iterations will be finite, that means all variable protocol blocks will get
     * their data from a library file.
     *
     * @return true if all variable protocol blocks use library files
     */
    private boolean finiteIterations() {
        if (injectionMethod == InjectionMethod.SIMULTANEOUS) {
            return injectedProtocolStructure.getVarBlock(0).getDataInjectionMethod() == DataInjectionMethod.LIBRARY;
        } else {
            for (int i = 0; i < injectedProtocolStructure.getVarSize(); i++) {
                if (injectedProtocolStructure.getVarBlock(i).getDataInjectionMethod() == DataInjectionMethod.RANDOM) {
                    return false;
                }
            }
            return true;
        }
    }

}
