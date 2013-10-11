/*
 * This file is part of ProDisFuzz, modified on 05.10.13 23:15.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.callable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class LearnSelectCallable implements Callable<int[]> {

    private final List<List<Byte>> sequences;

    /**
     * Instantiates a new callable responsible for selecting two sequences from the pool of sequences. The two
     * sequences will be chosen by their similarity to each other .
     *
     * @param sequences the input sequences
     */
    public LearnSelectCallable(final List<List<Byte>> sequences) {
        this.sequences = sequences;
    }

    @Override
    public int[] call() throws Exception {
        double[][] distances = new double[sequences.size()][sequences.size()];
        double[] avgDistances = new double[sequences.size()];
        int[] minIndices = new int[2];
        if (sequences.size() > 2) {
            for (int i = 0; i < distances.length; i++) {
                for (int j = i; j < distances[i].length; j++) {
                    if (i == j) {
                        distances[i][j] = 0;
                    } else {
                        distances[i][j] = getDiceValue(sequences.get(i), sequences.get(j));
                        distances[j][i] = distances[i][j];
                    }
                }
            }
            if (!Thread.currentThread().isInterrupted()) {
                // Calculate the average distances
                avgDistances = avgDistances(distances, sequences.size());
            }
            if (!Thread.currentThread().isInterrupted()) {
                // Calculate new distances
                distances = avgDistanceMatrix(distances, avgDistances);
            }
            if (!Thread.currentThread().isInterrupted()) {
                // Find minimum distance
                minIndices = minIndices(distances);
            }
        } else {
            minIndices[0] = 0;
            minIndices[1] = 1;
        }
        return minIndices;
    }

    /**
     * Gets the dice coefficient for two sequences.
     *
     * @param s1 the first sequence
     * @param s2 the second sequence
     * @return the dice coefficient
     */
    private double getDiceValue(final List<Byte> s1, final List<Byte> s2) {
        final Set<String> set1 = getSet(s1);
        final Set<String> set2 = getSet(s2);
        final Set<String> set3 = new HashSet<>(set1);
        set3.retainAll(set2);
        return (2.0 * set3.size()) / (set1.size() + set2.size());
    }

    /**
     * Gets the set of 3-grams for a given list of bytes.
     *
     * @param sequence the byte sequence
     * @return the set of string-encoded n-grams
     */
    private Set<String> getSet(final List<Byte> sequence) {
        final int ngram = 3;
        final StringBuilder fragment = new StringBuilder();
        final Set<String> set = new HashSet<>();
        for (int i = 0; i < sequence.size() + ngram - 1; i++) {
            fragment.delete(0, fragment.length());
            for (int j = i - ngram + 1; j <= i; j++) {
                if (j < 0 || j >= sequence.size()) {
                    fragment.append(" -");
                } else if (sequence.get(j) == null) {
                    fragment.append(" n");
                } else {
                    fragment.append(' ').append(sequence.get(j).toString());
                }
            }
            fragment.deleteCharAt(0);
            set.add(fragment.toString());
        }
        return set;
    }

    /**
     * Gets the average distances for all not yet learned sequences.
     *
     * @param distances the distance matrix with all distances
     * @param size      the number of all not yet learned sequences
     * @return the average distances of the sequences
     */
    private double[] avgDistances(final double[][] distances, final int size) {
        double[] avgDistances = new double[size];
        double sum;
        for (int i = 0; i < avgDistances.length; i++) {
            sum = 0;
            for (int j = 0; j < avgDistances.length; j++) {
                sum += distances[i][j];
            }
            avgDistances[i] = sum / (avgDistances.length - 2);
        }
        return avgDistances;
    }

    /**
     * Gets the distance matrix with temporary calculated distance values based on the average distances of the
     * sequences.
     *
     * @param distances    the current distance matrix
     * @param avgDistances the average distances for each sequence
     * @return the new calculated distance matrix
     */
    private double[][] avgDistanceMatrix(final double[][] distances, final double[] avgDistances) {
        for (int i = 0; i < distances.length; i++) {
            for (int j = i; j < distances[i].length; j++) {
                if (i == j) {
                    distances[i][j] = Double.MAX_VALUE;
                } else {
                    distances[i][j] -= avgDistances[i] + avgDistances[j];
                    distances[j][i] = distances[i][j];
                }
            }
        }
        return distances;
    }

    /**
     * Gets the two indices of the sequences that represent the two sequences with the lowest distance value.
     *
     * @param distances the distance matrix
     * @return the indices of the two minimal sequences
     */
    private int[] minIndices(final double[][] distances) {
        double minDistance = Double.MAX_VALUE;
        int[] minIndices = new int[2];
        for (int i = 0; i < distances.length; i++) {
            for (int j = 0; j < distances[i].length; j++) {
                if (distances[i][j] < minDistance) {
                    minDistance = distances[i][j];
                    minIndices[0] = Math.min(i, j);
                    minIndices[1] = Math.max(i, j);
                }
            }
        }
        return minIndices;
    }

}
