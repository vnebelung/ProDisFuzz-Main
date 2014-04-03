/*
 * This file is part of ProDisFuzz, modified on 03.04.14 20:36.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import model.Model;
import model.SavedDataFile;
import model.process.AbstractRunnable;
import model.process.AbstractThreadProcess;
import model.process.fuzzOptions.FuzzOptionsProcess;
import model.protocol.InjectedProtocolBlock;
import model.protocol.InjectedProtocolStructure;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

class FuzzingRunnable extends AbstractRunnable {
    private final InetSocketAddress target;
    private final int timeout;
    private final int interval;
    private final List<SavedDataFile> savedDataFiles;
    private final FuzzOptionsProcess.InjectionMethod injectionMethod;
    private final InjectedProtocolStructure injectedProtocolStructure;
    private final FuzzOptionsProcess.CommunicationSave saveCommunication;
    private long startTime;
    private long endTime;
    private long crashTime;

    /**
     * Instantiates a new runnable responsible for handling the fuzzing activities in a separate thread.
     *
     * @param injectionMethod           the injection method
     * @param injectedProtocolStructure the injected protocol blocks
     * @param target                    the fuzzing target
     * @param timeout                   the target timeout
     * @param interval                  the fuzzing interval
     * @param saveCommunication         the option to save the fuzzed messages
     */
    public FuzzingRunnable(FuzzOptionsProcess.InjectionMethod injectionMethod,
                           InjectedProtocolStructure injectedProtocolStructure, InetSocketAddress target,
                           int timeout, int interval, FuzzOptionsProcess.CommunicationSave saveCommunication) {
        super();
        this.injectionMethod = injectionMethod;
        this.injectedProtocolStructure = injectedProtocolStructure;
        this.target = target;
        this.timeout = timeout;
        this.interval = interval;
        this.saveCommunication = saveCommunication;
        savedDataFiles = new ArrayList<>();
        // Amount of work depends of the user chosen options
        setWorkTotal(calcWorkTotal());
    }

    /**
     * Calculates the amount of work for this process.
     *
     * @return the number of work steps
     */
    private int calcWorkTotal() {
        switch (injectionMethod) {
            case SEPARATE:
                return calcWorkTotalSeparate();
            case SIMULTANEOUS:
                return calcWorkTotalSimultaneous();
            default:
                return 0;
        }
    }

    /**
     * Calculates the amount of work for separate data injections.
     *
     * @return the number of work steps, -1 for infinite work
     */
    private int calcWorkTotalSeparate() {
        int result = 0;
        for (int i = 0; i < injectedProtocolStructure.getVarSize(); i++) {
            switch (injectedProtocolStructure.getVarBlock(i).getDataInjectionMethod()) {
                case LIBRARY:
                    result += injectedProtocolStructure.getVarBlock(i).getNumOfLibraryLines();
                    break;
                case RANDOM:
                    return -1;
                default:
                    return 0;
            }
        }
        return result;
    }

    /**
     * Calculates the amount of work for simultaneous data injections.
     *
     * @return the number of work steps, -1 for infinite work
     */
    private int calcWorkTotalSimultaneous() {
        InjectedProtocolBlock injectedProtocolBlock = Model.INSTANCE.getFuzzOptionsProcess()
                .getInjectedProtocolStructure().getVarBlock(0);
        switch (injectedProtocolBlock.getDataInjectionMethod()) {
            case LIBRARY:
                return injectedProtocolBlock.getNumOfLibraryLines();
            case RANDOM:
                return -1;
            default:
                return 0;
        }
    }

    @Override
    public void run() {
        startTime = System.currentTimeMillis();
        try {
            resetWorkProgress();
            setFinished(false);
            // The fuzzing begins …
            int iteration = 0;
            FuzzingMessageCallable messageCallable = new FuzzingMessageCallable(injectedProtocolStructure,
                    injectionMethod);
            while (true) {
                byte[] message = getMessage(messageCallable);
                if (message == null) {
                    break;
                }
                send(message, ++iteration);
            }
            endTime = System.currentTimeMillis();
            setFinished(true);
        } catch (InterruptedException e) {
            // Nothing to do here
        } catch (ExecutionException e) {
            Model.INSTANCE.getLogger().error(e);
        }
    }

    /**
     * Sends a given message to the target.
     *
     * @param message   the message to be sent
     * @param iteration the iteration count
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private void send(byte[] message, int iteration) throws ExecutionException, InterruptedException {
        FuzzingSendCallable sendCallable = new FuzzingSendCallable(message, target, timeout);
        Future<Boolean> sendFuture = AbstractThreadProcess.EXECUTOR.submit(sendCallable);
        Model.INSTANCE.getLogger().info("Sending fuzzed message #" + iteration);
        try {
            for (int i = 0; i < 3; i++) {
                if (sendFuture.get()) {
                    if (saveCommunication == FuzzOptionsProcess.CommunicationSave.ALL) {
                        savedDataFiles.add(new SavedDataFile(message, false, System.currentTimeMillis()));
                        savedDataFiles.add(new SavedDataFile(sendCallable.getLastResponse(), false,
                                System.currentTimeMillis()));
                    }
                    increaseWorkProgress();
                    break;
                }
                handleTimeout(i, message);
            }
            Thread.sleep(interval);
        } catch (InterruptedException e) {
            sendFuture.cancel(true);
            throw e;
        }
    }

    /**
     * Generates a fuzzed message.
     *
     * @param messageCallable the message callable used for generating the messages
     * @return the message as a byte sequence
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private byte[] getMessage(FuzzingMessageCallable messageCallable) throws InterruptedException, ExecutionException {
        Future<byte[]> messageFuture = AbstractThreadProcess.EXECUTOR.submit(messageCallable);
        byte[] result;
        try {
            result = messageFuture.get();
        } catch (InterruptedException e) {
            messageFuture.cancel(true);
            throw e;
        }
        return result;
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
    private void handleTimeout(int iteration, byte[] b) throws InterruptedException, ExecutionException {
        Future<Boolean> reconnectFuture = null;
        double errorInterval = Math.pow(iteration + 2, 0.75) * interval;
        DecimalFormat decimalFormat = new DecimalFormat(",##0.0");
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
                    increaseWorkProgress();
                    int count = 1;
                    do {
                        errorInterval = Math.pow(count++, 0.75) * interval;
                        Model.INSTANCE.getLogger().warning("Trying to reconnect to target in " + (decimalFormat
                                .format(errorInterval / 1000)) + " seconds");
                        Thread.sleep((long) errorInterval);
                        // Try again to connect
                        FuzzingReconnectCallable reconnectCallable = new FuzzingReconnectCallable(target, timeout);
                        reconnectFuture = AbstractThreadProcess.EXECUTOR.submit(reconnectCallable);
                    } while (!reconnectFuture.get());
                    Model.INSTANCE.getLogger().info("Connection to target successfully reestablished");
                    startTime += System.currentTimeMillis() - crashTime;
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
     * Returns the duration of the running time of the runnable. If the runnable is running at the time of the method
     * call, the current duration is returned.
     *
     * @return the fuzzing duration
     */
    public Duration getDuration() {
        try {
            return DatatypeFactory.newInstance().newDuration(isFinished() ? endTime - startTime : System
                    .currentTimeMillis() - startTime);
        } catch (DatatypeConfigurationException e) {
            Model.INSTANCE.getLogger().error(e);
            return null;
        }
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
