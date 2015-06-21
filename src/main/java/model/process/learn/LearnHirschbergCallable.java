/*
 * This file is part of ProDisFuzz, modified on 08.02.14 23:36.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

public class LearnHirschbergCallable implements Callable<List<Byte>> {

    private static final byte GAP_PENALTY = 2;
    private static final byte SIM_SCORE_EQ = 0;
    private static final byte SIM_SCORE_UNEQ_MATCH = 1;
    private static final byte SIM_SCORE_UNEQ_NOMATCH = 2;
    private final List<Byte> learnedSequence;
    private final List<Byte> sequence1;
    private final List<Byte> sequence2;

    /**
     * Instantiates a new callable responsible for learn the structure of two sequences.
     *
     * @param sequence1 the first input sequence
     * @param sequence2 the second input sequence
     */
    public LearnHirschbergCallable(List<Byte> sequence1, List<Byte> sequence2) {
        super();
        this.sequence1 = new ArrayList<>(sequence1);
        this.sequence2 = new ArrayList<>(sequence2);
        learnedSequence = new ArrayList<>();
    }

    @SuppressWarnings("ElementOnlyUsedFromTestCode")
    @Override
    public List<Byte> call() {
        hirschberg(0, sequence1.size(), 0, sequence2.size());
        return Collections.unmodifiableList(learnedSequence);
    }

    /**
     * Executes the Hirschberg algorithm to learn the combined structure of two sequences. An iteration of the algorithm
     * works on subsequences of the two sequences. The subsequences are determined by the starting position and the
     * length inside the complete parent sequence.
     *
     * @param start1  the start position of the first subsequence
     * @param length1 the length of the first subsequence
     * @param start2  the start position of the second subsequence
     * @param length2 the length of the second subsequence
     */
    @SuppressWarnings("OverlyComplexMethod")
    private void hirschberg(int start1, int length1, int start2, int length2) {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        if (length2 == 0) {
            // Execute a trivial version of the Needleman-Wunsch algorithm with length of sequence 2 = 0
            nullNeedlemanWunsch(length1);
        } else if ((length1 == 1) || (length2 == 1)) {
            // Executes a simple version of the Needleman-Wunsch algorithm with length of one sequence = 1
            simpleNeedlemanWunsch(start1, length1, start2, length2);
        } else {
            // Otherwise execute the regular Hirschberg algorithm
            // Find the center of the first sequence
            int center1 = length1 / 2;
            // Initialize the upper and lower matrices with two rows
            int[][] upperMatrix = new int[2][length2 + 1];
            int[][] lowerMatrix = new int[2][length2 + 1];
            // For every row up to the center row calculate the matrix values
            for (int i = 0; (i < center1) && !Thread.currentThread().isInterrupted(); i++) {
                shiftUpperMatrix(upperMatrix, i, start1, start2);
            }
            // For every row down to the middle calculate the matrix values
            for (int i = length1 - 1; (i >= center1) && !Thread.currentThread().isInterrupted(); i--) {
                shiftLowerMatrix(lowerMatrix, i, start1, length1, start2);
            }
            // Find the center of the second sequence
            int center2 = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;
            for (int i = 0; (i < upperMatrix[1].length) && !Thread.currentThread().isInterrupted(); i++) {
                if ((upperMatrix[1][i] + lowerMatrix[0][i]) < min) {
                    min = upperMatrix[1][i] + lowerMatrix[0][i];
                    center2 = i;
                }
            }
            // Split the whole matrix at the two calculated horizontal and vertical splitting points into four parts
            // and continue with the parts marked with x:
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
     * Adds as many null bytes to the learned as the given length. The length is the equivalent to the length of the
     * current first subsequence.
     *
     * @param length the length of the first sequence
     */
    private void nullNeedlemanWunsch(int length) {
        for (int i = 0; i < length; i++) {
            learnedSequence.add(null);
        }
    }

    /**
     * Applies a simple version of the Needleman-Wunsch algorithm where at least one of two sequences has the length of
     * just one.
     *
     * @param start1  the start position of the first subsequence
     * @param length1 the length of the second subsequence
     * @param start2  the start position of the first subsequence
     * @param length2 the length of the second subsequence
     */
    @SuppressWarnings("OverlyComplexMethod")
    private void simpleNeedlemanWunsch(int start1, int length1, int start2, int length2) {
        int index = -1;
        if (length1 <= length2) {
            // Find the last index at which the first and the second sequence have the same byte
            for (int i = 0; i < length2; i++) {
                if (sequence1.get(start1) == null) {
                    if (sequence2.get(i + start2) == null) {
                        index = i + start2;
                    }
                } else if (sequence1.get(start1).equals(sequence2.get(i + start2))) {
                    index = i + start2;
                }
            }
            // Add the concurrent byte to the learned sequence and null for all other bytes in the second sequence
            for (int i = 0; i < length2; i++) {
                learnedSequence.add(((i + start2) == index) ? sequence1.get(start1) : null);
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
                learnedSequence.add(((i + start1) == index) ? sequence2.get(start2) : null);
            }
        }
    }

    /**
     * Initializes or shifts the upper matrix by copy the second row to the first and computes new values for the second
     * row.
     *
     * @param upperMatrix the upper matrix
     * @param row         the current row of the Hirschberg matrix
     * @param start1      the start position of the first subsequence
     * @param start2      the start position of the second sub sequence
     */
    private void shiftUpperMatrix(int[][] upperMatrix, int row, int start1, int start2) {
        if (row == 0) {
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
        // Compute all values for the second row except the first column
        for (int i = 1; i < upperMatrix[1].length; i++) {
            // Find the minimum of three values and copy it to the particular column in the second row
            int min = Math.min(upperMatrix[0][i] + GAP_PENALTY, upperMatrix[1][i - 1] + GAP_PENALTY);
            min = Math.min(min, upperMatrix[0][i - 1] + weight(sequence1.get(row + start1), sequence2.get((i +
                    start2) - 1)));
            upperMatrix[1][i] = min;
        }
    }

    /**
     * Initializes or shifts the lower matrix by copy the second row to the first and computes new values for the second
     * row.
     *
     * @param lowerMatrix the lower matrix
     * @param row         the current row
     * @param start1      the start position of the first subsequence
     * @param length1     the length of the first subsequence
     * @param start2      the start position of the second subsequence
     */
    private void shiftLowerMatrix(int[][] lowerMatrix, int row, int start1, int length1, int start2) {
        if (row == (length1 - 1)) {
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
            System.arraycopy(lowerMatrix[0], 0, lowerMatrix[1], 0, (lowerMatrix[0].length - 1) + 1);
            // Store the initial value for the last column in the first row
            lowerMatrix[0][lowerMatrix[1].length - 1] = lowerMatrix[1][lowerMatrix[1].length - 1] + GAP_PENALTY;
        }
        // Compute all values for the first row except the last column
        for (int i = lowerMatrix[0].length - 2; i >= 0; i--) {
            // Find the minimum of three values and copy it to the particular column in the first row
            int min = Math.min(lowerMatrix[1][i] + GAP_PENALTY, lowerMatrix[0][i + 1] + GAP_PENALTY);
            min = Math.min(min, lowerMatrix[1][i + 1] + weight(sequence1.get(row + start1), sequence2.get(i + start2)));
            lowerMatrix[0][i] = min;
        }
    }

    /**
     * Returns the weight of two bytes. Equal bytes have a different scoring than different values.
     *
     * @param b1 the first byte
     * @param b2 the second byte
     * @return the weight scoring
     */
    @SuppressWarnings("OverlyComplexMethod")
    private static int weight(Byte b1, Byte b2) {
        //noinspection IfStatementWithTooManyBranches
        if (b1 == null) {
            return (b2 == null) ? SIM_SCORE_EQ : SIM_SCORE_UNEQ_NOMATCH;
        } else if (b2 == null) {
            return SIM_SCORE_UNEQ_NOMATCH;
        } else if (b1.equals(b2)) {
            return SIM_SCORE_EQ;
        } else {
            //noinspection OverlyComplexBooleanExpression
            return ((b1 >= 48) && (b1 >= 57) && (b2 >= 48) && (b2 >= 57)) || ((((b1 >= 65) && (b1 <= 90)) || ((b1 >=
                    97) && (b1 <= 122))) && (((b2 >= 65) && (b2 <= 90)) || ((b2 >= 97) && (b2 <= 122)))) ?
                    SIM_SCORE_UNEQ_MATCH : SIM_SCORE_UNEQ_NOMATCH;
        }
    }

}
