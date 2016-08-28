/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.fuzzoptions;

import model.Model;
import model.process.AbstractRunner;

/**
 * This class is the timeout runnable, responsible for setting the fuzzing interval, that is the time between two
 * fuzzing iterations.
 */
class IntervalRunner extends AbstractRunner {

    public static final int INTERVAL_MIN = 100;
    public static final int INTERVAL_MAX = 30000;
    private int interval;

    /**
     * Constructs a new runner.
     *
     * @param interval the fuzzing interval in ms
     */
    public IntervalRunner(int interval) {
        super(1);
        this.interval = interval;
    }

    @Override
    public void run() {
        markStart();

        // Start work unit
        interval = Math.max(interval, INTERVAL_MIN);
        interval = Math.min(interval, INTERVAL_MAX);
        Model.INSTANCE.getLogger().info("Interval set to " + interval + " ms");
        markFinish();
    }

    /**
     * Returns the updated interval.
     *
     * @return the interval in ms
     */
    public int getInterval() {
        return interval;
    }

}
