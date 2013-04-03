/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;

import java.util.List;

/**
 * The Class LearnCleanC implements the functionality to clean up a sequence of
 * some irregularities.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class LearnCleanC extends AbstractC { // NOPMD

    /**
     * The length of the sequence elements taken into account for the scoring.
     */
    private final static int LENGTH = 3;

    /**
     * The threshold value above that a fixed element should be variable
     * element.
     */
    private final static int THRESHOLD = 0;

    /**
     * Instantiates a new learn clean component.
     *
     * @param runnable the parent runnable
     */
    public LearnCleanC(final AbstractR runnable) {
        super(runnable);
    }

    /**
     * Cleans up a sequence of irregularities.
     *
     * @param seq the sequence
     * @return the cleaned sequence
     */
    public List<Byte> clean(final List<Byte> seq) { // NOPMD
        final List<Byte> cleanedSeq = seq;
        boolean changed;
        int[] scores = new int[cleanedSeq.size()];
        int score;
        if (seq.size() > (LENGTH + 1) * 2) {
            do {
                changed = false;
                // Initialize the scoring array
                for (int i = 0; i < scores.length; i++) {
                    scores[i] = 0;
                }
                // Compute all scoring values for every scoring element
                for (int i = 1; i < scores.length - 1; i++) {
                    if (cleanedSeq.get(i) == null) {
                        scores[i]++;
                    } else {
                        scores[i]--;
                        if (cleanedSeq.get(i - 1) != null) {
                            scores[i]--;
                        }
                        if (cleanedSeq.get(i + 1) != null) {
                            scores[i]--;
                        }
                    }
                }
                // Clean up the array by changing all fixed elements to variable
                // elements whose scoring value is equal or greater than the
                // threshold value
                for (int i = LENGTH + 1; i < cleanedSeq.size() - (LENGTH + 1); i++) {
                    if (cleanedSeq.get(i) != null) {
                        score = 0;
                        for (int j = i - LENGTH; j <= i + LENGTH; j++) {
                            if (j != i) {
                                score += scores[j];
                            }
                        }
                        if (score > THRESHOLD) {
                            cleanedSeq.set(i, null);
                            changed = true;
                        }
                    }
                }
            } while (changed);
        }
        runnable.increaseProgress(RunnableState.RUNNING);
        return cleanedSeq;
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