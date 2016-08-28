/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view;

import view.application.WindowApplication;

import java.util.Locale;

/**
 * This class is the view of the MVC design of ProDisFuzz, responsible for managing all components used for displaying
 * components.
 */
public class View {

    private WindowApplication windowApplication;

    /**
     * Constructs a new view.
     */
    public View() {
        Locale.setDefault(Locale.ENGLISH);
        windowApplication = new WindowApplication();
    }

    /**
     * Makes the basic window visible.
     */
    public void show() {
        windowApplication.show();
    }
}
