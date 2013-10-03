/*
 * This file is part of ProDisFuzz, modified on 03.10.13 19:37.
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
     * Instantiates a basic version of a page.
     *
     * @param frame the parent frame
     */
    AbstractPage(final Frame frame) {
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
                frame.showModePage();
            }
        });
        navCancel.setEnabled(true);
        navPanel.add(navCancel);
        navPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        navNext = new JButton(nextAction(frame));
        navNext.setEnabled(false);
        navPanel.add(navNext);
        navPanel.add(Box.createRigidArea(new Dimension(5, 0)));

        navFinish = new JButton(new AbstractAction("Finish") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.INSTANCE.reset();
                frame.showModePage();
            }
        });
        navFinish.setEnabled(false);
        navPanel.add(navFinish);
    }

    /**
     * Enables the next button. Disabled by default.
     */
    void enableNext() {
        navNext.setEnabled(true);
    }

    /**
     * Enables the cancel button. Enabled by default.
     */
    void enableCancel() {
        navCancel.setEnabled(true);
    }

    /**
     * Enables the finish button. Disabled by default.
     */
    void enableFinish() {
        navFinish.setEnabled(true);
    }

    /**
     * Disables the next button. Disabled by default.
     */
    void disableNext() {
        navNext.setEnabled(false);
    }

    /**
     * Disables the cancel button. Enabled by default.
     */
    void disableCancel() {
        navCancel.setEnabled(false);
    }

    /**
     * Disables the finish button. Disabled by default.
     */
    void disableFinish() {
        navFinish.setEnabled(false);
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
     * @param frame the parent frame
     * @return the action listener
     */
    Action nextAction(final Frame frame) {
        return new AbstractAction("Next >") {
            @Override
            public void actionPerformed(final ActionEvent e) {
            }
        };
    }
}
