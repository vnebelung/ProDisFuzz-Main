/*
 * This file is part of ProDisFuzz, modified on 08.02.14 23:13.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import model.InjectedProtocolPart;
import model.Model;
import model.RandomPool;
import model.process.fuzzOptions.FuzzOptionsProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

class FuzzingMessageCallable implements Callable<byte[]> {

    private final List<InjectedProtocolPart> parts;
    private final FuzzOptionsProcess.InjectionMethod injectionMethod;
    private int currentInjectedProtocolPart;
    private int currentLibraryLine;

    /**
     * Instantiates a new callable that is responsible for generating fuzzed messages.
     *
     * @param parts the injected protocol parts that define the protocol structure
     * @param im    the injection method the user-chosen injection method
     */
    public FuzzingMessageCallable(List<InjectedProtocolPart> parts, FuzzOptionsProcess.InjectionMethod im) {
        this.parts = parts;
        this.injectionMethod = im;
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
     * Generates a fuzzed message. All variable protocol parts will get separate random data.
     *
     * @return the generated fuzzed message
     */
    private List<Byte> sepInfMessage() {
        List<Byte> bytes = new ArrayList<>();
        // Generates the fuzzed string separate for every single protocol part
        for (InjectedProtocolPart each : parts) {
            switch (each.getProtocolPart().getType()) {
                case FIX:
                    bytes.addAll(each.getProtocolPart().getBytes());
                    break;
                case VAR:
                    switch (each.getDataInjectionMethod()) {
                        case LIBRARY:
                            bytes.addAll(each.getRandomLibraryLine());
                            break;
                        case RANDOM:
                            bytes.addAll(RandomPool.getInstance().nextBloatBytes(each.getProtocolPart().getMaxLength
                                    ()));
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
     * Generates a fuzzed message. All variable protocol parts will get the same random data.
     *
     * @return the generated message
     */
    private List<Byte> simInfMessage() {
        List<Byte> bytes = new ArrayList<>();
        // Generate the random bytes
        int maxLength = 0;
        for (InjectedProtocolPart each : parts) {
            maxLength = Math.max(maxLength, each.getProtocolPart().getMaxLength());
        }
        List<Byte> rndBytes = RandomPool.getInstance().nextBloatBytes(maxLength);
        // Apply the bytes for each VAR part
        for (InjectedProtocolPart each : parts) {
            switch (each.getProtocolPart().getType()) {
                case FIX:
                    bytes.addAll(each.getProtocolPart().getBytes());
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
     * Generates a fuzzed message. All variable protocol parts will get separate data of a library file.
     *
     * @return the generated message or null if all iterations are done
     */
    private List<Byte> sepFinMessage() {
        if (currentLibraryLine >= Model.INSTANCE.getFuzzOptionsProcess().filterVarParts(parts).get
                (currentInjectedProtocolPart).getNumOfLibraryLines()) {
            currentLibraryLine = 0;
            currentInjectedProtocolPart++;
        }
        if (currentInjectedProtocolPart >= Model.INSTANCE.getFuzzOptionsProcess().filterVarParts(parts).size()) {
            return null;
        }
        List<Byte> bytes = new ArrayList<>();
        // For every protocol part other than the current read a random line of its library file
        for (InjectedProtocolPart each : parts) {
            switch (each.getProtocolPart().getType()) {
                case FIX:
                    bytes.addAll(each.getProtocolPart().getBytes());
                    break;
                case VAR:
                    bytes.addAll(each.equals(Model.INSTANCE.getFuzzOptionsProcess().filterVarParts(parts).get
                            (currentInjectedProtocolPart)) ? each.getLibraryLine(currentLibraryLine) : each
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
     * Generates a fuzzed message. All variable protocol parts will get the same data of a library file.
     *
     * @return the generated message or null if all iterations are done
     */
    private List<Byte> simFinMessage() {
        if (currentLibraryLine >= Model.INSTANCE.getFuzzOptionsProcess().filterVarParts(parts).get(0)
                .getNumOfLibraryLines()) {
            return null;
        }
        List<Byte> bytes = new ArrayList<>();
        List<Byte> line = Model.INSTANCE.getFuzzOptionsProcess().filterVarParts(parts).get(0).getLibraryLine
                (currentLibraryLine);
        for (InjectedProtocolPart each : parts) {
            switch (each.getProtocolPart().getType()) {
                case FIX:
                    bytes.addAll(each.getProtocolPart().getBytes());
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
     * Checks whether the number of fuzzing iterations will be finite, that means all variable protocol parts will
     * get their data from a library file.
     *
     * @return true if all variable protocol parts use library files
     */
    private boolean finiteIterations() {
        for (InjectedProtocolPart each : Model.INSTANCE.getFuzzOptionsProcess().filterVarParts(parts)) {
            if (each.getDataInjectionMethod() == InjectedProtocolPart.DataInjectionMethod.RANDOM) {
                return false;
            }
        }
        return true;
    }

}
