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
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

/**
 * This class is the n-gram calculator callable, responsible for creating n-grams of a byte sequence. It returns a set
 * of string-encoded n-grams.
 */
class NGramCreator implements Callable<Set<String>> {

    private int nGramSize = 3;
    private List<Byte> sequence;

    /**
     * Constructs a new callable.
     *
     * @param sequence  the byte sequence
     * @param nGramSize the size of the n-grams to be created
     */
    public NGramCreator(List<Byte> sequence, int nGramSize) {
        this.sequence = new ArrayList<>(sequence);
        this.nGramSize = nGramSize;
    }

    @Override
    public Set<String> call() {
        StringBuilder fragment = new StringBuilder();
        Set<String> result = new TreeSet<>();
        for (int i = 0; i < ((sequence.size() + nGramSize) - 1); i++) {
            fragment.delete(0, fragment.length());
            for (int j = (i - nGramSize) + 1; j <= i; j++) {
                if ((j < 0) || (j >= sequence.size())) {
                    fragment.append(" -");
                } else if (sequence.get(j) == null) {
                    //noinspection HardCodedStringLiteral
                    fragment.append(" n");
                } else {
                    fragment.append(' ').append(sequence.get(j));
                }
            }
            fragment.deleteCharAt(0);
            result.add(fragment.toString());
        }
        return result;
    }
}
