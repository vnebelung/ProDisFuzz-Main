/*
 * This file is part of ProDisFuzz, modified on 07.12.13 21:44.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view;

import view.window.WindowApplication;

import java.util.Locale;

public class View {

    private final WindowApplication window;

    /**
     * Instantiates a new view responsible for managing all components used for displaying components.
     */
    public View() {
        Locale.setDefault(Locale.ENGLISH);
        window = new WindowApplication();
    }

    /**
     * Makes the basic window visible.
     */
    public void show() {
        window.show();
    }
}
