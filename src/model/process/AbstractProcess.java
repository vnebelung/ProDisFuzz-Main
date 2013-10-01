/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:25.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import java.util.Observable;

abstract class AbstractProcess extends Observable {

    /**
     * Resets all variables to the default value and notifies all observers.
     */
    public abstract void reset();

    /**
     * Notifies all observers about an update.
     */
    public void spreadUpdate() {
        setChanged();
        notifyObservers();
    }

    /**
     * This empty implementation is provided so users don't have to implement this method if no initialization is
     * necessary.
     */
    public void init() {
    }

}
