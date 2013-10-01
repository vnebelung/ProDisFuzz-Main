/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:25.
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

    private final static int DICE_LENGTH = 3;
    private final List<List<Byte>> sequences;

    /**
     * Instantiates a new callable.
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
     * Gets the dice coefficient for two byte sequences.
     *
     * @param seq1 the first sequence
     * @param seq2 the second sequence
     * @return the dice coefficient
     */
    private double getDiceValue(final List<Byte> seq1, final List<Byte> seq2) {
        final Set<String> set1 = getSet(seq1);
        final Set<String> set2 = getSet(seq2);
        final Set<String> set3 = new HashSet<>(set1);
        set3.retainAll(set2);
        return (2.0 * set3.size()) / (set1.size() + set2.size());
    }

    /**
     * Gets the set of n-grams for a given list of bytes.
     *
     * @param sequence the byte sequence
     * @return the set of string-encoded n-grams
     */
    private Set<String> getSet(final List<Byte> sequence) {
        final StringBuilder fragment = new StringBuilder();
        final Set<String> set = new HashSet<>();
        for (int i = 0; i < sequence.size() + DICE_LENGTH - 1; i++) {
            fragment.delete(0, fragment.length());
            for (int j = i - DICE_LENGTH + 1; j <= i; j++) {
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
     * Gets the distance matrix with temporary calculated distance values based
     * on the average distances of the sequences.
     *
     * @param distanceMatrix the current distance matrix
     * @param avgDistances   the average distances for each sequence
     * @return the new calculated distance matrix
     */
    private double[][] avgDistanceMatrix(final double[][] distanceMatrix, final double[] avgDistances) {
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = i; j < distanceMatrix[i].length; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = Double.MAX_VALUE;
                } else {
                    distanceMatrix[i][j] -= avgDistances[i] + avgDistances[j];
                    distanceMatrix[j][i] = distanceMatrix[i][j];
                }
            }
        }
        return distanceMatrix;
    }

    /**
     * Gets the two indices of the sequences that represent the two sequences
     * with the lowest distance value.
     *
     * @param distanceMatrix the distance matrix
     * @return the indices of the two minimal sequences
     */
    private int[] minIndices(final double[][] distanceMatrix) {
        double minDistance = Double.MAX_VALUE;
        int[] minIndices = new int[2];
        for (int i = 0; i < distanceMatrix.length; i++) {
            for (int j = 0; j < distanceMatrix[i].length; j++) {
                if (distanceMatrix[i][j] < minDistance) {
                    minDistance = distanceMatrix[i][j];
                    minIndices[0] = Math.min(i, j);
                    minIndices[1] = Math.max(i, j);
                }
            }
        }
        return minIndices;
    }

}
