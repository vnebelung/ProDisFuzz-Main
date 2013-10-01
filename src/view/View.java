/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:25.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view;

import model.logger.Logger;
import view.component.Frame;

import javax.swing.*;
import java.util.Locale;

public class View {

    private final Frame frame;

    /**
     * Instantiates a new view.
     */
    public View() {
        JComponent.setDefaultLocale(Locale.ENGLISH);
        try {
            for (final UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException |
                IllegalAccessException e) {
            Logger.getInstance().error(e);
        }
        frame = new Frame("ProDisFuzz");
    }

    /**
     * Makes the basic frame visible.
     */
    public void show() {
        frame.showModePage();
        frame.visible();
    }
}
