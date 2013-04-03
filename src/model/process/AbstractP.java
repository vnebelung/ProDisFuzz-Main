/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.process;

import java.util.Observable;

/**
 * The abstract Class AbstractP implements all basic functionality of a process
 * class.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public abstract class AbstractP extends Observable {

    /**
     * Resets all variables to the default value and notifies all observers.
     */
    protected abstract void reset();

    /**
     * Updates the model and notifies all observers.
     *
     * @param hasReseted true if process has been reseted, false otherwise.
     */
    public void spreadUpdate(final boolean hasReseted) {
        setChanged();
        notifyObservers(hasReseted);
    }

}