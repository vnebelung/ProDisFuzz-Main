/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * This class is sequence selector callable, responsible for selecting two sequences with the lowest
 * distance between each other, that is selecting the nearest neighbors.
 */
class SequenceSelector implements Callable<Set<LearnSequence>> {

    private final Set<LearnSequence> sequences;

    /**
     * Constructs a new callable.
     *
     * @param sequences the sequences
     */
    public SequenceSelector(Set<LearnSequence> sequences) {
        this.sequences = sequences;
    }

    @Override
    public Set<LearnSequence> call() {
        Set<LearnSequence> result = new HashSet<>(2);
        double minDistance = Double.MAX_VALUE;
        for (LearnSequence eachFrom : sequences) {
            for (LearnSequence eachTo : sequences) {
                if (eachFrom.getCombinedDistanceTo(eachTo) < minDistance) {
                    minDistance = eachFrom.getCombinedDistanceTo(eachTo);
                    result.clear();
                    result.add(eachFrom);
                    result.add(eachTo);
                }
            }
        }
        return result;
    }
}
