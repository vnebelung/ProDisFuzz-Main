/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:13.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page;

import model.Model;
import view.component.Frame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class AbstractPage extends JPanel {

    final JPanel area;
    private final JButton navNext;
    private final JButton navFinish;
    private final JButton navCancel;

    /**
     * Instantiates a basic abstract page.
     *
     * @param f the parent frame
     */
    public AbstractPage(final Frame f) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        area = new JPanel();
        add(area);
        add(Box.createRigidArea(new Dimension(0, 5)));

        final JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.LINE_AXIS));
        add(navPanel);

        navPanel.add(Box.createHorizontalGlue());

        navCancel = new JButton(new AbstractAction("Cancel") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.INSTANCE.reset();
                f.showModePage();
            }
        });
        navCancel.setEnabled(true);
        navPanel.add(navCancel);
        navPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        navNext = new JButton(nextAction(f));
        navNext.setEnabled(false);
        navPanel.add(navNext);
        navPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        navFinish = new JButton(new AbstractAction("Finish") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.INSTANCE.reset();
                f.showModePage();
            }
        });
        navFinish.setEnabled(false);
        navPanel.add(navFinish);
    }

    /**
     * Enables/disables the next button. Disabled by default.
     *
     * @param b true if the next button should be enabled
     */
    protected void setNextEnabled(final boolean b) {
        navNext.setEnabled(b);
    }

    /**
     * Enables/disables the cancel button. Enabled by default.
     *
     * @param b true if the next button should be enabled
     */
    void setCancelEnabled(final boolean b) {
        navCancel.setEnabled(b);
    }

    /**
     * Enables/disables the finish button. Disabled by default.
     *
     * @param b true if the next button should be enabled
     */
    void setFinishEnabled(final boolean b) {
        navFinish.setEnabled(b);
    }

    /**
     * Hides the navigation buttons. All Buttons are visible by default.
     */
    void hideButtons() {
        navCancel.setVisible(false);
        navNext.setVisible(false);
        navFinish.setVisible(false);
    }

    /**
     * This empty implementation is provided so users don't have to implement this method if the next button has no
     * function.
     *
     * @param f the parent frame
     * @return the action listener
     */
    Action nextAction(final Frame f) {
        return new AbstractAction("Next >") {
            @Override
            public void actionPerformed(final ActionEvent e) {
            }
        };
    }
}
