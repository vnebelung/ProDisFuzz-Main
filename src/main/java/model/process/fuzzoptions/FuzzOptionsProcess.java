/*
 * This file is part of ProDisFuzz, modified on 01.07.15 00:18.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import model.Model;
import model.process.AbstractProcess;
import model.protocol.InjectedProtocolBlock.DataInjectionMethod;
import model.protocol.InjectedProtocolStructure;
import model.protocol.ProtocolStructure;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;

public class FuzzOptionsProcess extends AbstractProcess {

    private static final int INTERVAL_MIN = 100;
    private static final int INTERVAL_MAX = 30000;
    private static final int TIMEOUT_MIN = 50;
    private static final int TIMEOUT_MAX = 10000;
    private final InjectedProtocolStructure injectedProtocolStructure;
    private InjectionMethod injectionMethod;
    private InetSocketAddress target;
    private int timeout;
    private int interval;
    private CommunicationSave saveCommunication;
    private boolean targetReachable;

    /**
     * Instantiates a new process responsible for setting all fuzz options.
     */
    public FuzzOptionsProcess() {
        super();
        injectedProtocolStructure = new InjectedProtocolStructure();
    }

    public void init(ProtocolStructure protocolStructure) {
        for (int i = 0; i < protocolStructure.getSize(); i++) {
            injectedProtocolStructure.addBlock(protocolStructure.getBlock(i).getBytes());
        }
        timeout = 5 * TIMEOUT_MIN;
        interval = 5 * INTERVAL_MIN;
        injectionMethod = InjectionMethod.SIMULTANEOUS;
        saveCommunication = CommunicationSave.CRITICAL;
        targetReachable = false;
        spreadUpdate();
    }

    @Override
    public void reset() {
        injectedProtocolStructure.clear();
        timeout = 5 * TIMEOUT_MIN;
        interval = 5 * INTERVAL_MIN;
        injectionMethod = InjectionMethod.SIMULTANEOUS;
        saveCommunication = CommunicationSave.CRITICAL;
        targetReachable = false;
        spreadUpdate();
    }

    /**
     * Returns the data injection method. The injection method specifies the mechanism data is injected into the
     * variable protocol parts
     *
     * @return the injection method
     */
    public InjectionMethod getInjectionMethod() {
        return injectionMethod;
    }

    /**
     * Returns the setting to save the communication data.
     *
     * @return the communication save method
     */
    public CommunicationSave getSaveCommunication() {
        return saveCommunication;
    }

    /**
     * Sets the data injection method to insert the same values into all fuzzable protocol blocks in one fuzzing
     * iteration.
     */
    public void setSimultaneousInjectionMode() {
        if (injectionMethod == InjectionMethod.SIMULTANEOUS) {
            return;
        }
        injectionMethod = InjectionMethod.SIMULTANEOUS;
        Model.INSTANCE.getLogger().info("Injection mode set to " + InjectionMethod.SIMULTANEOUS);
        switch (injectedProtocolStructure.getVarBlock(0).getDataInjectionMethod()) {
            case LIBRARY:
                for (int i = 1; i < injectedProtocolStructure.getVarSize(); i++) {
                    setLibraryInjection(i);
                    setLibrary(injectedProtocolStructure.getVarBlock(0).getLibrary(), i);
                }
                break;
            case RANDOM:
                for (int i = 1; i < injectedProtocolStructure.getVarSize(); i++) {
                    setRandomInjection(i);
                }
                break;
        }
        spreadUpdate();
    }

    /**
     * Sets the data injection method to insert different values into all variable protocol blocks in one fuzzing
     * iteration. All variable protocol blocks except the first one are set to random-based. The first block keeps
     * its data injection.
     */
    public void setSeparateInjectionMode() {
        if (injectionMethod == InjectionMethod.SEPARATE) {
            return;
        }
        injectionMethod = InjectionMethod.SEPARATE;
        Model.INSTANCE.getLogger().info("Injection mode set to " + InjectionMethod.SEPARATE);
        for (int i = 1; i < injectedProtocolStructure.getVarSize(); i++) {
            setSingleRandomInjection(i);
        }
        spreadUpdate();
    }

    /**
     * All communication exchanged between ProDisFuzz and the target will be saved, no matter whether the data triggers
     * a crash.
     */
    public void setSaveAllCommunication() {
        if (saveCommunication == CommunicationSave.ALL) {
            return;
        }
        saveCommunication = CommunicationSave.ALL;
        Model.INSTANCE.getLogger().info("Communication that is saved set to " + CommunicationSave.ALL);
        spreadUpdate();
    }

    /**
     * Only critical communication data will be saved, that is only data that leads to a crash in the target program.
     */
    public void setSaveCriticalCommunication() {
        if (saveCommunication == CommunicationSave.CRITICAL) {
            return;
        }
        saveCommunication = CommunicationSave.CRITICAL;
        Model.INSTANCE.getLogger().info("Communication that is saved set to " + CommunicationSave.CRITICAL);
        spreadUpdate();
    }

    /**
     * Sets the target's address and port.
     *
     * @param address the target's address
     * @param port    the target's port
     */
    public void setTarget(String address, int port) {
        if (target != null && target.getHostString().equals(address) && target.getPort() == port) {
            return;
        }
        if (address.isEmpty()) {
            //noinspection AssignmentToMethodParameter
            address = null;
        }
        //noinspection OverlyBroadCatchBlock
        try (Socket socket = new Socket()) {
            //noinspection ConstantConditions
            target = new InetSocketAddress(address, port);
            // Establish a test connection without sending any data
            socket.setSoTimeout(timeout);
            socket.connect(target, timeout);
            targetReachable = true;
            Model.INSTANCE.getLogger().fine("Target '" + target.getHostString() + ':' + target.getPort() + "' is " +
                    "reachable");
        } catch (IOException ignored) {
            Model.INSTANCE.getLogger().error("Target '" + target.getHostString() + ':' + target.getPort() + "' is" +
                    " not reachable");
            targetReachable = false;
        } catch (IllegalArgumentException ignored) {
            Model.INSTANCE.getLogger().error("Target has invalid syntax");
            target = null;
            targetReachable = false;
        }
        spreadUpdate();
    }

    /**
     * Returns whether the use-defined target is reachable.
     *
     * @return true if the target is reachable
     */
    public boolean isTargetReachable() {
        return targetReachable;
    }

    /**
     * Returns the fuzzing target.
     *
     * @return the target
     */
    public InetSocketAddress getTarget() {
        InetSocketAddress result = null;
        if (target != null) {
            result = new InetSocketAddress(target.getHostName(), target.getPort());
        }
        return result;
    }

    /**
     * Returns the fuzzing interval, that is the pause between two fuzzing iterations.
     *
     * @return the interval in ms
     */
    public int getInterval() {
        return interval;
    }

    /**
     * Sets the fuzzing interval, that is the time between two fuzzing iterations.
     *
     * @param interval the fuzzing interval in ms
     */
    public void setInterval(int interval) {
        if (interval == this.interval) {
            return;
        }
        this.interval = Math.max(interval, INTERVAL_MIN);
        this.interval = Math.min(this.interval, INTERVAL_MAX);
        Model.INSTANCE.getLogger().info("Interval set to " + this.interval + " ms");
        spreadUpdate();
    }

    /**
     * Returns the timeout, that is the time in which a target application must react on input data before a crash
     * occurrence is assumed.
     *
     * @return the timeout in ms
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * Sets the connection timeout, that is the time in which a target application must react on input data before a
     * crash occurrence is assumed.
     *
     * @param timeout the connection timeout in ms
     */
    public void setTimeout(int timeout) {
        if (timeout == this.timeout) {
            return;
        }
        this.timeout = Math.max(timeout, TIMEOUT_MIN);
        this.timeout = Math.min(this.timeout, TIMEOUT_MAX);
        Model.INSTANCE.getLogger().info("Timeout set to " + this.timeout + " ms");
        spreadUpdate();
    }

    /**
     * Returns the injected protocol structure. Each block has its particular fuzz options.
     *
     * @return the injected protocol structure
     */
    public InjectedProtocolStructure getInjectedProtocolStructure() {
        return injectedProtocolStructure;
    }

    /**
     * Sets the data injection to library-based for a given variable protocol block identified through its index. If the
     * injection method is set to SIMULTANEOUS all variable protocol parts will be set library-based regardless of the
     * given index.
     *
     * @param index the index of the variable protocol block
     */
    public void setLibraryInjection(int index) {
        switch (injectionMethod) {
            case SIMULTANEOUS:
                for (int i = 0; i < injectedProtocolStructure.getVarSize(); i++) {
                    setSingleLibraryInjection(i);
                }
                break;
            case SEPARATE:
                setSingleLibraryInjection(index);
                break;
        }
    }

    /**
     * Sets the data injection to library-based for a given index of a variable protocol part.
     *
     * @param index the index of the variable protocol block
     */
    private void setSingleLibraryInjection(int index) {
        if (injectedProtocolStructure.getVarBlock(index).getDataInjectionMethod() == DataInjectionMethod.LIBRARY) {
            return;
        }
        injectedProtocolStructure.getVarBlock(index).setLibraryInjection();
        Model.INSTANCE.getLogger().info("Data Injection method of variable protocol block #" + index + " set to " +
                DataInjectionMethod.LIBRARY);
        spreadUpdate();
    }

    /**
     * Sets the library file path for the injected protocol block identified through the given index to the given value.
     * If the injection method is set to SIMULTANEOUS the library of all variable protocol parts will be set regardless
     * of the given index.
     *
     * @param path  the path to the library file
     * @param index the index of the variable target protocol block
     */
    public void setLibrary(Path path, int index) {
        Path newPath = path;
        if (!checkLibrary(newPath)) {
            newPath = null;
        }
        switch (injectionMethod) {
            case SIMULTANEOUS:
                for (int i = 0; i < injectedProtocolStructure.getVarSize(); i++) {
                    setSingleLibrary(newPath, i);
                }
                break;
            case SEPARATE:
                setSingleLibrary(newPath, index);
                break;
        }
    }

    /**
     * Sets the library file path for an injected protocol block identified through the given index to the given value.
     *
     * @param path  the path to the library file
     * @param index the index of the variable target protocol block
     */
    private void setSingleLibrary(Path path, int index) {
        if (path.equals(injectedProtocolStructure.getVarBlock(index).getLibrary())) {
            return;
        }
        injectedProtocolStructure.getVarBlock(index).setLibrary(path);
        if (injectedProtocolStructure.getVarBlock(index).getLibrary() != null) {
            Model.INSTANCE.getLogger().info("Library file of variable protocol block #" + index + " set to '" +
                    injectedProtocolStructure.getVarBlock(index).getLibrary() + '\'');
        }
        spreadUpdate();
    }

    /**
     * Sets the data injection method to random-based for a given protocol block identified through its index in the
     * list of variable protocol blocks. If the injection method is set to SIMULTANEOUS all variable protocol parts will
     * be set to random-based regardless of the given index.
     *
     * @param index the index of the variable protocol block
     */
    public void setRandomInjection(int index) {
        switch (injectionMethod) {
            case SEPARATE:
                setSingleRandomInjection(index);
                break;
            case SIMULTANEOUS:
                for (int i = 0; i < injectedProtocolStructure.getVarSize(); i++) {
                    setSingleRandomInjection(i);
                }
                break;
        }
    }

    /**
     * Sets the data injection method to random-based for a given protocol block identified through its index in the
     * list of variable protocol blocks.
     *
     * @param index the index of the variable protocol block
     */
    private void setSingleRandomInjection(int index) {
        if (injectedProtocolStructure.getVarBlock(index).getDataInjectionMethod() == DataInjectionMethod.RANDOM) {
            return;
        }
        injectedProtocolStructure.getVarBlock(index).setRandomInjection();
        Model.INSTANCE.getLogger().info("Data Injection method of variable protocol block '" + index + "' set to " +
                DataInjectionMethod.RANDOM);
        spreadUpdate();
    }

    /**
     * Checks a library file for a valid structure, that means it does not contain any empty lines.
     *
     * @param path the path to the library file
     * @return true, if the library file has a valid format
     */
    private static boolean checkLibrary(Path path) {
        if (path == null) {
            return false;
        }
        if (!Files.isRegularFile(path)) {
            Model.INSTANCE.getLogger().error("'" + path + "' is not a regular file");
            return false;
        }
        if (!Files.isReadable(path)) {
            Model.INSTANCE.getLogger().error("Library file '" + path + "' is not readable");
            return false;
        }
        return true;
    }

    public enum CommunicationSave {ALL, CRITICAL}

    public enum InjectionMethod {SIMULTANEOUS, SEPARATE}
}
