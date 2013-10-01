/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:28.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.runnable;

import model.InjectedProtocolPart;
import model.ProtocolPart;
import model.SavedDataFile;
import model.callable.FuzzingCheckLibraryCallable;
import model.callable.FuzzingMessageCallable;
import model.callable.FuzzingReconnectCallable;
import model.callable.FuzzingSendCallable;
import model.logger.Logger;
import model.process.AbstractThreadProcess;
import model.process.FuzzOptionsProcess;

import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FuzzingRunnable extends AbstractRunnable {
    private final InetSocketAddress target;
    private final int timeout;
    private final int interval;
    private final List<SavedDataFile> savedDataFiles;
    private final FuzzOptionsProcess.InjectionMethod injectionMethod;
    private final List<InjectedProtocolPart> injectedProtocolParts;
    private final FuzzOptionsProcess.CommunicationSave saveCommunication;
    private long startTime;
    private long endTime;
    private long crashTime;

    /**
     * Instantiates a new fuzzing runnable.
     *
     * @param injectionMethod       the injection method
     * @param injectedProtocolParts the injected protocol parts
     * @param target                the fuzzing target
     * @param timeout               the target timeout
     * @param interval              the fuzzing interval
     * @param saveCommunication     the option to save the fuzzed messages
     */
    public FuzzingRunnable(final FuzzOptionsProcess.InjectionMethod injectionMethod,
                           final List<InjectedProtocolPart> injectedProtocolParts, final InetSocketAddress target,
                           final int timeout, final int interval, final FuzzOptionsProcess.CommunicationSave
            saveCommunication) {
        super();
        finished = false;
        this.injectionMethod = injectionMethod;
        this.injectedProtocolParts = injectedProtocolParts;
        this.target = target;
        this.timeout = timeout;
        this.interval = interval;
        this.saveCommunication = saveCommunication;
        savedDataFiles = new ArrayList<>();
    }

    @Override
    public void run() {
        finished = false;
        startTime = System.currentTimeMillis();
        Future<Boolean> sendFuture = null;
        Future<byte[]> messageFuture = null;
        try {
            final List<InjectedProtocolPart> varLibInjectedProtocolParts = filterVarLibParts(injectedProtocolParts);
            // Check library files
            checkLibraries(varLibInjectedProtocolParts);
            spreadUpdate();

            // The fuzzing begins …
            final FuzzingMessageCallable messageCallable = new FuzzingMessageCallable(injectedProtocolParts,
                    injectionMethod);
            byte[] message;
            int iteration = 0;
            while (true) {
                iteration++;
                messageFuture = AbstractThreadProcess.EXECUTOR.submit(messageCallable);
                message = messageFuture.get();
                if (message == null) {
                    break;
                }
                Logger.getInstance().info("Sending fuzzed message #" + iteration);
                final FuzzingSendCallable sendCallable = new FuzzingSendCallable(message, target, timeout);
                sendFuture = AbstractThreadProcess.EXECUTOR.submit(sendCallable);
                for (int i = 0; i < 3; i++) {
                    if (sendFuture.get()) {
                        if (saveCommunication == FuzzOptionsProcess.CommunicationSave.ALL) {
                            savedDataFiles.add(new SavedDataFile(message, false, System.currentTimeMillis()));
                            savedDataFiles.add(new SavedDataFile(sendCallable.getLastResponse(), false,
                                    System.currentTimeMillis()));
                        }
                        spreadUpdate();
                        break;
                    }
                    handleTimeout(i, message);
                }
                Thread.sleep(interval);
            }
            endTime = System.currentTimeMillis();
            finished = true;
            spreadUpdate();
        } catch (InterruptedException e) {
            if (messageFuture != null) {
                messageFuture.cancel(true);
            }
            if (sendFuture != null) {
                sendFuture.cancel(true);
            }
            finished = true;
        } catch (ExecutionException e) {
            Logger.getInstance().error(e);
        }
    }

    /**
     * Handles the situation when the target does not respond to sent messages and tries to reconnect in case of an
     * error.
     *
     * @param iteration the current iteration of tries to sent a message
     * @param message   the sent message
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void handleTimeout(final int iteration, final byte[] message) throws InterruptedException,
            ExecutionException {
        Future<Boolean> reconnectFuture = null;
        double errorInterval = Math.pow(iteration + 2, 0.75) * interval;
        final DecimalFormat decimalFormat = new DecimalFormat(",##0.0");
        try {
            switch (iteration) {
                case 0:
                    crashTime = System.currentTimeMillis();
                case 1:
                    // Error interval has a logarithmic style curve
                    Logger.getInstance().warning("Target not reachable. Resend message again in " + (decimalFormat
                            .format(errorInterval / 1000)) + " seconds");
                    Thread.sleep((long) errorInterval);
                    break;
                case 2:
                    Logger.getInstance().fine("Target not reachable for 3 times in a row. Information about the " +
                            "crash is being saved");
                    savedDataFiles.add(new SavedDataFile(message, true, crashTime));
                    int count = 1;
                    do {
                        errorInterval = Math.pow(count++, 0.75) * interval;
                        Logger.getInstance().warning("Trying to reconnect to target in " + (decimalFormat.format
                                (errorInterval / 1000)) + " seconds");
                        Thread.sleep((long) errorInterval);
                        // Try again to connect
                        final FuzzingReconnectCallable reconnectCallable = new FuzzingReconnectCallable(target,
                                timeout);
                        reconnectFuture = AbstractThreadProcess.EXECUTOR.submit(reconnectCallable);
                    } while (!reconnectFuture.get());
                    Logger.getInstance().info("Connection to target successfully reestablished");
                    startTime += System.currentTimeMillis() - crashTime;
                    spreadUpdate();
                    break;
                default:
            }
        } catch (InterruptedException e) {
            if (reconnectFuture != null) {
                reconnectFuture.cancel(true);
            }
            throw e;
        }
    }

    /**
     * Checks all library files for a valid structure, that means they do not contain any empty lines.
     *
     * @param varLibInjectedProtocolParts the variable protocol parts with data injection method LIBRARY
     * @throws InterruptedException
     */
    private void checkLibraries(final List<InjectedProtocolPart> varLibInjectedProtocolParts) throws
            InterruptedException, ExecutionException {
        if (varLibInjectedProtocolParts.isEmpty()) {
            return;
        }
        Future<Boolean> checkLibraryFuture = null;
        try {
            switch (injectionMethod) {
                case SEPARATE:
                    for (final InjectedProtocolPart injectedProtocolPart : varLibInjectedProtocolParts) {
                        final FuzzingCheckLibraryCallable checkLibraryCallable = new FuzzingCheckLibraryCallable
                                (injectedProtocolPart.getLibrary());
                        checkLibraryFuture = AbstractThreadProcess.EXECUTOR.submit(checkLibraryCallable);
                        if (!checkLibraryFuture.get()) {
                            Logger.getInstance().error("File '" + injectedProtocolPart.getLibrary().toString() + "' " +
                                    "contains empty lines");
                            return;
                        }
                    }
                    break;
                case SIMULTANEOUS:
                    final FuzzingCheckLibraryCallable checkLibraryCallable = new FuzzingCheckLibraryCallable
                            (varLibInjectedProtocolParts.get(0).getLibrary());
                    checkLibraryFuture = AbstractThreadProcess.EXECUTOR.submit(checkLibraryCallable);
                    if (!checkLibraryFuture.get()) {
                        Logger.getInstance().error("File '" + varLibInjectedProtocolParts.get(0).getLibrary()
                                .toString() + "' contains empty lines");
                        return;
                    }
                    break;
                default:
            }
        } catch (InterruptedException e) {
            checkLibraryFuture.cancel(true);
            throw e;
        }
    }

    /**
     * Filters a given list of injected protocol parts and returns only party that fulfill the requirements type is
     * VAR and data injection mode is LIBRARY.
     *
     * @param injectedProtocolParts the unfiltered list of injected protocol parts
     * @return the filtered list of injected protocol parts
     */
    private List<InjectedProtocolPart> filterVarLibParts(final List<InjectedProtocolPart> injectedProtocolParts) {
        final List<InjectedProtocolPart> varLibInjectedProtocolParts = new ArrayList<>();
        for (final InjectedProtocolPart injectedProtocolPart : injectedProtocolParts) {
            if (injectedProtocolPart.getProtocolPart().getType() == ProtocolPart.Type.VAR && injectedProtocolPart
                    .getDataInjectionMethod() == InjectedProtocolPart.DataInjectionMethod.LIBRARY) {
                varLibInjectedProtocolParts.add(injectedProtocolPart);
            }
        }
        return varLibInjectedProtocolParts;
    }

    /**
     * Gets the duration of the runnable.
     *
     * @return the fuzzing duration in milliseconds.
     */
    public long getDuration() {
        return endTime - startTime;
    }

    /**
     * Gets the start time.
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Gets the saved data files with sent and received data, depending on the chosen record settings.
     *
     * @return the saved data files
     */
    public List<SavedDataFile> getSavedDataFiles() {
        return savedDataFiles;
    }
}