/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:25.
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
     * Instantiates a new instance of ProDisFuzz.
     */
    private ProDisFuzz() {
        model = Model.getInstance();
        view = new View();
    }

    /**
     * Main method of ProDisFuzz.
     *
     * @param args the arguments
     */
    public static void main(final String[] args) {
        final ProDisFuzz pdf = new ProDisFuzz();
        pdf.init();
    }

    /**
     * Prepares ProDisFuzz for the initial start.
     */
    private void init() {
        // Send an initial reset to update the view
        model.reset();
        // Make the window
        view.show();
    }
}
