/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.Model;
import model.process.AbstractRunner;
import model.protocol.ProtocolFile;
import model.protocol.ProtocolStructure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This class is the learn runnable, responsible for controlling the learning of sequences.
 */
class Runner extends AbstractRunner {

    private Set<ProtocolFile> files;
    private ProtocolStructure protocolStructure = new ProtocolStructure();

    /**
     * Constructs a learn runnable.
     *
     * @param files the protocol files that are the input for learning the protocol structure
     */
    public Runner(Set<ProtocolFile> files) {
        // Work: convert + n-grams + dice values + nearest neighbor + Hirschberg + n-grams + dice values + convert +
        // adjust + convert
        super(files.size() + files.size() + (files.size() * (files.size() - 1)) / 2 + (files.size() - 1) +
                (files.size() - 1) + (files.size() - 1) + ((files.size() - 1) * (files.size() - 2)) / 2 +
                (files.size() - 1) + 1 + 1);
        this.files = files;
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    public void run() {
        try {
            markStart();
            protocolStructure.clear();
            Set<LearnSequence> sequences = new HashSet<>(files.size());

            // Create sequences out of the protocol files
            for (ProtocolFile each : files) {

                // Start new workunit
                List<Byte> sequence = convertToSequence(each);
                markProgress();

                // Start new workunit
                Set<String> nGrams = createNGgams(sequence, 3);
                markProgress();

                sequences.add(new LearnSequence(sequence, nGrams));
            }

            // Calculate dice values and distances
            for (LearnSequence eachFrom : sequences) {
                for (LearnSequence eachTo : sequences) {
                    //noinspection ObjectEquality
                    if (eachFrom == eachTo) {
                        continue;
                    }
                    if (eachTo.getDistanceTo(eachFrom) > -1) {
                        eachFrom.addDistanceTo(eachTo, eachTo.getDistanceTo(eachFrom));
                        continue;
                    }

                    // Start new workunit
                    eachFrom.addDistanceTo(eachTo, calculateDiceValue(eachFrom, eachTo));
                    markProgress();
                }
            }

            // Every iteration two sequences are combined into a new one until there is only one left
            while (sequences.size() > 1) {

                // Find the two distances that will be merged, that that is the two sequences with the lowest
                // distance to each other
                // Start new workunit
                Set<LearnSequence> nearestNeighbors = findNearestNeighbors(sequences);
                markProgress();

                StringBuilder logEntry = new StringBuilder();
                logEntry.append("Queued sequences: ");
                for (LearnSequence each : sequences) {
                    logEntry.append(nearestNeighbors.contains(each) ? '*' : "");
                    logEntry.append(Integer.toHexString(each.hashCode()));
                    logEntry.append(nearestNeighbors.contains(each) ? '*' : "");
                    logEntry.append(", ");
                }
                logEntry.delete(logEntry.length() - 2, logEntry.length());
                Model.INSTANCE.getLogger().info(logEntry.toString());

                // Start new workunit
                //Execute the Hirschberg algorithm on the two sequences
                List<LearnSequence> tmp = new ArrayList<>(nearestNeighbors);
                List<Byte> hirschbergSequence = learn(tmp.get(0), tmp.get(1));
                markProgress();

                // Start new workunit
                Set<String> nGrams = createNGgams(hirschbergSequence, 3);
                markProgress();

                // Remove the two old sequences
                nearestNeighbors.forEach(sequences::remove);
                // Clean up old references
                for (LearnSequence eachNearestNeighbor : nearestNeighbors) {
                    for (LearnSequence each : sequences) {
                        each.removeDistanceTo(eachNearestNeighbor);
                    }
                }
                // Add new references
                LearnSequence mergedSequence = new LearnSequence(hirschbergSequence, nGrams);

                // Update dice values and distances of existing sequences and the new merged one
                for (LearnSequence each : sequences) {

                    // Start new workunit
                    double diceValue = calculateDiceValue(each, mergedSequence);
                    each.addDistanceTo(mergedSequence, diceValue);
                    mergedSequence.addDistanceTo(each, diceValue);
                    markProgress();
                }

                // Add new sequence
                sequences.add(mergedSequence);
                String hash0 = Integer.toHexString(tmp.get(0).hashCode());
                String hash1 = Integer.toHexString(tmp.get(1).hashCode());
                String newHash = Integer.toHexString(mergedSequence.hashCode());
                Model.INSTANCE.getLogger().info("Sequences merged: " + hash0 + ", " + hash1 + " -> " + newHash);

                // Generate new protocol blocks
                // Start new workunit
                protocolStructure = generateProtocolParts(mergedSequence);
                Model.INSTANCE.getLogger().info("Temporary protocol structure generated");
                markProgress();
            }
            // Adjust the last remaining sequence
            // Start new workunit
            LearnSequence lastSequence = sequences.iterator().next();
            LearnSequence adjustedSequence = clean(lastSequence);
            sequences.remove(lastSequence);
            sequences.add(adjustedSequence);
            Model.INSTANCE.getLogger().info("Protocol structure cleaned");
            markProgress();

            // Generate new protocol blocks
            // Start new workunit
            protocolStructure = generateProtocolParts(sequences.iterator().next());
            markFinish();
        } catch (InterruptedException ignored) {
            Model.INSTANCE.getLogger().info("Learning protocol structure cancelled");
            markCancel();
        } catch (ExecutionException e) {
            // Should not happen
            Model.INSTANCE.getLogger().error(e);
        }
    }

    /**
     * Calculates the dice value for two input sequences.
     *
     * @param sequence1 the first input sequence
     * @param sequence2 the second input sequence
     * @return the dice value
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    private static double calculateDiceValue(LearnSequence sequence1, LearnSequence sequence2) throws
            ExecutionException, InterruptedException {
        //noinspection TypeMayBeWeakened
        DiceValueCalculator diceValueCalculator = new DiceValueCalculator(sequence1, sequence2);
        Future<Double> diceValueCalculatorFuture = submitToThreadPool(diceValueCalculator);
        return diceValueCalculatorFuture.get();
    }

    /**
     * Converts the collected files to a list of byte sequences.
     *
     * @param sequence  the sequence the n-grams are created for
     * @param nGramSize the size of the n-grams to be created
     * @return the n-gram size that is going to be used
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    private static Set<String> createNGgams(List<Byte> sequence, int nGramSize) throws ExecutionException,
            InterruptedException {
        //noinspection TypeMayBeWeakened
        NGramCreator nGramCreator = new NGramCreator(sequence, nGramSize);
        Future<Set<String>> nGramCreatorFuture = submitToThreadPool(nGramCreator);
        return nGramCreatorFuture.get();
    }

    /**
     * Converts the collected files to a list of byte sequences.
     *
     * @param file the protocol file to be converted
     * @return the listed byte sequences
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    private static List<Byte> convertToSequence(ProtocolFile file) throws ExecutionException, InterruptedException {
        //noinspection TypeMayBeWeakened
        SequenceConverter sequenceConverter = new SequenceConverter(file);
        Future<List<Byte>> sequenceConverterFuture = submitToThreadPool(sequenceConverter);
        return sequenceConverterFuture.get();
    }

    /**
     * Selects two sequences that will be merged into one aligned sequence. That is the two sequences that have the
     * lowest distance to each other and are therefore the nearest neighbors.
     *
     * @param sequences the input sequences
     * @return the two chosen sequences
     * @throws ExecutionException   if the computation threw an exception
     * @throws InterruptedException if the current thread was interrupted while waiting
     */
    private static Set<LearnSequence> findNearestNeighbors(Set<LearnSequence> sequences) throws ExecutionException,
            InterruptedException {
        //noinspection TypeMayBeWeakened
        SequenceSelector sequenceSelector = new SequenceSelector(sequences);
        Future<Set<LearnSequence>> sequenceSelectorFuture = submitToThreadPool(sequenceSelector);
        return sequenceSelectorFuture.get();
    }

    /**
     * Executes the protocol learning algorithm on two sequences to generate a new aligned sequence.
     *
     * @param sequence1 the first input sequence
     * @param sequence2 the second input sequence
     * @return the aligned sequence
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws ExecutionException   if the computation threw an exception
     */
    private static List<Byte> learn(LearnSequence sequence1, LearnSequence sequence2) throws InterruptedException,
            ExecutionException {
        //noinspection TypeMayBeWeakened
        HirschbergExecutor hirschbergExecutor =
                new HirschbergExecutor(sequence1.getSequence(), sequence2.getSequence());
        Future<List<Byte>> hirschbergExecutorFuture = submitToThreadPool(hirschbergExecutor);
        return hirschbergExecutorFuture.get();
    }

    /**
     * Generates protocol blocks from a given sequence. All byte values will be transformed block-wise into protocol
     * blocks, each indicating whether it is a variable or fixed data block.
     *
     * @param sequence the sequence to generate the protocol blocks from
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws ExecutionException   if the computation threw an exception
     */
    private static ProtocolStructure generateProtocolParts(LearnSequence sequence) throws InterruptedException,
            ExecutionException {
        Callable<ProtocolStructure> structureConverter = new StructureConverter(sequence.getSequence());
        Future<ProtocolStructure> structureConverterFuture = submitToThreadPool(structureConverter);
        return structureConverterFuture.get();
    }

    /**
     * Adjust a sequence by cleaning it up from inconsistencies and giving it a more block-oriented structure.
     *
     * @param sequence the sequence to adjust
     * @return the adjusted sequence
     * @throws InterruptedException if the current thread was interrupted while waiting
     * @throws ExecutionException   if the computation threw an exception
     */
    private static LearnSequence clean(LearnSequence sequence) throws InterruptedException, ExecutionException {
        Callable<List<Byte>> sequenceCleaner = new SequenceCleaner(sequence.getSequence());
        Future<List<Byte>> sequenceCleanerFuture = submitToThreadPool(sequenceCleaner);
        List<Byte> cleanedSequence = sequenceCleanerFuture.get();
        return new LearnSequence(cleanedSequence, new HashSet<>(0));
    }

    /**
     * Returns the learned protocol structure that is the combination of the input sequences.
     *
     * @return the learned protocol structure
     */
    public ProtocolStructure getProtocolStructure() {
        return protocolStructure;
    }

}
