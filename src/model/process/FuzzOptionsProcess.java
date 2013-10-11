/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:47.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import model.InjectedProtocolPart;
import model.Model;
import model.ProtocolPart;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FuzzOptionsProcess extends AbstractProcess {

    public final static int PORT_MIN = 1;
    public final static int PORT_MAX = 65535;
    public final static int INTERVAL_MIN = 100;
    public final static int INTERVAL_MAX = 30000;
    public final static int TIMEOUT_MIN = 50;
    public final static int TIMEOUT_MAX = 10000;
    private final List<InjectedProtocolPart> injectedProtocolParts;
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
        injectedProtocolParts = new ArrayList<>();
    }

    @Override
    public void init() {
        for (final ProtocolPart each : Model.INSTANCE.getImportProcess().getProtocolParts()) {
            injectedProtocolParts.add(new InjectedProtocolPart(each));
        }
        target = new InetSocketAddress("", PORT_MIN);
        timeout = 5 * TIMEOUT_MIN;
        interval = 5 * INTERVAL_MIN;
        injectionMethod = FuzzOptionsProcess.InjectionMethod.SIMULTANEOUS;
        saveCommunication = CommunicationSave.CRITICAL;
        targetReachable = false;
        spreadUpdate();
    }

    @Override
    public void reset() {
        injectedProtocolParts.clear();
        target = new InetSocketAddress("", PORT_MIN);
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
     * Sets the data injection method to insert the same values into all fuzzable protocol parts in one fuzzing
     * iteration.
     */
    public void setSimultaneousInjectionMode() {
        if (injectionMethod == FuzzOptionsProcess.InjectionMethod.SIMULTANEOUS) {
            return;
        }
        injectionMethod = FuzzOptionsProcess.InjectionMethod.SIMULTANEOUS;
        Model.INSTANCE.getLogger().info("Injection mode set to " + FuzzOptionsProcess.InjectionMethod.SIMULTANEOUS
                .toString());
        spreadUpdate();
    }

    /**
     * Sets the data injection method to insert different values into all fuzzable protocol parts in one fuzzing
     * iteration.
     */
    public void setSeparateInjectionMethod() {
        if (injectionMethod == FuzzOptionsProcess.InjectionMethod.SEPARATE) {
            return;
        }
        injectionMethod = FuzzOptionsProcess.InjectionMethod.SEPARATE;
        final List<InjectedProtocolPart> varParts = filterVarParts(injectedProtocolParts);
        for (int i = 0; i < varParts.size(); i++) {
            if (i > 0) {
                setLibraryFile(Paths.get(""), varParts.get(i).hashCode());
            }
        }

        Model.INSTANCE.getLogger().info("Injection mode set to " + FuzzOptionsProcess.InjectionMethod.SEPARATE
                .toString());
        spreadUpdate();
    }

    /**
     * All communication exchanged between ProDisFuzz and the target will be saved,
     * no matter whether the data triggers a crash.
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
    public void setTarget(final String address, final int port) {
        if (target.getHostString().equals(address) && target.getPort() == port) {
            return;
        }
        int tmp = Math.max(port, PORT_MIN);
        tmp = Math.min(tmp, PORT_MAX);
        target = new InetSocketAddress(address, tmp);
        try (Socket socket = new Socket()) {
            // Establish a test connection without sending any data
            socket.setSoTimeout(timeout);
            socket.connect(target, timeout);
            targetReachable = true;
            Model.INSTANCE.getLogger().fine("Target '" + target.getHostString() + ":" + target.getPort() + "' is " +
                    "reachable");
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error("Target '" + target.getHostString() + ":" + target.getPort() + "' is not" +
                    " " +
                    "reachable");
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
        return new InetSocketAddress(target.getHostName(), target.getPort());
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
    public void setInterval(final int interval) {
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
    public void setTimeout(final int timeout) {
        if (timeout == this.timeout) {
            return;
        }
        this.timeout = Math.max(timeout, TIMEOUT_MIN);
        this.timeout = Math.min(this.timeout, TIMEOUT_MAX);
        Model.INSTANCE.getLogger().info("Timeout set to " + this.timeout + " ms");
        spreadUpdate();
    }

    /**
     * Returns the injected protocol parts. Each part has its particular fuzz options.
     *
     * @return the injected protocol parts
     */
    public List<InjectedProtocolPart> getInjectedProtocolParts() {
        return Collections.unmodifiableList(injectedProtocolParts);
    }

    /**
     * Sets the data injection method to library-based for a given variable protocol part identified through its hash
     * code.
     *
     * @param hash the hashCode of the protocol part
     */
    public void setLibraryInjectionForPart(final int hash) {
        switch (injectionMethod) {
            case SEPARATE:
                for (final InjectedProtocolPart each : injectedProtocolParts) {
                    if (each.hashCode() == hash) {
                        if (each.setLibraryInjection()) {
                            Model.INSTANCE.getLogger().info("Data Injection method of protocol part '" + Integer
                                    .toHexString(hash) + "' set to " + InjectedProtocolPart.DataInjectionMethod
                                    .LIBRARY);
                            spreadUpdate();
                        }
                        break;
                    }
                }
                break;
            case SIMULTANEOUS:
                boolean hasChanged = false;
                for (final InjectedProtocolPart each : injectedProtocolParts) {
                    if (each.setLibraryInjection()) {
                        hasChanged = true;
                    }
                }
                if (hasChanged) {
                    Model.INSTANCE.getLogger().info("Data Injection method of all protocol parts set to " +
                            InjectedProtocolPart.DataInjectionMethod.LIBRARY);
                    spreadUpdate();
                }
                break;
            default:
                break;
        }

    }

    /**
     * Sets the library file for a given protocol part identified through its hash code.
     *
     * @param p    the path to the library file
     * @param hash the hash code of the protocol part
     */
    public void setLibraryFile(final Path p, final int hash) {
        switch (injectionMethod) {
            case SEPARATE:
                for (final InjectedProtocolPart each : injectedProtocolParts) {
                    if (each.hashCode() == hash) {
                        each.setLibrary(p);
                        if (each.getLibrary() != null) {
                            Model.INSTANCE.getLogger().info("Library file of protocol part '" + Integer.toHexString
                                    (hash) +
                                    "' set to '" + each.getLibrary().toString() + "'");
                        }
                        spreadUpdate();
                        break;
                    }
                }
                break;
            case SIMULTANEOUS:
                for (final InjectedProtocolPart each : injectedProtocolParts) {
                    each.setLibrary(p);
                }
                for (final InjectedProtocolPart each : filterVarParts(injectedProtocolParts)) {
                    if (each.getLibrary() != null) {
                        Model.INSTANCE.getLogger().info("Library file of all protocol parts set to '" +
                                each.getLibrary().toString() + "'");
                        break;
                    }
                }
                spreadUpdate();
                break;
            default:
                break;
        }

    }

    /**
     * Sets the data injection method to random-based for a given variable protocol part identified through its hash
     * code.
     *
     * @param hash the hashCode of the protocol part
     */

    public void setRandomInjectionForPart(final int hash) {
        switch (injectionMethod) {
            case SEPARATE:
                for (final InjectedProtocolPart each : injectedProtocolParts) {
                    if (each.hashCode() == hash) {
                        if (each.setRandomInjection()) {
                            Model.INSTANCE.getLogger().info("Data Injection method of protocol part '" + Integer
                                    .toHexString(hash) + "' set to " + InjectedProtocolPart.DataInjectionMethod.RANDOM);
                            spreadUpdate();
                        }
                        break;
                    }
                }
                break;
            case SIMULTANEOUS:
                boolean hasChanged = false;
                for (final InjectedProtocolPart each : injectedProtocolParts) {
                    if (each.setRandomInjection()) {
                        hasChanged = true;
                    }
                }
                if (hasChanged) {
                    Model.INSTANCE.getLogger().info("Data Injection method of all protocol parts set to " +
                            InjectedProtocolPart.DataInjectionMethod.RANDOM);
                    spreadUpdate();
                }
                break;
            default:
                break;
        }
    }

    /**
     * Filters all non-variable protocol parts out of a list of injected protocol parts.
     *
     * @param parts the input injected protocol parts
     * @return the variable injected protocol parts
     */
    public List<InjectedProtocolPart> filterVarParts(final List<InjectedProtocolPart> parts) {
        final List<InjectedProtocolPart> varParts = new ArrayList<>(parts);
        for (int i = parts.size() - 1; i >= 0; i--) {
            if (varParts.get(i).getProtocolPart().getType() != ProtocolPart.Type.VAR) {
                varParts.remove(i);
            }
        }
        return varParts;
    }

    public static enum CommunicationSave {ALL, CRITICAL}

    public static enum InjectionMethod {SIMULTANEOUS, SEPARATE}
}
