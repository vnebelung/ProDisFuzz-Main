/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package support;

import model.process.AbstractRunner.ExternalState;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

public class RunnerMonitor implements Observer {

    private Map<ExternalState, Boolean> map = new EnumMap<>(ExternalState.class);
    private ExternalState currentState;

    public RunnerMonitor() {
        map.put(ExternalState.IDLE, true);
        map.put(ExternalState.RUNNING, false);
        map.put(ExternalState.FINISHED, false);
    }

    @Override
    public void update(Observable o, Object arg) {
        map.put((ExternalState) arg, true);
        currentState = (ExternalState) arg;
    }

    public boolean areAllStatesVisited() {
        boolean result = true;
        for (Entry<ExternalState, Boolean> each : map.entrySet()) {
            result &= each.getValue();
        }
        return result;
    }

    public ExternalState getCurrentState() {
        return currentState;
    }
}
