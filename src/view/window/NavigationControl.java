/*
 * This file is part of ProDisFuzz, modified on 05.01.14 15:45.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.window;

public interface NavigationControl {

    /**
     * Enters the learn mode, in which all pages support the protocol learning.
     */
    public void enterLearnMode();

    /**
     * Enters the fuzz mode, in which all pages support the fuzzing of a learned protocol.
     */
    public void enterFuzzMode();

    /**
     * Makes the page after the current displayed page visible. The next page is determined by the
     * defined order of the pages.
     */
    public void nextPage();

    /**
     * Makes the page before the current displayed page visible. The previous page is determined by the
     * defined order of the pages.
     */
    public void previousPage();

    /**
     * Makes the default start page visible. The start page is determined by the defined order ot hte pages.
     */
    public void resetPage();
}
