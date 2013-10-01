/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:25.
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

public class LearnAdjustCallable implements Callable<List<Byte>> {

    private final static int CLEAN_LENGTH = 3;
    private final static int CLEAN_THRESHOLD = 0;
    private final List<Byte> cleanedSequence;

    /**
     * Instantiates a new callable.
     *
     * @param sequence the input sequence
     */
    public LearnAdjustCallable(final List<Byte> sequence) {
        cleanedSequence = new ArrayList<>(sequence);
    }

    @Override
    public List<Byte> call() throws Exception {
        if (cleanedSequence.size() <= (CLEAN_LENGTH + 1) * 2) {
            Collections.unmodifiableList(cleanedSequence);
        }
        boolean changed;
        int[] scores = new int[cleanedSequence.size()];
        int score;
        do {
            changed = false;
            // Initialize the scoring array
            for (int i = 0; i < scores.length; i++) {
                scores[i] = 0;
            }
            // Compute all scoring values for every scoring element
            for (int i = 1; i < scores.length - 1; i++) {
                if (cleanedSequence.get(i) == null) {
                    scores[i]++;
                } else {
                    scores[i]--;
                    if (cleanedSequence.get(i - 1) != null) {
                        scores[i]--;
                    }
                    if (cleanedSequence.get(i + 1) != null) {
                        scores[i]--;
                    }
                }
            }
            // Clean up the array by changing all fixed elements to variable elements whose scoring value is
            // equal or greater than the threshold value
            for (int i = CLEAN_LENGTH + 1; i < cleanedSequence.size() - (CLEAN_LENGTH + 1); i++) {
                if (cleanedSequence.get(i) != null) {
                    score = 0;
                    for (int j = i - CLEAN_LENGTH; j <= i + CLEAN_LENGTH; j++) {
                        if (j != i) {
                            score += scores[j];
                        }
                    }
                    if (score > CLEAN_THRESHOLD) {
                        cleanedSequence.set(i, null);
                        changed = true;
                    }
                }
            }
        } while (changed);
        return Collections.unmodifiableList(cleanedSequence);
    }
}
