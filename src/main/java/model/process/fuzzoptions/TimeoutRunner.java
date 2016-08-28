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
 * This class is the timeout runnable, responsible for setting the connection timeout, that is the time in which a
 * target application must react on input data before a crash occurrence is assumed.
 */
class TimeoutRunner extends AbstractRunner {

    public static final int TIMEOUT_MIN = 50;
    public static final int TIMEOUT_MAX = 10000;
    private int timeout;

    /**
     * Constructs a new injection runner.
     *
     * @param timeout the connection timeout in ms
     */
    protected TimeoutRunner(int timeout) {
        super(1);
        this.timeout = timeout;
    }

    @Override
    public void run() {
        markStart();

        // Start work unit
        timeout = Math.max(timeout, TIMEOUT_MIN);
        timeout = Math.min(timeout, TIMEOUT_MAX);
        Model.INSTANCE.getLogger().info("Timeout set to " + timeout + " ms");
        markFinish();
    }

    /**
     * Returns the timeout.
     *
     * @return the timout in ms
     */
    public int getTimeout() {
        return timeout;
    }

}
