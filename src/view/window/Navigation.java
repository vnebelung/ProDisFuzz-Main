/*
 * This file is part of ProDisFuzz, modified on 28.12.13 11:29.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.window;

import javafx.scene.Node;

public interface Navigation {

    /**
     * Sets whether the control panel should enable the option to cancel the current page.
     *
     * @param b true, if the cancel option should be enabled
     * @param n the Node that sends the signal
     */
    public void setFinishable(boolean b, Node n);

    /**
     * Sets whether the control panel should enable the option to finish the current page.
     *
     * @param b true, if the finish option should be enabled
     * @param n the Node that sends the signal
     */
    public void setCancelable(boolean b, Node n);
}
