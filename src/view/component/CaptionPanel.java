/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:25.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.component;

import info.clearthought.layout.TableLayout;

import javax.swing.*;

public class CaptionPanel extends JPanel {

    /**
     * Instantiates a new caption panel for the protocol structure.
     */
    public CaptionPanel() {
        super();

        final double[][] captionLayout = {{20, 100, 20, 100}, {40}};
        setLayout(new TableLayout(captionLayout));

        final ColorBox varColorBox = new ColorBox(ProtocolPane.COLOR_VAR);
        add(varColorBox, "0, 0, c, c");

        final JLabel varColorLabel = new JLabel("Variable Data");
        add(varColorLabel, "1, 0, l, c");

        final ColorBox fixColorBox = new ColorBox(ProtocolPane.COLOR_FIX);
        add(fixColorBox, "2, 0, c, c");

        final JLabel fixColorLabel = new JLabel("Fixed Data");
        add(fixColorLabel, "3, 0, l, c");
    }
}
