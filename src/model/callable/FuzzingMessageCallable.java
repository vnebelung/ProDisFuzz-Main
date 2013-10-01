/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:28.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.callable;

import model.InjectedProtocolPart;
import model.Model;
import model.RandomPool;
import model.process.FuzzOptionsProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class FuzzingMessageCallable implements Callable<byte[]> {

    private final List<InjectedProtocolPart> injectedProtocolParts;
    private final FuzzOptionsProcess.InjectionMethod injectionMethod;
    private int currentInjectedProtocolPart;
    private int currentLibraryLine;

    /**
     * Instantiates a new callable.
     *
     * @param injectedProtocolParts the injected protocol parts
     * @param injectionMethod       the injection method
     */
    public FuzzingMessageCallable(final List<InjectedProtocolPart> injectedProtocolParts,
                                  final FuzzOptionsProcess.InjectionMethod injectionMethod) {
        this.injectedProtocolParts = injectedProtocolParts;
        this.injectionMethod = injectionMethod;
        currentInjectedProtocolPart = 0;
        currentLibraryLine = 0;
    }

    @Override
    public byte[] call() throws Exception {
        List<Byte> bytes = null;
        if (finiteIterations()) {
            switch (injectionMethod) {
                case SEPARATE:
                    bytes = sepFinMessage();
                    break;
                case SIMULTANEOUS:
                    bytes = simFinMessage();
                    break;
                default:
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
                default:
                    break;
            }
        }
        if (bytes == null) {
            return null;
        }
        byte[] message = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            message[i] = bytes.get(i);
        }
        return message;
    }

    /**
     * Generates a fuzzed message. All variable protocol parts have separate random input.
     *
     * @return the generated message
     */
    private List<Byte> sepInfMessage() {
        final List<Byte> bytes = new ArrayList<>();
        // Generates the fuzzed string separate for every single protocol part
        for (final InjectedProtocolPart injectedProtocolPart : injectedProtocolParts) {
            switch (injectedProtocolPart.getProtocolPart().getType()) {
                case FIX:
                    bytes.addAll(injectedProtocolPart.getProtocolPart().getBytes());
                    break;
                case VAR:
                    switch (injectedProtocolPart.getDataInjectionMethod()) {
                        case LIBRARY:
                            bytes.addAll(injectedProtocolPart.getRandomLibraryLine());
                            break;
                        case RANDOM:
                            bytes.addAll(RandomPool.getInstance().nextBloatBytes(injectedProtocolPart.getProtocolPart
                                    ().getMaxLength()));
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
        return bytes;
    }

    /**
     * Generates a fuzzed message. All variable protocol parts get the same random input.
     *
     * @return the generated message
     */
    private List<Byte> simInfMessage() {
        final List<Byte> bytes = new ArrayList<>();
        // Generate the random bytes
        int maxLength = 0;
        for (final InjectedProtocolPart injectedProtocolPart : injectedProtocolParts) {
            maxLength = Math.max(maxLength, injectedProtocolPart.getProtocolPart().getMaxLength());
        }
        final List<Byte> rndBytes = RandomPool.getInstance().nextBloatBytes(maxLength);
        // Apply the bytes for each VAR part
        for (final InjectedProtocolPart injectedProtocolPart : injectedProtocolParts) {
            switch (injectedProtocolPart.getProtocolPart().getType()) {
                case FIX:
                    bytes.addAll(injectedProtocolPart.getProtocolPart().getBytes());
                    break;
                case VAR:
                    bytes.addAll(rndBytes);
                    break;
                default:
                    break;
            }
        }
        return bytes;
    }

    /**
     * Generates a fuzzed message. All variable protocol parts have separate library files.
     *
     * @return the generated message or null if all iterations are done
     */
    private List<Byte> sepFinMessage() {
        if (currentLibraryLine >= Model.getInstance().getFuzzOptionsProcess().filterVarParts(injectedProtocolParts)
                .get(currentInjectedProtocolPart).getNumOfLibraryLines()) {
            currentLibraryLine = 0;
            currentInjectedProtocolPart++;
        }
        if (currentInjectedProtocolPart >= Model.getInstance().getFuzzOptionsProcess().filterVarParts
                (injectedProtocolParts).size()) {
            return null;
        }
        final List<Byte> bytes = new ArrayList<>();
        // For every protocol part other than the current read a random line of its library file
        for (final InjectedProtocolPart injectedProtocolPart : injectedProtocolParts) {
            switch (injectedProtocolPart.getProtocolPart().getType()) {
                case FIX:
                    bytes.addAll(injectedProtocolPart.getProtocolPart().getBytes());
                    break;
                case VAR:
                    bytes.addAll(injectedProtocolPart.equals(Model.getInstance().getFuzzOptionsProcess()
                            .filterVarParts(injectedProtocolParts).get(currentInjectedProtocolPart)) ?
                            injectedProtocolPart.getLibraryLine(currentLibraryLine) : injectedProtocolPart
                            .getRandomLibraryLine());
                    break;
                default:
                    break;
            }
        }
        currentLibraryLine++;
        return bytes;
    }

    /**
     * Generates a fuzzed message. All variable protocol parts have the same library file.
     *
     * @return the generated message or null if all iterations are done
     */
    private List<Byte> simFinMessage() {
        if (currentLibraryLine >= Model.getInstance().getFuzzOptionsProcess().filterVarParts(injectedProtocolParts)
                .get(0).getNumOfLibraryLines()) {
            return null;
        }
        final List<Byte> bytes = new ArrayList<>();
        final List<Byte> line = Model.getInstance().getFuzzOptionsProcess().filterVarParts(injectedProtocolParts).get
                (0).getLibraryLine(currentLibraryLine);
        for (final InjectedProtocolPart injectedProtocolPart : injectedProtocolParts) {
            switch (injectedProtocolPart.getProtocolPart().getType()) {
                case FIX:
                    bytes.addAll(injectedProtocolPart.getProtocolPart().getBytes());
                    break;
                case VAR:
                    bytes.addAll(line);
                    break;
                default:
                    break;
            }
        }
        currentLibraryLine++;
        return bytes;
    }

    /**
     * Checks whether the number of fuzzing iterations will be finite, that means all variable protocol parts are set
     * to LIBRARY.
     *
     * @return true if all variable protocol parts use library files
     */
    private boolean finiteIterations() {
        for (final InjectedProtocolPart injectedProtocolPart : Model.getInstance().getFuzzOptionsProcess()
                .filterVarParts(injectedProtocolParts)) {
            if (injectedProtocolPart.getDataInjectionMethod() == InjectedProtocolPart.DataInjectionMethod.RANDOM) {
                return false;
            }
        }
        return true;
    }

}
