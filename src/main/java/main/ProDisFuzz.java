/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package main;

import model.Model;
import view.View;

/**
 * This is the main class of ProDisFuzz and responsible for starting the model and the view.
 */
public class ProDisFuzz {

    private final Model model;
    private final View view;

    /**
     * Constructs the application ProDisFuzz. An instance includes a model and a view.
     */
    private ProDisFuzz() {
        model = Model.INSTANCE;
        view = new View();
    }

    /**
     * The main method of ProDisFuzz is the starting point of the application. The given arguments are ignored as all
     * options will be set via the GUI.
     *
     * @param args the ignored arguments
     */
    public static void main(String... args) {
        ProDisFuzz app = new ProDisFuzz();
        app.init();
    }

    /**
     * Prepares ProDisFuzz for the initial readDirectory. This includes resetting the model to its default values and
     * making the
     * view visible.
     */
    private void init() {
        // Send an initial reset to update the view
        model.reset();
        // Make the window
        view.show();
    }
}
