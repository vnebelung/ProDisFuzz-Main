/*
 * This file is part of ProDisFuzz, modified on 03.04.14 19:28.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.protocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InjectedProtocolStructure {

    private final List<InjectedProtocolBlock> injectedProtocolBlocks;
    private final List<InjectedProtocolBlock> varInjectedProtocolBlocks;

    /**
     * Instantiates a new protocol structure representing the whole protocol including information about the chosen
     * fuzzing options.
     */
    public InjectedProtocolStructure() {
        injectedProtocolBlocks = new ArrayList<>();
        varInjectedProtocolBlocks = new ArrayList<>();
    }

    /**
     * Adds a protocol block to the protocol structure. The given bytes must only contain null values or not null
     * values. If mixed null - not null combinations are found, the data block will be silently dropped.
     *
     * @param bytes the bytes
     */
    public void addBlock(Byte[] bytes) {
        boolean fixed = true;
        boolean variable = true;
        for (Byte each : bytes) {
            if (each == null) {
                fixed = false;
            } else {
                variable = false;
            }
        }
        if (!fixed && !variable) {
            return;
        }
        ProtocolBlock.Type type = fixed ? ProtocolBlock.Type.FIX : ProtocolBlock.Type.VAR;
        InjectedProtocolBlock injectedProtocolBlock = new InjectedProtocolBlock(type, bytes);
        injectedProtocolBlocks.add(injectedProtocolBlock);
        if (type == ProtocolBlock.Type.VAR) {
            varInjectedProtocolBlocks.add(injectedProtocolBlock);
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
}
