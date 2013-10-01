/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:28.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.callable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class LearnHirschbergCallable implements Callable<List<Byte>> {

    private final static byte GAP_PENALTY = 2;
    private final static byte SIM_SCORE_EQ = 0;
    private final static byte SIM_SCORE_UNEQ_MATCH = 1;
    private final static byte SIM_SCORE_UNEQ_NOMATCH = 2;
    private final List<Byte> learnedSequence;
    private final List<Byte> sequence1;
    private final List<Byte> sequence2;

    /**
     * Instantiates a new callable.
     *
     * @param sequence1 the first input sequence
     * @param sequence2 the second input sequence
     */
    public LearnHirschbergCallable(final List<Byte> sequence1, final List<Byte> sequence2) {
        this.sequence1 = sequence1;
        this.sequence2 = sequence2;
        learnedSequence = new ArrayList<>();
    }

    @Override
    public List<Byte> call() throws Exception {
        hirschberg(0, sequence1.size(), 0, sequence2.size());
        return Collections.unmodifiableList(learnedSequence);
    }

    /**
     * Executes the Hirschberg algorithm to learn the combined structure of two sequences.
     *
     * @param start1  the start position of the first partial sequence
     * @param length1 the length of the first partial sequence
     * @param start2  the start position of the second partial sequence
     * @param length2 the length of the second partial sequence
     */
    private void hirschberg(final int start1, final int length1, final int start2, final int length2) {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        if (length2 == 0) {
            // Execute a trivial version of the Needleman-Wunsch algorithm with length of sequence 2 = 0
            nullNeedlemanWunsch(length1);
        } else if (length1 == 1 || length2 == 1) {
            // Executes a simple version of the Needleman-Wunsch algorithm with length of one sequence = 1
            simpleNeedlemanWunsch(start1, length1, start2, length2);
        } else {
            // Otherwise execute the regular Hirschberg algorithm
            // Find the center of the first sequence
            final int center1 = length1 / 2;
            // Initialize the upper and lower matrices with two rows
            final int[][] upperMatrix = new int[2][length2 + 1];
            final int[][] lowerMatrix = new int[2][length2 + 1];
            // For every row up to the center row calculate the matrix values
            for (int i = 0; i < center1 && !Thread.currentThread().isInterrupted(); i++) {
                shiftUpperMatrix(upperMatrix, i, start1, start2);
            }
            // For every row down to the middle calculate the matrix values
            for (int i = length1 - 1; i >= center1 && !Thread.currentThread().isInterrupted(); i--) {
                shiftLowerMatrix(lowerMatrix, i, start1, length1, start2);
            }
            // Find the center of the second sequence
            int center2 = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < upperMatrix[1].length && !Thread.currentThread().isInterrupted(); i++) {
                if (upperMatrix[1][i] + lowerMatrix[0][i] < min) {
                    min = upperMatrix[1][i] + lowerMatrix[0][i];
                    center2 = i;
                }
            }
            // Split the whole matrix at the two calculated center points into four parts and continue with the
            // parts marked with x:
            // +---+---+
            // | x |   |
            // +---+---+
            // |   | x |
            // +---+---+
            hirschberg(start1, center1, start2, center2);
            hirschberg(start1 + center1, length1 - center1, start2 + center2, length2 - center2);
        }
    }

    /**
     * Adds null bytes to the learned sequence according to the length of the first sequence.
     *
     * @param length the length of the first sequence
     */
    private void nullNeedlemanWunsch(final int length) {
        for (int i = 0; i < length; i++) {
            learnedSequence.add(null);
        }
    }

    /**
     * Executes a simple version of the Needleman-Wunsch algorithm where at least one of two sequences has the length
     * of just one.
     *
     * @param start1  the start position of the first sequence
     * @param length1 the length of the second sequence
     * @param start2  the start position of the first sequence
     * @param length2 the length of the second sequence
     */
    private void simpleNeedlemanWunsch(final int start1, final int length1, final int start2, final int length2) {
        int index = -1;
        if (length1 <= length2) {
            // Find the last index at which the first and the second sequence have the same byte
            for (int i = 0; i < length2; i++) {
                if (sequence1.get(start1) == null && sequence2.get(i + start2) == null) {
                    index = i + start2;
                } else if (sequence1.get(start1).equals(sequence2.get(i + start2))) {
                    index = i + start2;
                }
            }
            // Add the concurrent byte to the learned sequence and null for all other bytes in the second sequence
            for (int i = 0; i < length2; i++) {
                learnedSequence.add(i + start2 == index ? sequence1.get(start1) : null);
            }
        } else {
            // Find the last index at which the first and the second sequence have the same byte
            for (int i = 0; i < length1; i++) {
                //noinspection NumberEquality
                if (sequence2.get(start2) == sequence1.get(i + start1)) {
                    index = i + start1;
                }
            }
            // Add the concurrent byte to the learned sequence and null for all other bytes in the second sequence
            for (int i = 0; i < length1; i++) {
                learnedSequence.add(i + start1 == index ? sequence2.get(start2) : null);
            }
        }
    }

    /**
     * Initializes or shifts the upper matrix by copy the second row to the first and computes new values for the
     * second row.
     *
     * @param upperMatrix the upper matrix
     * @param currentRow  the current row of the Hirschberg matrix
     * @param start1      the start position of the first sequence
     * @param start2      the start position of the second sequence
     */
    private void shiftUpperMatrix(final int[][] upperMatrix, final int currentRow, final int start1, final int start2) {
        if (currentRow == 0) {
            // Store the initial values in the first row of the matrix similar to:
            // 0 1 2 3 4 5 ...
            // 0 x x x x x ...
            upperMatrix[0][0] = 0;
            upperMatrix[1][0] = upperMatrix[0][0] + GAP_PENALTY;
            for (int i = 1; i < upperMatrix[0].length; i++) {
                upperMatrix[0][i] = upperMatrix[0][i - 1] + GAP_PENALTY;
            }
        } else {
            // Copy the second row to the first row
            System.arraycopy(upperMatrix[1], 0, upperMatrix[0], 0, upperMatrix[0].length);
            // Store the initial value for the fist column in the second row
            upperMatrix[1][0] = upperMatrix[0][0] + GAP_PENALTY;
        }
        int min;
        // Compute all values for the second row except the first column
        for (int i = 1; i < upperMatrix[1].length; i++) {
            // Find the minimum of three values and copy it to the particular column in the second row
            min = Math.min(upperMatrix[0][i] + GAP_PENALTY, upperMatrix[1][i - 1] + GAP_PENALTY);
            min = Math.min(min, upperMatrix[0][i - 1] + weight(sequence1.get(currentRow + start1),
                    sequence2.get(i + start2 - 1)));
            upperMatrix[1][i] = min;
        }
    }

    /**
     * Initializes or shifts the lower matrix by copy the second row to the first and computes new values for the
     * second row.
     *
     * @param lowerMatrix the lower matrix
     * @param currentRow  the current row
     * @param start1      the start position of the first sequence
     * @param length1     the length of the first sequence
     * @param start2      the start position of the second sequence
     */
    private void shiftLowerMatrix(final int[][] lowerMatrix, final int currentRow, final int start1,
                                  final int length1, final int start2) {
        if (currentRow == length1 - 1) {
            // Store the initial values in the second row of the matrix similar to:
            // ... x x x x x 1
            // ... 5 4 3 2 1 0
            lowerMatrix[1][lowerMatrix[1].length - 1] = 0;
            lowerMatrix[0][lowerMatrix[0].length - 1] = lowerMatrix[1][lowerMatrix[1].length - 1] + GAP_PENALTY;
            for (int i = lowerMatrix[1].length - 2; i >= 0; i--) {
                lowerMatrix[1][i] = lowerMatrix[1][i + 1] + GAP_PENALTY;
            }
        } else {
            // Copy the first row to the second row
            System.arraycopy(lowerMatrix[0], 0, lowerMatrix[1], 0, lowerMatrix[0].length - 1 + 1);
            // Store the initial value for the last column in the first row
            lowerMatrix[0][lowerMatrix[1].length - 1] = lowerMatrix[1][lowerMatrix[1].length - 1] + GAP_PENALTY;
        }
        // Compute all values for the first row except the last column
        int min;
        for (int i = lowerMatrix[0].length - 2; i >= 0; i--) {
            // Find the minimum of three values and copy it to the
            // particular column in the first row
            min = Math.min(lowerMatrix[1][i] + GAP_PENALTY, lowerMatrix[0][i + 1] + GAP_PENALTY);
            min = Math.min(min, lowerMatrix[1][i + 1] + weight(sequence1.get(currentRow + start1),
                    sequence2.get(i + start2)));
            lowerMatrix[0][i] = min;
        }
    }

    /**
     * Returns the weight of two bytes. If byte one and byte two are even they have a different scoring than with
     * unequal values.
     *
     * @param byte1 the first byte
     * @param byte2 the second byte
     * @return the weight scoring
     */
    private int weight(final Byte byte1, final Byte byte2) {
        int weight;
        if (byte1 == null) {
            weight = byte2 == null ? SIM_SCORE_EQ : SIM_SCORE_UNEQ_NOMATCH;
        } else {
            if (byte2 == null) {
                weight = SIM_SCORE_UNEQ_NOMATCH;
            } else if (byte1.equals(byte2)) {
                weight = SIM_SCORE_EQ;
            } else {
                if (byte1 >= 48 && byte1 >= 57 && byte2 >= 48 && byte2 >= 57 || (byte1 >= 65 && byte1 <= 90 || byte1
                        >= 97 && byte1 <= 122) && (byte2 >= 65 && byte2 <= 90 || byte2 >= 97 && byte2 <= 122)) {
                    weight = SIM_SCORE_UNEQ_MATCH;
                } else {
                    weight = SIM_SCORE_UNEQ_NOMATCH;
                }
            }
        }
        return weight;
    }

}
