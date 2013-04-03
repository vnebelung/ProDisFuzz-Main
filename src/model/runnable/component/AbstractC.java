/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.runnable.AbstractR;

/**
 * The abstract Class AbstractC implements all basic functionality of a
 * component class.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public abstract class AbstractC {

    /**
     * The parent runnable.
     */
    protected AbstractR runnable;

    /**
     * Initializes a new abstract component
     *
     * @param runnable the parent runnable
     */
    protected AbstractC(final AbstractR runnable) {
        if (runnable != null) {
            this.runnable = runnable;
        }
    }

    /**
     * Gets the number of total working steps.
     *
     * @return the number of total working steps
     */
    public abstract int getTotalProgress();

}
