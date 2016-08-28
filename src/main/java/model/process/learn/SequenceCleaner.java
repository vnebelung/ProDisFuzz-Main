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
 * This class is the sequence cleaner callable, responsible for adjusting a sequence by cleaning it up from little
 * inconsistencies.
 */
class SequenceCleaner implements Callable<List<Byte>> {

    private static final int CLEAN_LENGTH = 3;
    private static final int CLEAN_THRESHOLD = 0;
    private List<Byte> sequence;

    /**
     * Constructs the callable.
     *
     * @param sequence the input sequence
     */
    public SequenceCleaner(List<Byte> sequence) {
        this.sequence = new ArrayList<>(sequence);
    }

    @SuppressWarnings("OverlyComplexMethod")
    @Override
    public List<Byte> call() {
        List<Byte> result = new ArrayList<>(sequence);
        if (result.size() <= ((CLEAN_LENGTH + 1) * 2)) {
            return result;
        }
        boolean changed;
        int[] scores = new int[result.size()];
        do {
            changed = false;
            // Initialize the scoring array
            for (int i = 0; i < scores.length; i++) {
                scores[i] = 0;
            }
            // Compute all scoring values for every scoring element
            for (int i = 1; i < (scores.length - 1); i++) {
                if (result.get(i) == null) {
                    scores[i]++;
                } else {
                    scores[i]--;
                    if (result.get(i - 1) != null) {
                        scores[i]--;
                    }
                    if (result.get(i + 1) != null) {
                        scores[i]--;
                    }
                }
            }
            // Clean up the array by changing all fixed elements to variable elements whose scoring value is equal or
            // greater than the threshold value
            for (int i = CLEAN_LENGTH + 1; i < (result.size() - (CLEAN_LENGTH + 1)); i++) {
                if (result.get(i) != null) {
                    int score = 0;
                    for (int j = i - CLEAN_LENGTH; j <= (i + CLEAN_LENGTH); j++) {
                        if (j != i) {
                            score += scores[j];
                        }
                    }
                    if (score > CLEAN_THRESHOLD) {
                        result.set(i, null);
                        changed = true;
                    }
                }
            }
        } while (changed);
        return result;
    }
}
