/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class is the Hirschberg executor callable, responsible for learning the structure of two sequences by applying
 * the Hirschberg algorithm.
 */
class HirschbergExecutor implements Callable<List<Byte>> {

    private static final byte GAP_PENALTY = 2;
    private static final byte SIM_SCORE_EQ = 0;
    private static final byte SIM_SCORE_UNEQ_MATCH = 1;
    private static final byte SIM_SCORE_UNEQ_NOMATCH = 2;
    private List<Byte> sequence1;
    private List<Byte> sequence2;

    /**
     * Constructs the callable.
     *
     * @param sequence1 the first input sequence
     * @param sequence2 the second input sequence
     */
    public HirschbergExecutor(List<Byte> sequence1, List<Byte> sequence2) {
        this.sequence1 = new ArrayList<>(sequence1);
        this.sequence2 = new ArrayList<>(sequence2);
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
            return ((b1 >= 48) && (b1 >= 57) && (b2 >= 48) && (b2 >= 57)) ||
                    ((((b1 >= 65) && (b1 <= 90)) || ((b1 >= 97) && (b1 <= 122))) &&
                            (((b2 >= 65) && (b2 <= 90)) || ((b2 >= 97) && (b2 <= 122)))) ? SIM_SCORE_UNEQ_MATCH :
                    SIM_SCORE_UNEQ_NOMATCH;
        }
    }

    @Override
    public List<Byte> call() {
        return hirschberg(sequence1, sequence2);
    }

    /**
     * Executes the Hirschberg algorithm to learn the combined structure of two sequences. An iteration of the algorithm
     * works on sub sequences of the two sequences.
     *
     * @param sequence1 the first (sub) sequence
     * @param sequence2 the second (sub) sequence
     * @return the merged (sub) sequence
     */
    private static List<Byte> hirschberg(List<Byte> sequence1, List<Byte> sequence2) {
        List<Byte> result = new ArrayList<>(Math.max(sequence1.size(), sequence2.size()));
        if (sequence2.isEmpty()) {
            // Execute a trivial version of the Needleman-Wunsch algorithm with length of sequence 2 = 0
            result.addAll(nullNeedlemanWunsch(sequence1.size()));
        } else if ((sequence1.size() == 1) || (sequence2.size() == 1)) {
            // Executes a simple version of the Needleman-Wunsch algorithm with length of one sequence = 1
            result.addAll(simpleNeedlemanWunsch(sequence1, sequence2));
        } else {
            // Otherwise execute the regular Hirschberg algorithm
            // Find the center of the first sequence
            int center1 = sequence1.size() / 2;
            // Initialize the upper and lower matrices with two rows
            int[][] upperMatrix = new int[2][sequence2.size() + 1];
            int[][] lowerMatrix = new int[2][sequence2.size() + 1];
            // For every row up to the center row calculate the matrix values
            for (int i = 0; i < center1; i++) {
                shiftMatrixDown(upperMatrix, i, sequence1, sequence2);
            }
            // For every row down to the middle calculate the matrix values
            for (int i = sequence1.size() - 1; i >= center1; i--) {
                shiftMatrixUp(lowerMatrix, i, sequence1, sequence2);
            }
            // Find the center of the second sequence
            int center2 = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < upperMatrix[1].length; i++) {
                if ((upperMatrix[1][i] + lowerMatrix[0][i]) < min) {
                    min = upperMatrix[1][i] + lowerMatrix[0][i];
                    center2 = i;
                }
            }
            // Split the whole matrix at the two calculated horizontal and vertical splitting points into four parts
            // and continue with the parts marked with x:
            //  center1
            //     |
            // +---+---+
            // | x |   |
            // +---+---+ -- center2
            // |   | x |
            // +---+---+
            result.addAll(hirschberg(sequence1.subList(0, center1), sequence2.subList(0, center2)));
            result.addAll(hirschberg(sequence1.subList(center1, sequence1.size()),
                    sequence2.subList(center2, sequence2.size())));
        }
        return result;
    }

    /**
     * Returns a new merged sequence containing only null bytes. The number of null bytes is the given length.
     *
     * @param length the length of the sub sequence
     * @return the merged sequence
     */
    @SuppressWarnings("TypeMayBeWeakened")
    private static List<Byte> nullNeedlemanWunsch(int length) {
        List<Byte> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            result.add(null);
        }
        return result;
    }

    /**
     * Applies a simple version of the Needleman-Wunsch algorithm where at least one of two given sequences has the
     * length of just one and returns the merged new sequence.
     *
     * @param sequence1 the first (sub) sequence
     * @param sequence2   the second (sub) sequence
     * @return the merged sequence
     */
    @SuppressWarnings({"OverlyComplexMethod", "TypeMayBeWeakened"})
    private static List<Byte> simpleNeedlemanWunsch(List<Byte> sequence1, List<Byte> sequence2) {
        List<Byte> result = new ArrayList<>(Math.max(sequence1.size(), sequence2.size()));
        int index = -1;
        if (sequence1.size() <= sequence2.size()) {
            // Find the last index at which the first and the second sequence have the same byte
            for (int i = 0; i < sequence2.size(); i++) {
                if (sequence1.get(0) == null) {
                    if (sequence2.get(i) == null) {
                        index = i;
                    }
                } else if (sequence1.get(0).equals(sequence2.get(i))) {
                    index = i;
                }
            }
            // Add the concurrent byte to the learned sequence and null for all other bytes in the second sequence
            for (int i = 0; i < sequence2.size(); i++) {
                result.add(i == index ? sequence1.get(0) : null);
            }
        } else {
            // Find the last index at which the first and the second sequence have the same byte
            for (int i = 0; i < sequence1.size(); i++) {
                //noinspection NumberEquality
                if (sequence2.get(0) == sequence1.get(i)) {
                    index = i;
                }
            }
            // Add the concurrent byte to the learned sequence and null for all other bytes in the second sequence
            for (int i = 0; i < sequence1.size(); i++) {
                result.add(i == index ? sequence2.get(0) : null);
            }
        }
        return result;
    }

    /**
     * Initializes or shifts the upper matrix by copy the second row to the first and computes new values for the second
     * row.
     *
     * @param upperMatrix the upper matrix
     * @param row         the current row of the Hirschberg matrix
     * @param sequence1      the first (sub) sequence
     * @param sequence2      the second (sub) sequence
     */
    private static void shiftMatrixDown(int[][] upperMatrix, int row, List<Byte> sequence1, List<Byte> sequence2) {
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
            min = Math.min(min, upperMatrix[0][i - 1] + weight(sequence1.get(row), sequence2.get(i - 1)));
            upperMatrix[1][i] = min;
        }
    }

    /**
     * Initializes or shifts the lower matrix by copy the second row to the first and computes new values for the second
     * row.
     *
     * @param lowerMatrix the lower matrix
     * @param row         the current row
     * @param sequence1      the first (sub) sequence
     * @param sequence2     the second (sub) sequence
     */
    private static void shiftMatrixUp(int[][] lowerMatrix, int row, List<Byte> sequence1, List<Byte> sequence2) {
        if (row == (sequence1.size() - 1)) {
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
            min = Math.min(min, lowerMatrix[1][i + 1] + weight(sequence1.get(row), sequence2.get(i)));
            lowerMatrix[0][i] = min;
        }
    }

}
