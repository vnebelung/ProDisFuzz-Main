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
 * This class is dice value calculator callable, responsible for computing the dice value for two sequences based on
 * their n-grams.
 */
class DiceValueCalculator implements Callable<Double> {

    private Set<String> nGrams1;
    private Set<String> nGrams2;

    /**
     * Constructs a new callable.
     *
     * @param sequence1 the first learn sequence
     * @param sequence2 the second learn sequence
     */
    public DiceValueCalculator(LearnSequence sequence1, LearnSequence sequence2) {
        nGrams1 = sequence1.getNGrams();
        nGrams2 = sequence2.getNGrams();
    }

    @Override
    public Double call() {
        //noinspection TypeMayBeWeakened
        Set<String> intersection = new HashSet<>(nGrams1);
        intersection.retainAll(nGrams2);
        return (2.0 * intersection.size()) / (nGrams1.size() + nGrams2.size());
    }
}
