/*
 * This file is part of ProDisFuzz, modified on 04.10.13 00:26.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package main;

import model.Model;
import view.View;

public final class ProDisFuzz {

    private final Model model;
    private final View view;

    /**
     * Instantiates the application ProDisFuzz. An instance includes a model and a view.
     */
    private ProDisFuzz() {
        model = Model.INSTANCE;
        view = new View();
    }

    /**
     * The main method of ProDisFuzz is the starting point of the application. The given arguments are ignored as all
     * options will be set in the GUI.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        final ProDisFuzz app = new ProDisFuzz();
        app.init();
    }

    /**
     * Prepares ProDisFuzz for the initial start. That includes resetting the model to its default values and making
     * the view visible.
     */
    private void init() {
        // Send an initial reset to update the view
        model.reset();
        // Make the window
        view.show();
    }
}
