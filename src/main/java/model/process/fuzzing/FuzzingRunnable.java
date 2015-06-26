/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import model.Model;
import model.process.AbstractRunnable;
import model.process.AbstractThreadProcess;
import model.process.fuzzoptions.FuzzOptionsProcess.CommunicationSave;
import model.process.fuzzoptions.FuzzOptionsProcess.InjectionMethod;
import model.protocol.InjectedProtocolBlock;
import model.protocol.InjectedProtocolStructure;
import model.record.Recordings;

import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FuzzingRunnable extends AbstractRunnable {
    private final InetSocketAddress target;
    private final int timeout;
    private final int interval;
    private final Recordings recordings;
    private final InjectionMethod injectionMethod;
    private final InjectedProtocolStructure injectedProtocolStructure;
    private final CommunicationSave saveCommunication;
    private Instant startTime;
    private Instant endTime;
    private Instant crashTime;

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
    public FuzzingRunnable(InjectionMethod injectionMethod, InjectedProtocolStructure injectedProtocolStructure,
                           InetSocketAddress target, int timeout, int interval, CommunicationSave saveCommunication) {
        super();
        this.injectionMethod = injectionMethod;
        this.injectedProtocolStructure = injectedProtocolStructure;
        this.target = target;
        this.timeout = timeout;
        this.interval = interval;
        this.saveCommunication = saveCommunication;
        recordings = new Recordings();
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
            //noinspection UnnecessaryDefault
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
            }
        }
        return result;
    }

    /**
     * Calculates the amount of work for simultaneous data injections.
     *
     * @return the number of work steps, -1 for infinite work
     */
    private static int calcWorkTotalSimultaneous() {
        InjectedProtocolBlock injectedProtocolBlock = Model.INSTANCE.getFuzzOptionsProcess()
                .getInjectedProtocolStructure().getVarBlock(0);
        switch (injectedProtocolBlock.getDataInjectionMethod()) {
            case LIBRARY:
                return injectedProtocolBlock.getNumOfLibraryLines();
            case RANDOM:
                return -1;
            //noinspection UnnecessaryDefault
            default:
                return 0;
        }
    }

    @Override
    public void run() {
        startTime = Instant.now();
        try {
            resetWorkProgress();
            setFinished(false);
            // The fuzzing begins …
            int iteration = 0;
            Callable<byte[]> messageCallable = new FuzzingMessageCallable(injectedProtocolStructure, injectionMethod);
            while (true) {
                byte[] message = getMessage(messageCallable);
                if (message == null) {
                    break;
                }
                iteration++;
                send(message, iteration);
            }
            endTime = Instant.now();
            setFinished(true);
        } catch (InterruptedException ignored) {
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
                    if (saveCommunication == CommunicationSave.ALL) {
                        recordings.addRecording(message, false, Instant.now());
                        recordings.addRecording(sendCallable.getLastResponse(), false, Instant.now());
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
    private static byte[] getMessage(Callable<byte[]> messageCallable) throws InterruptedException, ExecutionException {
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
     * @param bytes         the sent message
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void handleTimeout(int iteration, byte... bytes) throws InterruptedException, ExecutionException {
        Future<Boolean> reconnectFuture = null;
        double errorInterval = Math.pow(iteration + 2, 0.75) * interval;
        DecimalFormat decimalFormat = new DecimalFormat(",##0.0");
        try {
            switch (iteration) {
                case 0:
                    crashTime = Instant.now();
                case 1:
                    // Error interval has a logarithmic style curve
                    Model.INSTANCE.getLogger().warning("Target not reachable. Resend message again in " +
                            decimalFormat.format(errorInterval / 1000) + " seconds");
                    //noinspection NumericCastThatLosesPrecision
                    Thread.sleep((long) errorInterval);
                    break;
                case 2:
                    Model.INSTANCE.getLogger().fine("Target not reachable for 3 times in a row. Information about the" +
                            ' ' + "crash is being saved");
                    recordings.addRecording(bytes, true, crashTime);
                    increaseWorkProgress();
                    int count = 1;
                    do {
                        errorInterval = Math.pow(count, 0.75) * interval;
                        count++;
                        Model.INSTANCE.getLogger().warning("Trying to reconnect to target in " + decimalFormat.format
                                (errorInterval / 1000) + " seconds");
                        //noinspection NumericCastThatLosesPrecision,BusyWait
                        Thread.sleep((long) errorInterval);
                        // Try again to connect
                        Callable<Boolean> reconnectCallable = new FuzzingReconnectCallable(target, timeout);
                        reconnectFuture = AbstractThreadProcess.EXECUTOR.submit(reconnectCallable);
                    } while (!reconnectFuture.get());
                    Model.INSTANCE.getLogger().info("Connection to target successfully reestablished");
                    startTime = startTime.plusMillis(Instant.now().minusMillis(crashTime.toEpochMilli()).toEpochMilli
                            ());
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
        return isFinished() ? Duration.between(startTime, endTime) : Duration.between(startTime, Instant.now());
    }

    /**
     * Returns the time the fuzzing was started.
     *
     * @return the fuzzing start time
     */
    public Temporal getStartTime() {
        return startTime;
    }

    /**
     * Returns the saved data files with sent and received data, depending on the chosen record settings.
     *
     * @return the saved data files
     */
    public Recordings getRecordings() {
        return recordings;
    }
}
