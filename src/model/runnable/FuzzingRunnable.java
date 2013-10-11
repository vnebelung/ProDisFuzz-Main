/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:35.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.runnable;

import model.InjectedProtocolPart;
import model.Model;
import model.ProtocolPart;
import model.SavedDataFile;
import model.callable.FuzzingCheckLibraryCallable;
import model.callable.FuzzingMessageCallable;
import model.callable.FuzzingReconnectCallable;
import model.callable.FuzzingSendCallable;
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
     * Instantiates a new runnable responsible for handling the fuzzing activities in a separate thread.
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
                Model.INSTANCE.getLogger().info("Sending fuzzed message #" + iteration);
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
            Model.INSTANCE.getLogger().error(e);
        }
    }

    /**
     * Handles the situation when the target does not respond to sent messages and tries to reconnect in case of an
     * error.
     *
     * @param iteration the current iteration of tries to sent a message
     * @param b         the sent message
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void handleTimeout(final int iteration, final byte[] b) throws InterruptedException, ExecutionException {
        Future<Boolean> reconnectFuture = null;
        double errorInterval = Math.pow(iteration + 2, 0.75) * interval;
        final DecimalFormat decimalFormat = new DecimalFormat(",##0.0");
        try {
            switch (iteration) {
                case 0:
                    crashTime = System.currentTimeMillis();
                case 1:
                    // Error interval has a logarithmic style curve
                    Model.INSTANCE.getLogger().warning("Target not reachable. Resend message again in " +
                            (decimalFormat.format(errorInterval / 1000)) + " seconds");
                    Thread.sleep((long) errorInterval);
                    break;
                case 2:
                    Model.INSTANCE.getLogger().fine("Target not reachable for 3 times in a row. Information about the" +
                            " " + "crash is being saved");
                    savedDataFiles.add(new SavedDataFile(b, true, crashTime));
                    int count = 1;
                    do {
                        errorInterval = Math.pow(count++, 0.75) * interval;
                        Model.INSTANCE.getLogger().warning("Trying to reconnect to target in " + (decimalFormat
                                .format(errorInterval / 1000)) + " seconds");
                        Thread.sleep((long) errorInterval);
                        // Try again to connect
                        final FuzzingReconnectCallable reconnectCallable = new FuzzingReconnectCallable(target,
                                timeout);
                        reconnectFuture = AbstractThreadProcess.EXECUTOR.submit(reconnectCallable);
                    } while (!reconnectFuture.get());
                    Model.INSTANCE.getLogger().info("Connection to target successfully reestablished");
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
     * @param parts the variable protocol parts with library-based data injection method
     * @throws InterruptedException
     */
    private void checkLibraries(final List<InjectedProtocolPart> parts) throws InterruptedException,
            ExecutionException {
        if (parts.isEmpty()) {
            return;
        }
        Future<Boolean> checkLibraryFuture = null;
        try {
            switch (injectionMethod) {
                case SEPARATE:
                    for (final InjectedProtocolPart each : parts) {
                        final FuzzingCheckLibraryCallable checkLibraryCallable = new FuzzingCheckLibraryCallable(each
                                .getLibrary());
                        checkLibraryFuture = AbstractThreadProcess.EXECUTOR.submit(checkLibraryCallable);
                        if (!checkLibraryFuture.get()) {
                            Model.INSTANCE.getLogger().error("File '" + each.getLibrary().toString() + "' " +
                                    "contains empty lines");
                            return;
                        }
                    }
                    break;
                case SIMULTANEOUS:
                    final FuzzingCheckLibraryCallable checkLibraryCallable = new FuzzingCheckLibraryCallable(parts
                            .get(0).getLibrary());
                    checkLibraryFuture = AbstractThreadProcess.EXECUTOR.submit(checkLibraryCallable);
                    if (!checkLibraryFuture.get()) {
                        Model.INSTANCE.getLogger().error("File '" + parts.get(0).getLibrary().toString() + "' " +
                                "contains empty lines");
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
     * Filters a given list of injected protocol parts and returns only variable parts with library-based data
     * injection mode.
     *
     * @param parts the unfiltered list of injected protocol parts
     * @return the filtered list of injected protocol parts
     */
    private List<InjectedProtocolPart> filterVarLibParts(final List<InjectedProtocolPart> parts) {
        final List<InjectedProtocolPart> varLibInjectedProtocolParts = new ArrayList<>();
        for (final InjectedProtocolPart each : parts) {
            if (each.getProtocolPart().getType() == ProtocolPart.Type.VAR && each.getDataInjectionMethod() ==
                    InjectedProtocolPart.DataInjectionMethod.LIBRARY) {
                varLibInjectedProtocolParts.add(each);
            }
        }
        return varLibInjectedProtocolParts;
    }

    /**
     * Returns the duration of the running time of the runnable. If the runnable is running at the time of the method
     * call, the current duration is returned.
     *
     * @return the fuzzing duration in milliseconds
     */
    public long getDuration() {
        return finished ? endTime - startTime : System.currentTimeMillis() - startTime;
    }

    /**
     * Returns the time the fuzzing was started.
     *
     * @return the fuzzing start time in milliseconds
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Returns the saved data files with sent and received data, depending on the chosen record settings.
     *
     * @return the saved data files
     */
    public List<SavedDataFile> getSavedDataFiles() {
        return savedDataFiles;
    }
}
