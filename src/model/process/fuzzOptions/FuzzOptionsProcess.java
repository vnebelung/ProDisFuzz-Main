/*
 * This file is part of ProDisFuzz, modified on 03.04.14 20:36.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzOptions;

import model.Model;
import model.process.AbstractProcess;
import model.protocol.InjectedProtocolBlock;
import model.protocol.InjectedProtocolStructure;
import model.protocol.ProtocolStructure;

import java.io.IOException;
import java.io.LineNumberReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class FuzzOptionsProcess extends AbstractProcess {

    private final static int INTERVAL_MIN = 100;
    private final static int INTERVAL_MAX = 30000;
    private final static int TIMEOUT_MIN = 50;
    private final static int TIMEOUT_MAX = 10000;
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

    @Override
    public void init() {
        ProtocolStructure protocolStructure = Model.INSTANCE.getImportProcess().getProtocolStructure();
        for (int i = 0; i < protocolStructure.getSize(); i++) {
            injectedProtocolStructure.addBlock(protocolStructure.getBytes(i));
        }
        timeout = 5 * TIMEOUT_MIN;
        interval = 5 * INTERVAL_MIN;
        injectionMethod = FuzzOptionsProcess.InjectionMethod.SIMULTANEOUS;
        saveCommunication = CommunicationSave.CRITICAL;
        targetReachable = false;
        spreadUpdate();
    }

    @Override
    public void reset() {
        injectedProtocolStructure.clear();
        timeout = 5 * TIMEOUT_MIN;
        interval = 5 * INTERVAL_MIN;
        injectionMethod = FuzzOptionsProcess.InjectionMethod.SIMULTANEOUS;
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
        if (injectionMethod == FuzzOptionsProcess.InjectionMethod.SIMULTANEOUS) {
            return;
        }
        injectionMethod = FuzzOptionsProcess.InjectionMethod.SIMULTANEOUS;
        Model.INSTANCE.getLogger().info("Injection mode set to " + FuzzOptionsProcess.InjectionMethod.SIMULTANEOUS
                .toString());
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
            default:
                // Should not happen
                break;
        }
        spreadUpdate();
    }

    /**
     * Sets the data injection method to insert different values into all fuzzable protocol blocks in one fuzzing
     * iteration.
     */
    public void setSeparateInjectionMode() {
        if (injectionMethod == FuzzOptionsProcess.InjectionMethod.SEPARATE) {
            return;
        }
        injectionMethod = FuzzOptionsProcess.InjectionMethod.SEPARATE;
        Model.INSTANCE.getLogger().info("Injection mode set to " + FuzzOptionsProcess.InjectionMethod.SEPARATE
                .toString());
        for (int i = 1; i < injectedProtocolStructure.getVarSize(); i++) {
            setRandomInjection(i);
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
        Model.INSTANCE.getLogger().info("Communication that is saved set to " + CommunicationSave.ALL.toString());
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
        Model.INSTANCE.getLogger().info("Communication that is saved set to " + CommunicationSave.CRITICAL.toString());
        spreadUpdate();
    }

    /**
     * Sets the target address and port.
     *
     * @param address the target address
     * @param port    the target port
     */
    public void setTarget(String address, int port) {
        if (target != null && target.getHostString().equals(address) && target.getPort() == port) {
            return;
        }
        if (address.isEmpty()) {
            address = null;
        }
        try (Socket socket = new Socket()) {
            target = new InetSocketAddress(address, port);
            // Establish a test connection without sending any data
            socket.setSoTimeout(timeout);
            socket.connect(target, timeout);
            targetReachable = true;
            Model.INSTANCE.getLogger().fine("Target '" + target.getHostString() + ":" + target.getPort() + "' is " +
                    "reachable");
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error("Target '" + target.getHostString() + ":" + target.getPort() + "' is" +
                    " not reachable");
            targetReachable = false;
        } catch (IllegalArgumentException e) {
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
     * Returns the injected protocol structure. Each part has its particular fuzz options.
     *
     * @return the injected protocol structure
     */
    public InjectedProtocolStructure getInjectedProtocolStructure() {
        return injectedProtocolStructure;
    }

    /**
     * Sets the data injection to library-based for a given variable protocol block identified through its index.
     *
     * @param index the index of the variable protocol block
     */
    public void setLibraryInjection(int index) {
        if (injectedProtocolStructure.getVarBlock(index).getDataInjectionMethod() == InjectedProtocolBlock
                .DataInjectionMethod.LIBRARY) {
            return;
        }
        if (injectedProtocolStructure.getVarBlock(index).getDataInjectionMethod() == null) {
            return;
        }
        injectedProtocolStructure.getVarBlock(index).setLibraryInjection();
        Model.INSTANCE.getLogger().info("Data Injection method of variable protocol block '" + index + "' set to " +
                InjectedProtocolBlock.DataInjectionMethod.LIBRARY);
        if (injectionMethod == InjectionMethod.SIMULTANEOUS) {
            for (int i = index + 1; i < injectedProtocolStructure.getVarSize(); i++) {
                setLibraryInjection(i);
            }
        }
        spreadUpdate();
    }

    /**
     * Sets the library file path for the injected protocol block identified through the given index to the given value.
     * All other protocol parts library will be set according to the current injection method.
     *
     * @param path  the path to the library file
     * @param index the index of the variable target protocol block
     */
    public void setLibrary(Path path, int index) {
        if (path == injectedProtocolStructure.getVarBlock(index).getLibrary()) {
            return;
        }
        Path newPath = path;
        if (!checkLibrary(newPath)) {
            newPath = null;
        }
        injectedProtocolStructure.getVarBlock(index).setLibrary(newPath);
        if (injectedProtocolStructure.getVarBlock(index).getLibrary() != null) {
            Model.INSTANCE.getLogger().info("Library file of variable protocol block '" + index + "' set to '" +
                    injectedProtocolStructure.getVarBlock(index).getLibrary().toString() + "'");
        }
        if (injectionMethod == InjectionMethod.SIMULTANEOUS) {
            for (int i = index + 1; i < injectedProtocolStructure.getVarSize(); i++) {
                setLibrary(newPath, i);
            }
        }
        spreadUpdate();
    }

    /**
     * Sets the data injection method to random-based for a given protocol block identified through its index in the
     * list of variable protocol blocks.
     *
     * @param index the index of the variable protocol block
     */
    public void setRandomInjection(int index) {
        if (injectedProtocolStructure.getVarBlock(index).getDataInjectionMethod() == InjectedProtocolBlock
                .DataInjectionMethod.RANDOM) {
            return;
        }
        if (injectedProtocolStructure.getVarBlock(index).getDataInjectionMethod() == null) {
            return;
        }
        injectedProtocolStructure.getVarBlock(index).setRandomInjection();
        Model.INSTANCE.getLogger().info("Data Injection method of variable protocol block '" + index + "' set to " +
                InjectedProtocolBlock.DataInjectionMethod.RANDOM);
        if (injectionMethod == InjectionMethod.SIMULTANEOUS) {
            for (int i = index; i < injectedProtocolStructure.getVarSize(); i++) {
                setRandomInjection(i);
            }
        }
        spreadUpdate();
    }

    /**
     * Checks a library file for a valid structure, that means it does not contain any empty lines.
     *
     * @param path the path to the library file
     * @return true, if the library file has a valid format
     */
    private boolean checkLibrary(Path path) {
        if (path == null) {
            return false;
        }
        if (!Files.isRegularFile(path)) {
            Model.INSTANCE.getLogger().error("" + path.toString() + "' is not a regular file");
            return false;
        }
        if (!Files.isReadable(path)) {
            Model.INSTANCE.getLogger().error("Library file '" + path.toString() + "' is not readable");
            return false;
        }
        try (LineNumberReader lineNumberReader = new LineNumberReader(Files.newBufferedReader(path,
                Charset.forName("UTF-8")))) {
            String line;
            while ((line = lineNumberReader.readLine()) != null) {
                if (line.isEmpty()) {
                    Model.INSTANCE.getLogger().error("Library file '" + path.toString() + "' contains empty lines");
                    return false;
                }
            }
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error(e);
            return false;
        }
        return true;
    }

    public static enum CommunicationSave {ALL, CRITICAL}

    public static enum InjectionMethod {SIMULTANEOUS, SEPARATE}
}
