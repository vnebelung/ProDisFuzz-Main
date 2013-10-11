/*
 * This file is part of ProDisFuzz, modified on 11.10.13 21:02.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.runnable;

import model.Model;
import model.ProtocolFile;
import model.ProtocolPart;
import model.callable.*;
import model.process.AbstractThreadProcess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LearnRunnable extends AbstractRunnable {

    private final List<ProtocolFile> files;
    private List<ProtocolPart> parts;

    /**
     * Instantiates a new runnable responsible for controlling the learning of sequences.
     *
     * @param files the protocol files
     */
    public LearnRunnable(final List<ProtocolFile> files) {
        super();
        this.files = files;
        finished = false;
    }

    @Override
    public void run() {
        parts = new ArrayList<>();
        finished = false;
        Future<List<List<Byte>>> convertFuture = null;
        Future<int[]> selectFuture = null;
        Future<List<Byte>> hirschbergFuture = null;
        Future<List<ProtocolPart>> partsFuture = null;
        Future<List<Byte>> adjustFuture = null;
        try {
            final LearnConvertCallable convertCallable = new LearnConvertCallable(files);
            convertFuture = AbstractThreadProcess.EXECUTOR.submit(convertCallable);
            final List<List<Byte>> sequences = new ArrayList<>(convertFuture.get());
            Model.INSTANCE.getLogger().info("Files converted to sequences");
            spreadUpdate();

            // Every iteration two sequences are combined into a new one until there is only one left
            while (sequences.size() > 1) {
                final LearnSelectCallable selectCallable = new LearnSelectCallable(sequences);
                selectFuture = AbstractThreadProcess.EXECUTOR.submit(selectCallable);
                final int[] minIndices = selectFuture.get();
                final StringBuilder string = new StringBuilder();
                string.append("Queued sequences: ");
                for (int i = 0; i < sequences.size(); i++) {
                    if (i > 0) {
                        string.append(", ");
                    }
                    if (i == minIndices[0] || i == minIndices[1]) {
                        string.append('*');
                    }
                    string.append(Integer.toHexString(sequences.get(i).hashCode()));
                    if (i == minIndices[0] || i == minIndices[1]) {
                        string.append('*');
                    }
                }
                Model.INSTANCE.getLogger().info(string.toString());
                spreadUpdate();

                //Execute the Hirschberg algorithm
                final LearnHirschbergCallable hirschbergCallable = new LearnHirschbergCallable(sequences.get
                        (minIndices[0]), sequences.get(minIndices[1]));
                hirschbergFuture = AbstractThreadProcess.EXECUTOR.submit(hirschbergCallable);
                // Add the new sequence to the list
                sequences.add(new ArrayList<>(hirschbergFuture.get()));
                final String hash0 = Integer.toHexString(sequences.get(minIndices[0]).hashCode());
                final String hash1 = Integer.toHexString(sequences.get(minIndices[1]).hashCode());
                final String newHash = Integer.toHexString(sequences.get(sequences.size() - 1).hashCode());
                // Remove the two learned sequences from the list, first minIndices[1] because it is the higher index
                sequences.remove(minIndices[1]);
                sequences.remove(minIndices[0]);
                Model.INSTANCE.getLogger().info("Sequences merged: " + hash0 + ", " + hash1 + " -> " + newHash);
                spreadUpdate();

                final LearnPartsCallable partsCallable = new LearnPartsCallable(sequences.get(sequences.size() - 1));
                partsFuture = AbstractThreadProcess.EXECUTOR.submit(partsCallable);
                parts.clear();
                parts.addAll(partsFuture.get());
                Model.INSTANCE.getLogger().info("Temporary protocol structure generated");
                spreadUpdate();
            }

            final LearnAdjustCallable adjustRunnable = new LearnAdjustCallable(sequences.get(0));
            adjustFuture = AbstractThreadProcess.EXECUTOR.submit(adjustRunnable);
            sequences.add(adjustFuture.get());
            sequences.remove(0);
            Model.INSTANCE.getLogger().info("Protocol structure adjusted");
            spreadUpdate();

            final LearnPartsCallable partsCallable = new LearnPartsCallable(sequences.get(0));
            partsFuture = AbstractThreadProcess.EXECUTOR.submit(partsCallable);
            parts.clear();
            parts.addAll(partsFuture.get());
            finished = true;
            Model.INSTANCE.getLogger().info("Final protocol structure generated");
            spreadUpdate();
        } catch (InterruptedException e) {
            convertFuture.cancel(true);
            if (selectFuture != null) {
                selectFuture.cancel(true);
            }
            if (hirschbergFuture != null) {
                hirschbergFuture.cancel(true);
            }
            if (partsFuture != null) {
                partsFuture.cancel(true);
            }
            if (adjustFuture != null) {
                adjustFuture.cancel(true);
            }
        } catch (ExecutionException e) {
            Model.INSTANCE.getLogger().error(e);
        }
    }

    /**
     * Returns the learned protocol parts that are the combination of two inout sequences.
     *
     * @return the learned protocol parts
     */
    public List<ProtocolPart> getParts() {
        return Collections.unmodifiableList(parts);
    }

}
