/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzing;

import model.Model;
import model.process.AbstractRunner;
import model.process.fuzzoptions.Process.InjectionMethod;
import model.process.fuzzoptions.Process.RecordingMethod;
import model.protocol.InjectedProtocolStructure;
import model.record.Recordings;

import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This class is the fuzzing runnable, responsible for handling the fuzzing activities in a separate thread.
 */
class Runner extends AbstractRunner {
    private InetSocketAddress target;
    private int timeout;
    private int interval;
    private Recordings recordings;
    private InjectionMethod injectionMethod;
    private InjectedProtocolStructure injectedProtocolStructure;
    private RecordingMethod recordingMethod;
    private Instant crashTime;

    /**
     * Constructs a fuzzing runnable.
     *
     * @param injectionMethod           the injection method
     * @param injectedProtocolStructure the injected protocol blocks
     * @param target                    the fuzzing target
     * @param timeout                   the target timeout
     * @param interval                  the fuzzing interval
     * @param recordingMethod           the option to save the fuzzed messages
     */
    public Runner(InjectionMethod injectionMethod, InjectedProtocolStructure injectedProtocolStructure,
                  InetSocketAddress target, int timeout, int interval, RecordingMethod recordingMethod) {
        // Amount of work depends of the user chosen options
        super(injectedProtocolStructure.getNumOfIterations(injectionMethod) == -1 ? -1 :
                injectedProtocolStructure.getNumOfIterations(injectionMethod) + 1);
        this.injectionMethod = injectionMethod;
        this.injectedProtocolStructure = injectedProtocolStructure;
        this.target = target;
        this.timeout = timeout;
        this.interval = interval;
        this.recordingMethod = recordingMethod;
        recordings = new Recordings();
    }

    @Override
    public void run() {
        try {
            markStart();
            Model.INSTANCE.getLogger().info("Fuzzing process started");

            // The fuzzing begins …
            int iteration = 0;
            Callable<byte[]> fuzzedMessageCreator =
                    new FuzzedMessageCreator(injectedProtocolStructure, injectionMethod);
            while (true) {
                byte[] message = submitToThreadPool(fuzzedMessageCreator).get();
                if (message == null) {
                    break;
                }
                iteration++;
                FuzzedMessageSender fuzzedMessageSender = new FuzzedMessageSender(message, target, timeout);
                Model.INSTANCE.getLogger().info("Sending fuzzed message #" + iteration);

                // Start new work unit
                for (int i = 0; i < 3; i++) {
                    if (submitToThreadPool(fuzzedMessageSender).get()) {
                        if (recordingMethod == RecordingMethod.ALL) {
                            recordings.addRecording(message, false, Instant.now());
                            recordings.addRecording(fuzzedMessageSender.getLastResponse(), false, Instant.now());
                        }
                        markProgress();
                        break;
                    } else {
                        handleTimeout(i, message);
                    }
                }
                //noinspection BusyWait
                Thread.sleep(interval);
            }
            Model.INSTANCE.getLogger().info("Fuzzing process successfully completed");
            markFinish();
        } catch (InterruptedException ignored) {
            Model.INSTANCE.getLogger().info("Fuzzing process cancelled");
            markCancel();
        } catch (ExecutionException e) {
            // Should not happen
            Model.INSTANCE.getLogger().error(e);
        }
    }

    /**
     * Handles the situation when the target does not respond to sent messages and tries to reconnect in case of an
     * error.
     *
     * @param iteration the current iteration of tries to sent a message
     * @param bytes     the sent message
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws ExecutionException   if the computation threw an exception
     */
    private void handleTimeout(int iteration, byte... bytes) throws InterruptedException, ExecutionException {
        double errorInterval = Math.pow(iteration + 2, 0.75) * interval;
        Future<Boolean> reconnectorFuture;
        DecimalFormat decimalFormat = new DecimalFormat(",##0.0");
        switch (iteration) {
            case 0:
                crashTime = Instant.now();
            case 1:
                // Error interval has a logarithmic style curve
                Model.INSTANCE.getLogger().warning(
                        "Target not reachable. Resend message again in " + decimalFormat.format(errorInterval / 1000) +
                                " seconds");
                //noinspection NumericCastThatLosesPrecision
                Thread.sleep((long) errorInterval);
                break;
            case 2:
                Model.INSTANCE.getLogger()
                        .fine("Target not reachable for 3 times in a row. Information about the" + ' ' +
                                "crash is being saved");
                recordings.addRecording(bytes, true, crashTime);
                markProgress();
                int count = 1;
                do {
                    errorInterval = Math.pow(count, 0.75) * interval;
                    count++;
                    Model.INSTANCE.getLogger().warning(
                            "Trying to reconnect to target in " + decimalFormat.format(errorInterval / 1000) +
                                    " seconds");
                    //noinspection NumericCastThatLosesPrecision,BusyWait
                    Thread.sleep((long) errorInterval);
                    // Try again to connect
                    //noinspection TypeMayBeWeakened
                    Reconnector reconnector = new Reconnector(target, timeout);
                    reconnectorFuture = submitToThreadPool(reconnector);
                } while (!reconnectorFuture.get());
                Model.INSTANCE.getLogger().info("Connection to target successfully reestablished");
                break;
            default:
        }
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
