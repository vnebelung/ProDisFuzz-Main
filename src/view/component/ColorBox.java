/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:25.
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
     * Instantiates a new color box.
     *
     * @param color the fill color of the box
     */
    public ColorBox(final Color color) {
        super();
        this.color = color;
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