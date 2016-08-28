/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.report;

import model.Model;
import model.process.AbstractRunner;
import model.process.fuzzoptions.Process.RecordingMethod;
import model.protocol.InjectedProtocolStructure;
import model.record.Recordings;
import model.util.Constants;
import model.util.XmlExchange;
import nu.xom.Document;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This class is the learn runnable, responsible for controlling the learning of sequences.
 */
class Runner extends AbstractRunner {

    private final Recordings recordings;
    private final Duration duration;
    private final int interval;
    private final InetSocketAddress target;
    private final InjectedProtocolStructure injectedProtocolStructure;
    private final int workProgress;
    private final int workTotal;
    private final RecordingMethod recordingMethod;
    private final int timeout;
    private final Path outputDirectory;

    /**
     * Constructs a report runnable.
     *
     * @param recordings                the communication recordings
     * @param duration                  the fuzzing duration
     * @param target                    the tested target
     * @param interval                  the fuzzing interval
     * @param injectedProtocolStructure the injected protocol structure used for fuzzing
     * @param workProgress              the processed fuzzign iterations
     * @param workTotal                 the total amount of fuzzing iterations
     * @param recordingMethod           the method used for recordings
     * @param timeout                   the timeout of a successful crash
     * @param outputDirectory           the directory the report will be saved to
     */
    public Runner(Recordings recordings, Duration duration, InetSocketAddress target, int interval,
                  InjectedProtocolStructure injectedProtocolStructure, int workProgress, int workTotal,
                  RecordingMethod recordingMethod, int timeout, Path outputDirectory) {
        super(3);
        this.recordings = recordings;
        this.duration = duration;
        this.target = target;
        this.interval = interval;
        this.injectedProtocolStructure = injectedProtocolStructure;
        this.workProgress = workProgress;
        this.workTotal = workTotal;
        this.recordingMethod = recordingMethod;
        this.timeout = timeout;
        this.outputDirectory = outputDirectory;
    }

    @Override
    public void run() {
        //noinspection TryWithIdenticalCatches
        try {
            markStart();

            // Start new work unit
            // Determine the output name used for the file and the directory
            //noinspection TypeMayBeWeakened
            NameFinder nameFinder = new NameFinder(outputDirectory, recordings);
            Future<String> nameFinderFuture = submitToThreadPool(nameFinder);
            String outputName = nameFinderFuture.get();
            markProgress();

            // Start new work unit
            //noinspection TypeMayBeWeakened
            DocumentCreator documentCreator =
                    new DocumentCreator(recordingMethod, recordings, injectedProtocolStructure, target, timeout,
                            interval, duration, workProgress, workTotal);
            Future<Document> documentFuture = submitToThreadPool(documentCreator);
            Document html = documentFuture.get();
            markProgress();

            // Start new work unit
            //noinspection StringConcatenationMissingWhitespace
            Path directory = outputDirectory.resolve(outputName + Constants.RECORDINGS_DIRECTORY_POSTFIX);
            Files.createDirectory(directory);
            //noinspection HardCodedStringLiteral
            boolean saved = XmlExchange.save(html, outputDirectory.resolve(outputName + ".html"));
            if (!saved) {
                markCancel();
                return;
            }
            // Write every recorded file in a file in the specified directory
            for (int i = 0; i < recordings.getSize(); i++) {
                // Move the temporary file to the output directory
                Files.move(recordings.getRecord(i).getFilePath(), recordings.getRecord(i).getOutputPath());
            }
            markFinish();
        } catch (InterruptedException ignored) {
            Model.INSTANCE.getLogger().info("Learning protocol structure cancelled");
            markCancel();
        } catch (ExecutionException e) {
            // Should not happen
            Model.INSTANCE.getLogger().error(e);
            markCancel();
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error(e);
            markCancel();
        }
    }
}
