/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The Class LearnDiceC implements the functionality to compute the dice
 * coefficient for two Byte sequences.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class LearnDiceC extends AbstractC {

    /**
     * The length of the fragments of the n-gramm.
     */
    private final static int FRAGMENT_LENGTH = 3;

    /**
     * Instantiates a new learn dice component.
     *
     * @param runnable the parent runnable
     */
    public LearnDiceC(final AbstractR runnable) {
        super(runnable);
    }

    /**
     * Gets the dice coefficient for two Byte lists.
     *
     * @param seq1 the sequence one
     * @param seq2 the sequence two
     * @return the dice coefficient
     */
    public double getDiceValue(final List<Byte> seq1, final List<Byte> seq2) {
        final Set<String> set1 = getSet(seq1);
        final Set<String> set2 = getSet(seq2);
        final Set<String> set3 = new HashSet<String>(set1);
        set3.retainAll(set2);
        runnable.increaseProgress(RunnableState.RUNNING);
        return (2.0 * set3.size()) / (set1.size() + set2.size());
    }

    /**
     * Gets the set of n-grams for a given list of Byte elements.
     *
     * @param seq the sequence of Bytes
     * @return the set of n-grams encoded as a string
     */
    private Set<String> getSet(final List<Byte> seq) {
        final StringBuffer fragment = new StringBuffer();
        final Set<String> set = new HashSet<String>();
        for (int i = 0; i < seq.size() + FRAGMENT_LENGTH - 1; i++) {
            fragment.delete(0, fragment.length());
            for (int j = i - FRAGMENT_LENGTH + 1; j <= i; j++) {
                if (j < 0 || j >= seq.size()) {
                    fragment.append(" -");
                } else if (seq.get(j) == null) {
                    fragment.append(" n");
                } else {
                    fragment.append(' ').append(seq.get(j).toString());
                }
            }
            fragment.deleteCharAt(0);
            set.add(fragment.toString());
        }
        return set;
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