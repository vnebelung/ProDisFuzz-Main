/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import java.util.*;
import java.util.Map.Entry;

/**
 * This class represents a sequence within the learning algorithm.
 */
class LearnSequence {

    private List<Byte> sequence;
    private Set<String> nGrams;
    private Map<LearnSequence, Double> distances;

    /**
     * Constructs the learn sequence.
     *
     * @param sequence the byte sequence
     * @param nGrams   the set of n-grams
     */
    public LearnSequence(List<Byte> sequence, Set<String> nGrams) {
        this.sequence = new ArrayList<>(sequence);
        this.nGrams = new TreeSet<>(nGrams);
        distances = new HashMap<>();
    }

    /**
     * Adds a distance to the given neighbor sequence, based on the given dice value for the current sequence and the
     * neighbor. If the neighbor is already listed with a distance, the existing value will be overwritten with the
     * given one. The neighbor's distance to this sequence is updated as well.
     *
     * @param neighbor  the neighbor value
     * @param diceValue the dice value for the sequence and its neighbor
     */
    public void addDistanceTo(LearnSequence neighbor, double diceValue) {
        distances.put(neighbor, 1 - diceValue);
        //noinspection FloatingPointEquality
        if (neighbor.getDistanceTo(this) != getDistanceTo(neighbor)) {
            neighbor.addDistanceTo(this, diceValue);
        }
    }

    /**
     * Returns the distance for the sequence and a given neighbor. If a distance for the given neighbor has not been
     * calculated (yet), the return value will be -1. The distance is 1 - dice value.
     *
     * @param neighbor the neighbor
     * @return the distance or -1, if the value is not existing
     */
    public double getDistanceTo(LearnSequence neighbor) {
        Double result = distances.get(neighbor);
        if (result == null) {
            return -1;
        }
        return result;
    }

    /**
     * Removes a given neighbor and its distance.
     *
     * @param neighbor the neighbor sequence
     */
    public void removeDistanceTo(LearnSequence neighbor) {
        distances.remove(neighbor);
    }

    /**
     * Returns the underlying byte sequence of the learn sequence.
     *
     * @return the byte sequence
     */
    public List<Byte> getSequence() {
        return Collections.unmodifiableList(sequence);
    }

    /**
     * Returns the n-grams of the learn sequence.
     *
     * @return the n-grams
     */
    public Set<String> getNGrams() {
        return Collections.unmodifiableSet(nGrams);
    }

    /**
     * Returns the average distance of the sequence to all of its neighbors.
     *
     * @return the average distance
     */
    public double getAverageDistance() {
        double result = 0;
        for (Entry<LearnSequence, Double> each : distances.entrySet()) {
            result += each.getValue();
        }
        result /= (distances.size() + 1) - 2; // + 1 because distances does not contain a reference to this
        return result;
    }

    /**
     * Returns the distance for the combined sequence of the current sequence and the given neighbor.
     *
     * @param neighbor the neighbor for that the combined distance will be returned
     * @return the combined distance
     */
    public double getCombinedDistanceTo(LearnSequence neighbor) {
        if (distances.get(neighbor) == null) {
            return Double.MAX_VALUE;
        }
        return distances.get(neighbor) - (getAverageDistance() + neighbor.getAverageDistance());
    }
}
