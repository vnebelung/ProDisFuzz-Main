/*
 * This file is part of ProDisFuzz, modified on 11.10.13 21:51.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.component;

import javax.swing.*;
import java.awt.*;

class ColorBox extends JPanel {

    private final static Dimension SIZE = new Dimension(10, 10);
    private final Color color;

    /**
     * Instantiates a new color box that contains the colors for the protocol visualization.
     *
     * @param c the fill color of the box
     */
    public ColorBox(final Color c) {
        super();
        color = c;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillRect(0, 0, SIZE.width, SIZE.height);
        g.setColor(color.darker());
        g.drawRect(0, 0, SIZE.width - 1, SIZE.height - 1);
    }
}
