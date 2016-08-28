/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.window;

/**
 * This interface represents a navigation control component, responsible for navigating through pages.
 */
public interface NavigationControl {

    /**
     * Enters the learn mode, in which all pages support the protocol learning.
     */
    void enterLearnMode();

    /**
     * Enters the fuzz mode, in which all pages support the fuzzing of a learned protocol.
     */
    void enterFuzzMode();

    /**
     * Makes the page after the current displayed page visible. The next page is determined by the defined order of the
     * pages.
     */
    void nextPage();

    /**
     * Makes the page before the current displayed page visible. The previous page is determined by the defined order of
     * the pages.
     */
    void previousPage();

    /**
     * Makes the default readDirectory page visible. The readDirectory page is determined by the defined order ot hte
     * pages.
     */
    void resetPage();
}
