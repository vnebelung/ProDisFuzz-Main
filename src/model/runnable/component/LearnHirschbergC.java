/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class LearnHirschbergC implements the functionality to learn the protocol
 * structure with the Hirschberg algorithm.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class LearnHirschbergC extends AbstractC { // NOPMD

    /**
     * The gap penalty for the Hirschberg/Needleman-Wunsch algorithm
     */
    private final static byte GAP_PENALTY = 2;

    /**
     * The similarity weight for two equal elements for the
     * Hirschberg/Needleman-Wunsch algorithm.
     */
    private final static byte SIM_SCORE_EQ = 0;

    /**
     * The similarity weight for two unequal elements for the
     * Hirschberg/Needleman-Wunsch algorithm.
     */
    private final static byte SIM_SCORE_UNEQ_MATCH = 1; // NOPMD

    /**
     * The similarity weight for two unequal elements for the
     * Hirschberg/Needleman-Wunsch algorithm.
     */
    private final static byte SIM_SCORE_UNEQ_NOMATCH = 2; // NOPMD

    /**
     * The generated sequence which contains fixed and variable parts.
     */
    private final List<Byte> sequence;

    /**
     * The sequence of the first file that is being compared.
     */
    private List<Byte> seq1;

    /**
     * The sequence of the second file that is being compared.
     */
    private List<Byte> seq2;

    /**
     * Instantiates a new learn hirschberg component.
     *
     * @param runnable the parent runnable
     */
    public LearnHirschbergC(final AbstractR runnable) {
        super(runnable);
        sequence = new ArrayList<Byte>();
        seq1 = new ArrayList<Byte>();
        seq2 = new ArrayList<Byte>();
    }

    /**
     * Executes the Hirschberg algorithm for finding an optimal alignment for
     * two sequences.
     *
     * @param seq1 the sequence 1
     * @param seq2 the sequence 2
     * @return the learned sequence
     */
    public List<Byte> learn(final List<Byte> seq1, final List<Byte> seq2) {
        this.seq1 = seq1;
        this.seq2 = seq2;
        sequence.clear();
        executeHirschberg(0, seq1.size(), 0, seq2.size());
        runnable.increaseProgress(RunnableState.RUNNING);
        return sequence;
    }

    /**
     * Executes the Hirschberg algorithm to learn the sequence of two Byte
     * lists.
     *
     * @param start1  the start position of sequence 1
     * @param length1 the length of sequence 1
     * @param start2  the start position of sequence 2
     * @param length2 the length of sequence 2
     */
    private void executeHirschberg(final int start1, final int length1,
                                   final int start2, final int length2) {
        if (!runnable.isInterrupted()) {
            if (length2 == 0) {
                // Execute a trivial version of the Needleman-Wunsch algorithm
                // with
                // the length of sequence 2 = 0
                executeNullNeedlemanWunsch(length1);
            } else if (length1 == 1 || length2 == 1) {
                // Executes a simple version of the Needleman-Wunsch algorithm
                // where
                // the length of one sequence is 1
                executeSimpleNeedlemanWunsch(start1, length1, start2, length2);
            } else {
                // Otherwise execute the regular Hirschberg algorithm
                // Find the middle of sequence 1
                final int center1 = length1 / 2;
                // Initialize the upper and lower matrices with two rows
                final int[][] upperMatrix = new int[2][length2 + 1];
                final int[][] lowerMatrix = new int[2][length2 + 1];
                // For every row up to the center row calculate the matrix
                // values
                for (int i = 0; i < center1 && !runnable.isInterrupted(); i++) {
                    shiftUpperMatrix(upperMatrix, i, seq1, start1, seq2, start2);
                }
                // For every row down to the middle calculate the matrix values
                for (int i = length1 - 1; i >= center1
                        && !runnable.isInterrupted(); i--) {
                    shiftLowerMatrix(lowerMatrix, i, seq1, start1, length1,
                            seq2, start2);

                }
                // Determine the center of sequence 2
                int center2 = Integer.MIN_VALUE;
                int min = Integer.MAX_VALUE;
                for (int i = 0; i < upperMatrix[1].length
                        && !runnable.isInterrupted(); i++) {
                    if (upperMatrix[1][i] + lowerMatrix[0][i] < min) {
                        min = upperMatrix[1][i] + lowerMatrix[0][i];
                        center2 = i;
                    }
                }
                // Split the whole matrix at the two calculated center points
                // into
                // four parts and continue with the parts marked wit x:
                // +---+---+
                // | x | - |
                // +---+---+
                // | - | x |
                // +---+---+
                executeHirschberg(start1, center1, start2, center2);
                executeHirschberg(start1 + center1, length1 - center1, start2
                        + center2, length2 - center2);
            }
        }
    }

    /**
     * Initializes or shifts the lower matrix by copy the second row to the
     * first and computes new values for the second row.
     *
     * @param lowerMatrix the lower matrix
     * @param currentRow  the current row
     * @param seq1        the sequence 1
     * @param start1      the start position of sequence 1
     * @param length1     the length of sequence 1
     * @param seq2        the sequence 2
     * @param start2      the start position of sequence 2
     */
    private void shiftLowerMatrix(final int[][] lowerMatrix,
                                  final int currentRow, final List<Byte> seq1, final int start1,
                                  final int length1, final List<Byte> seq2, final int start2) {
        if (currentRow == length1 - 1) {
            // Store the initial values in the second row of the matrix
            // similar to:
            // ... x x x x x 1
            // ... 5 4 3 2 1 0
            lowerMatrix[1][lowerMatrix[1].length - 1] = 0;
            lowerMatrix[0][lowerMatrix[0].length - 1] = lowerMatrix[1][lowerMatrix[1].length - 1]
                    + GAP_PENALTY;
            for (int i = lowerMatrix[1].length - 2; i >= 0; i--) {
                lowerMatrix[1][i] = lowerMatrix[1][i + 1] + GAP_PENALTY;
            }
        } else {
            // Copy the first row to the second row
            for (int i = lowerMatrix[0].length - 1; i >= 0; i--) {
                lowerMatrix[1][i] = lowerMatrix[0][i];
            }
            lowerMatrix[0][lowerMatrix[1].length - 1] = lowerMatrix[1][lowerMatrix[1].length - 1]
                    + GAP_PENALTY;
        }
        // Compute all values for the first row except the last column
        int min;
        for (int i = lowerMatrix[0].length - 2; i >= 0; i--) {
            // Find the minimum of three values and copy it to the
            // particular column in the first row
            min = Math.min(lowerMatrix[1][i] + GAP_PENALTY,
                    lowerMatrix[0][i + 1] + GAP_PENALTY);
            min = Math.min(
                    min,
                    lowerMatrix[1][i + 1]
                            + weight(seq1.get(currentRow + start1),
                            seq2.get(i + start2)));
            lowerMatrix[0][i] = min;
        }
    }

    /**
     * Initializes or shifts the upper matrix by copy the second row to the
     * first and computes new values for the second row.
     *
     * @param upperMatrix the upper matrix
     * @param currentRow  the current row of the Hirschberg matrix
     * @param seq1        the sequence 1
     * @param start1      the start position of sequence 1
     * @param seq2        the sequence 2
     * @param start2      the start position of sequence 2
     */
    private void shiftUpperMatrix(final int[][] upperMatrix,
                                  final int currentRow, final List<Byte> seq1, final int start1,
                                  final List<Byte> seq2, final int start2) {
        if (currentRow == 0) {
            // Store the initial values in the first row of the matrix
            // similar to:
            // 0 1 2 3 4 5 ...
            // 0 x x x x x ...
            upperMatrix[0][0] = 0;
            upperMatrix[1][0] = upperMatrix[0][0] + GAP_PENALTY;
            for (int i = 1; i < upperMatrix[0].length; i++) {
                upperMatrix[0][i] = upperMatrix[0][i - 1] + GAP_PENALTY;
            }
        } else {
            // Copy the second row to the first row
            for (int i = 0; i < upperMatrix[0].length; i++) {
                upperMatrix[0][i] = upperMatrix[1][i];
            }
            // Store the initial value for the fist column in the second
            // row
            upperMatrix[1][0] = upperMatrix[0][0] + GAP_PENALTY;
        }
        int min;
        // Compute all values for the second row except the first column
        for (int i = 1; i < upperMatrix[1].length; i++) {
            // Find the minimum of three values and copy it to the
            // particular column in the second row
            min = Math.min(upperMatrix[0][i] + GAP_PENALTY,
                    upperMatrix[1][i - 1] + GAP_PENALTY);
            min = Math.min(
                    min,
                    upperMatrix[0][i - 1]
                            + weight(seq1.get(currentRow + start1),
                            seq2.get(i + start2 - 1)));
            upperMatrix[1][i] = min;
        }
    }

    /**
     * Adds null bytes to the sequence according to the length of sequence one.
     *
     * @param length1 the length of sequence one
     */
    private void executeNullNeedlemanWunsch(final int length1) {
        for (int i = 0; i < length1; i++) {
            sequence.add(null);
        }
    }

    /**
     * Executes a simple version of the Needleman-Wunsch algorithm where at
     * least one of two sequences has the length of just one.
     *
     * @param start1  the start position of sequence one
     * @param length1 the length of sequence one
     * @param start2  the start position of sequence two
     * @param length2 the length of sequence two
     */
    private void executeSimpleNeedlemanWunsch(final int start1, // NOPMD
                                              final int length1, final int start2, final int length2) {
        int index = -1;
        if (length1 <= length2) {
            // Find the last index at which sequence 1 and sequence 2 have the
            // same byte
            for (int i = 0; i < length2; i++) {
                if (seq1.get(start1) == null) {
                    if (seq2.get(i + start2) == null) {
                        index = i + start2;
                    }
                } else if (seq1.get(start1).equals(seq2.get(i + start2))) {
                    index = i + start2;
                }
            }
            // Add the concurrent byte to the sequence and null for all other
            // bytes in sequence 2
            for (int i = 0; i < length2; i++) {
                sequence.add(i + start2 == index ? seq1.get(start1) : null);
            }
        } else {
            // Find the last index at which sequence 1 and sequence 2 have the
            // same byte
            for (int i = 0; i < length1; i++) {
                if (seq2.get(start2) == seq1.get(i + start1)) {
                    index = i + start1;
                }
            }
            // Add the concurrent byte to the sequence and null for all other
            // bytes in sequence 1
            for (int i = 0; i < length1; i++) {
                sequence.add(i + start1 == index ? seq2.get(start2) : null);
            }
        }
    }

    /**
     * Returns the weight of two bytes. If byte one and byte two are even they
     * have a different scoring than with unequal values.
     *
     * @param byte1 the first byte
     * @param byte2 the second byte
     * @return the weight scoring
     */
    private int weight(final Byte byte1, final Byte byte2) { // NOPMD
        int weight;
        if (byte1 == null) {
            weight = (byte2 == null ? SIM_SCORE_EQ : SIM_SCORE_UNEQ_NOMATCH);
        } else {
            if (byte2 == null) {
                weight = SIM_SCORE_UNEQ_NOMATCH;
            } else if (byte1.equals(byte2)) {
                weight = SIM_SCORE_EQ;
            } else {
                if ((byte1 >= 48 && byte1 >= 57 && byte2 >= 48 && byte2 >= 57)
                        || (((byte1 >= 65 && byte1 <= 90) || (byte1 >= 97 && byte1 <= 122)) && ((byte2 >= 65 && byte2 <= 90) || (byte2 >= 97 && byte2 <= 122)))) {
                    weight = SIM_SCORE_UNEQ_MATCH;
                } else {
                    weight = SIM_SCORE_UNEQ_NOMATCH;
                }
            }
        }
        // if (byte1 == null) {
        // weight = (byte2 == null ? SIMILARITY_SCORE_EQ
        // : SIMILARITY_SCORE_UNEQ);
        // } else {
        // weight = (byte1.equals(byte2) ? SIMILARITY_SCORE_EQ
        // : SIMILARITY_SCORE_UNEQ);
        // }
        // return weight;
        return weight;
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.component.AbstractComponent#getTotalProgress()
     */
    @Override
    public int getTotalProgress() {
        return 1;
    }

}