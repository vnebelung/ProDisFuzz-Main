/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.protocol;

import model.process.fuzzoptions.Process.InjectionMethod;
import model.protocol.ProtocolBlock.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents the protocol structure, that is the whole protocol including information about the chosen
 * fuzzing options.
 */
public class InjectedProtocolStructure {

    private List<InjectedProtocolBlock> injectedProtocolBlocks;
    private List<InjectedProtocolBlock> varInjectedProtocolBlocks;

    /**
     * Constructs a new injected protocol structure.
     */
    public InjectedProtocolStructure() {
        injectedProtocolBlocks = Collections.emptyList();
        varInjectedProtocolBlocks = Collections.emptyList();
    }

    /**
     * Constructs a new injected protocol structure out of a given protocol structure.
     */
    public InjectedProtocolStructure(ProtocolStructure protocolStructure) {
        injectedProtocolBlocks = new ArrayList<>(protocolStructure.getSize());
        varInjectedProtocolBlocks = new ArrayList<>(protocolStructure.getSize());
        for (int i = 0; i < protocolStructure.getSize(); i++) {
            Type type = protocolStructure.getBlock(i).getType();
            InjectedProtocolBlock injectedProtocolBlock =
                    new InjectedProtocolBlock(type, protocolStructure.getBlock(i).getBytes());
            injectedProtocolBlocks.add(injectedProtocolBlock);
            if (type == Type.VAR) {
                varInjectedProtocolBlocks.add(injectedProtocolBlock);
            }
        }
    }

    /**
     * Calculates the fuzzing iterations for simultaneous data injections.
     *
     * @return the number of iterations, -1 for infinite work
     */
    private int calcWorkSimultaneous() {
        switch (varInjectedProtocolBlocks.get(0).getDataInjection()) {
            case LIBRARY:
                return varInjectedProtocolBlocks.get(0).getNumOfLibraryLines();
            case RANDOM:
                return -1;
            //noinspection UnnecessaryDefault
            default:
                return 0;
        }
    }

    /**
     * Clears the structure from any defined protocol blocks.
     */
    public void clear() {
        injectedProtocolBlocks.clear();
        varInjectedProtocolBlocks.clear();
    }

    /**
     * Returns the number of protocol blocks.
     *
     * @return the number of protocol blocks
     */
    public int getSize() {
        return injectedProtocolBlocks.size();
    }

    /**
     * Returns the protocol block with the given index.
     *
     * @param index the block index
     * @return the protocol block
     */
    public InjectedProtocolBlock getBlock(int index) {
        return injectedProtocolBlocks.get(index);
    }

    /**
     * Returns the variable protocol block with the given index.
     *
     * @param index the block index
     * @return the protocol block
     */
    public InjectedProtocolBlock getVarBlock(int index) {
        return varInjectedProtocolBlocks.get(index);
    }

    /**
     * Returns the number of variable protocol blocks.
     *
     * @return the number of variable protocol blocks
     */
    public int getVarSize() {
        return varInjectedProtocolBlocks.size();
    }

    /**
     * Returns the injected protocol structure transformed to a regular protocol structure.
     *
     * @return the protocol structure
     */
    public ProtocolStructure toProtocolStructure() {
        ProtocolStructure result = new ProtocolStructure();
        for (InjectedProtocolBlock each : injectedProtocolBlocks) {
            result.addBlock(Arrays.asList(each.getBytes()));
        }
        return result;
    }

    /**
     * Creates a deep copy of the object.
     *
     * @return the copied object
     */
    public InjectedProtocolStructure copy() {
        InjectedProtocolStructure result = new InjectedProtocolStructure();

        result.injectedProtocolBlocks = new ArrayList<>(injectedProtocolBlocks.size());
        result.injectedProtocolBlocks
                .addAll(injectedProtocolBlocks.stream().map(InjectedProtocolBlock::copy).collect(Collectors.toList()));

        result.varInjectedProtocolBlocks = new ArrayList<>(varInjectedProtocolBlocks);
        result.varInjectedProtocolBlocks.addAll(varInjectedProtocolBlocks.stream().map(InjectedProtocolBlock::copy)
                .collect(Collectors.toList()));

        return result;
    }

    /**
     * Calculates the fuzzing iterations for separate data injections.
     *
     * @return the number of iterations, -1 for infinite work
     */
    private int calcNumOfSeparateIterations() {
        int result = 0;
        for (InjectedProtocolBlock each : varInjectedProtocolBlocks) {
            switch (each.getDataInjection()) {
                case LIBRARY:
                    result += each.getNumOfLibraryLines();
                    break;
                case RANDOM:
                    return -1;
            }
        }
        return result;
    }

    /**
     * Calculates the number of fuzzing iterations based on the injected protocol structure for the given injection
     * method.
     *
     * @param injectionMethod the injection method
     * @return the number of fuzzing iterations
     */
    public int getNumOfIterations(InjectionMethod injectionMethod) {
        if (varInjectedProtocolBlocks.isEmpty()) {
            return 0;
        }
        switch (injectionMethod) {
            case SEPARATE:
                return calcNumOfSeparateIterations();
            case SIMULTANEOUS:
                return calcWorkSimultaneous();
            default:
                return 0;
        }
    }
}
