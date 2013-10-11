/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:35.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.component;

import info.clearthought.layout.TableLayout;
import model.InjectedProtocolPart;
import model.Model;
import view.icons.ImageRepository;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class InjectionPanel extends JPanel {

    private final JRadioButton randomButton;
    private final JRadioButton libraryButton;
    private final JTextField libraryText;
    private final JButton browseButton;
    private final JLabel libraryIcon;
    private final JLabel libraryLabel;
    private final int processHash;

    /**
     * Instantiates a new injection panel that displays the options for selecting the injection data.
     *
     * @param f    the parent frame
     * @param hash the hash code of the corresponding protocol part
     */
    public InjectionPanel(final Frame f, final int hash) {
        super();
        processHash = hash;

        final double[][] panelLayout = {{TableLayout.MINIMUM, TableLayout.MINIMUM, TableLayout.MINIMUM,
                TableLayout.FILL, TableLayout.MINIMUM}, {TableLayout.MINIMUM, TableLayout.MINIMUM}};
        setLayout(new TableLayout(panelLayout));

        randomButton = new JRadioButton(new AbstractAction("Random") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.INSTANCE.getFuzzOptionsProcess().setRandomInjectionForPart(processHash);
            }
        });
        add(randomButton, "0, 0, c, c");

        libraryButton = new JRadioButton(new AbstractAction("File:") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.INSTANCE.getFuzzOptionsProcess().setLibraryInjectionForPart(processHash);
            }
        });
        add(libraryButton, "1, 0, c, c");

        final ButtonGroup injectionGroup = new ButtonGroup();
        injectionGroup.add(randomButton);
        injectionGroup.add(libraryButton);

        libraryText = new JTextField();
        add(libraryText, "2, 0, 3, 0, f, c");
        libraryText.getDocument().addDocumentListener(pathListener());

        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        browseButton = new JButton(new AbstractAction("Browse...") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int choose = fileChooser.showOpenDialog(f);
                if (choose == JFileChooser.APPROVE_OPTION) {
                    final File directory = fileChooser.getSelectedFile();
                    libraryText.setText(directory.getAbsolutePath());
                }
            }
        });
        add(browseButton, "4, 0, c, c");

        libraryIcon = new JLabel();
        add(libraryIcon, "2, 1, c, c");

        libraryLabel = new JLabel();
        add(libraryLabel, "3, 1, 4, 1, l, c");
    }

    /**
     * Creates the listener for the library text field and forwards every change to the corresponding process of the
     * model.
     *
     * @return the listener
     */
    private DocumentListener pathListener() {
        return new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                Model.INSTANCE.getFuzzOptionsProcess().setLibraryFile(Paths.get(libraryText.getText()), processHash);
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                Model.INSTANCE.getFuzzOptionsProcess().setLibraryFile(Paths.get(libraryText.getText()), processHash);
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                Model.INSTANCE.getFuzzOptionsProcess().setLibraryFile(Paths.get(libraryText.getText()), processHash);
            }
        };
    }

    /**
     * Updates all components of this panel.
     *
     * @param dataInjectionMethod the data injection method
     * @param enabled             false if the panel should be greyed out
     * @param p                   the library path
     */
    public void update(final InjectedProtocolPart.DataInjectionMethod dataInjectionMethod, final boolean enabled,
                       final Path p) {
        randomButton.setSelected(dataInjectionMethod == InjectedProtocolPart.DataInjectionMethod.RANDOM);
        randomButton.setEnabled(enabled);

        libraryButton.setSelected(dataInjectionMethod == InjectedProtocolPart.DataInjectionMethod.LIBRARY);
        libraryButton.setEnabled(enabled);
        libraryText.setEnabled(enabled && dataInjectionMethod == InjectedProtocolPart.DataInjectionMethod.LIBRARY);
        browseButton.setEnabled(enabled && dataInjectionMethod == InjectedProtocolPart.DataInjectionMethod.LIBRARY);
        libraryIcon.setIcon(p == null ? ImageRepository.INSTANCE.getErrorIcon() : ImageRepository.INSTANCE.getOkIcon());
        libraryLabel.setText(p == null ? "Please choose a valid library file" : "Valid library file chosen");

        libraryIcon.setVisible(dataInjectionMethod == InjectedProtocolPart.DataInjectionMethod.LIBRARY && enabled);
        libraryLabel.setVisible(dataInjectionMethod == InjectedProtocolPart.DataInjectionMethod.LIBRARY && enabled);
    }
}
