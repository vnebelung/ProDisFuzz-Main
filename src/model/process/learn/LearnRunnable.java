/*
 * This file is part of ProDisFuzz, modified on 13.03.14 22:10.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import model.Model;
import model.ProtocolFile;
import model.ProtocolPart;
import model.process.AbstractRunnable;
import model.process.AbstractThreadProcess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

class LearnRunnable extends AbstractRunnable {

    private final List<ProtocolFile> files;
    private List<ProtocolPart> protocolParts;

    /**
     * Instantiates a new runnable responsible for controlling the learning of sequences.
     *
     * @param files the protocol files
     */
    public LearnRunnable(List<ProtocolFile> files) {
        super();
        this.files = files;
        // Work: Convert + Select + Hirschberg + Parts + Clean + Parts
        setWorkTotal(1 + files.size() - 1 + files.size() - 1 + files.size() - 1 + 1 + 1);
    }

    @Override
    public void run() {
        protocolParts = new ArrayList<>();
        try {
            resetWorkProgress();
            setFinished(false);
            List<List<Byte>> sequences = convertToSequences();
            // Every iteration two sequences are combined into a new one until there is only one left
            while (sequences.size() > 1) {
                // Find the two sequences that are merged
                int[] minIndices = selectSequences(sequences);
                //Execute the Hirschberg algorithm on the two sequences
                sequences.add(learn(sequences.get(minIndices[0]), sequences.get(minIndices[1])));
                // Remove the two learned sequences from the list, first minIndices[1] because it is the higher index
                sequences.remove(minIndices[1]);
                sequences.remove(minIndices[0]);
                // Generate new protocol parts
                generateProtocolParts(sequences.get(sequences.size() - 1));
            }
            // Adjust the last remaining sequence
            sequences.add(adjust(sequences.get(0)));
            sequences.remove(0);
            // Generate new protocol parts
            generateProtocolParts(sequences.get(0));
            setFinished(true);
        } catch (InterruptedException e) {
            // Nothing to do here
        } catch (ExecutionException e) {
            Model.INSTANCE.getLogger().error(e);
        }
    }

    /**
     * Converts the collected files to a list of byte sequences.
     *
     * @return the listed byte sequences
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private List<List<Byte>> convertToSequences() throws ExecutionException, InterruptedException {
        LearnConvertCallable convertCallable = new LearnConvertCallable(files);
        Future<List<List<Byte>>> convertFuture = AbstractThreadProcess.EXECUTOR.submit(convertCallable);
        try {
            List<List<Byte>> result = new ArrayList<>(convertFuture.get());
            increaseWorkProgress();
            Model.INSTANCE.getLogger().info("Files converted to sequences");
            return result;
        } catch (InterruptedException e) {
            convertFuture.cancel(true);
            throw e;
        }
    }

    /**
     * Selects two sequences that will be merged into one aligned sequence.
     *
     * @param sequences the input sequences
     * @return the two indices of the chosen sequences
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private int[] selectSequences(List<List<Byte>> sequences) throws ExecutionException, InterruptedException {
        LearnSelectCallable selectCallable = new LearnSelectCallable(sequences);
        Future<int[]> selectFuture = AbstractThreadProcess.EXECUTOR.submit(selectCallable);
        try {
            int[] result = selectFuture.get();
            increaseWorkProgress();
            StringBuilder string = new StringBuilder();
            string.append("Queued sequences: ");
            for (int i = 0; i < sequences.size(); i++) {
                string.append(i > 0 ? ", " : "");
                string.append(i == result[0] || i == result[1] ? '*' : "");
                string.append(Integer.toHexString(sequences.get(i).hashCode()));
                string.append(i == result[0] || i == result[1] ? '*' : "");
            }
            Model.INSTANCE.getLogger().info(string.toString());
            return result;
        } catch (InterruptedException e) {
            selectFuture.cancel(true);
            throw e;
        }
    }

    /**
     * Executes the protocol learning algorithm on two sequences to generate a new aligned sequence.
     *
     * @param sequence1 the first input sequence
     * @param sequence2 the second input sequence
     * @return the aligned sequence
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private List<Byte> learn(List<Byte> sequence1, List<Byte> sequence2) throws InterruptedException,
            ExecutionException {
        LearnHirschbergCallable hirschbergCallable = new LearnHirschbergCallable(sequence1, sequence2);
        Future<List<Byte>> hirschbergFuture = AbstractThreadProcess.EXECUTOR.submit(hirschbergCallable);
        try {
            // Add the new sequence to the list
            List<Byte> result = new ArrayList<>(hirschbergFuture.get());
            String hash0 = Integer.toHexString(sequence1.hashCode());
            String hash1 = Integer.toHexString(sequence2.hashCode());
            String newHash = Integer.toHexString(result.hashCode());
            increaseWorkProgress();
            Model.INSTANCE.getLogger().info("Sequences merged: " + hash0 + ", " + hash1 + " -> " + newHash);
            return result;
        } catch (InterruptedException e) {
            hirschbergFuture.cancel(true);
            throw e;
        }
    }

    /**
     * Generates protocol parts from a given sequence. All byte values will be transformed block-wise into protocol
     * parts, each indicating whether it is a variable or fixed data block.
     *
     * @param sequence the sequence to generate the protocol parts from
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private void generateProtocolParts(List<Byte> sequence) throws InterruptedException, ExecutionException {
        LearnPartsCallable partsCallable = new LearnPartsCallable(sequence);
        Future<List<ProtocolPart>> partsFuture = AbstractThreadProcess.EXECUTOR.submit(partsCallable);
        try {
            protocolParts = partsFuture.get();
            Model.INSTANCE.getLogger().info("Temporary protocol structure generated");
            increaseWorkProgress();
        } catch (InterruptedException e) {
            partsFuture.cancel(true);
            throw e;
        }
    }

    /**
     * Adjust a sequence by cleaning it up from inconsistencies and giving it a more block-oriented structure.
     *
     * @param sequence the sequence to adjust
     * @return the adjusted sequence
     * @throws InterruptedException
     * @throws ExecutionException
     */
    private List<Byte> adjust(List<Byte> sequence) throws InterruptedException, ExecutionException {
        LearnAdjustCallable adjustCallable = new LearnAdjustCallable(sequence);
        Future<List<Byte>> adjustFuture = AbstractThreadProcess.EXECUTOR.submit(adjustCallable);
        try {
            List<Byte> result = adjustFuture.get();
            Model.INSTANCE.getLogger().info("Protocol structure adjusted");
            increaseWorkProgress();
            return result;
        } catch (InterruptedException e) {
            adjustFuture.cancel(true);
            throw e;
        }
    }

    /**
     * Returns the learned protocol parts that are the combination of two inout sequences.
     *
     * @return the learned protocol parts
     */
    public List<ProtocolPart> getProtocolParts() {
        return Collections.unmodifiableList(protocolParts);
    }

}
