/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:47.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view;

import model.Model;
import view.component.Frame;

import javax.swing.*;
import java.util.Locale;

public class View {

    private final Frame frame;

    /**
     * Instantiates a new view responsible for managing all components used for displaying components.
     */
    public View() {
        JComponent.setDefaultLocale(Locale.ENGLISH);
        try {
            for (final UIManager.LookAndFeelInfo each : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(each.getName())) {
                    UIManager.setLookAndFeel(each.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException |
                IllegalAccessException e) {
            Model.INSTANCE.getLogger().error(e);
        }
        frame = new Frame("ProDisFuzz");
    }

    /**
     * Makes the basic frame visible.
     */
    public void show() {
        frame.showModePage();
        frame.setVisible(true);
    }
}
