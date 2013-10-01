/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:27.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.connector;

import model.modificator.FuzzedData;

public abstract class AbstractConnector {

    /**
     * Tests for a valid connection to the target without sending fuzzed data.
     *
     * @return true if a connection could successfully be established
     */
    protected abstract boolean connect();

    /**
     * Sends fuzzed data to the target.
     *
     * @param data the fuzzed data
     * @return true if the data could successfully be sent to the target, false if some connection error occured.
     */
    protected abstract boolean call(FuzzedData data);

    /**
     * Sets the fuzzing target.
     *
     * @param args the target with various parameters depending on the specific implementation of the connector.
     */
    protected abstract void setTarget(String... args);
}
