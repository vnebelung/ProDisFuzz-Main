/*
 * This file is part of ProDisFuzz, modified on 03.10.13 22:25.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.component;

import info.clearthought.layout.TableLayout;
import model.InjectedProtocolPart;
import model.Model;
import view.ImageRepository;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Path;

public class InjectionPanel extends JPanel {

    private final JRadioButton randomButton;
    private final JRadioButton libraryButton;
    private final JTextField libraryText;
    private final JButton browseButton;
    private final JLabel libraryIcon;
    private final JLabel libraryLabel;
    private final int processHash;

    public InjectionPanel(final Frame frame, final int hash) {
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
                final int choose = fileChooser.showOpenDialog(frame);
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
                Model.INSTANCE.getFuzzOptionsProcess().setLibraryFile(libraryText.getText(), processHash);
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                Model.INSTANCE.getFuzzOptionsProcess().setLibraryFile(libraryText.getText(), processHash);
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                Model.INSTANCE.getFuzzOptionsProcess().setLibraryFile(libraryText.getText(), processHash);
            }
        };
    }

    public void update(final InjectedProtocolPart.DataInjectionMethod injectedData, final boolean enabled,
                       final Path path) {
        randomButton.setSelected(injectedData == InjectedProtocolPart.DataInjectionMethod.RANDOM);
        randomButton.setEnabled(enabled);

        libraryButton.setSelected(injectedData == InjectedProtocolPart.DataInjectionMethod.LIBRARY);
        libraryButton.setEnabled(enabled);
        libraryText.setEnabled(enabled && injectedData == InjectedProtocolPart.DataInjectionMethod.LIBRARY);
        browseButton.setEnabled(enabled && injectedData == InjectedProtocolPart.DataInjectionMethod.LIBRARY);
        libraryIcon.setIcon(path == null ? ImageRepository.INSTANCE.getErrorIcon() : ImageRepository.INSTANCE
                .getOkIcon());
        libraryLabel.setText(path == null ? "Please choose a valid library file" : "Valid library file chosen");

        libraryIcon.setVisible(injectedData == InjectedProtocolPart.DataInjectionMethod.LIBRARY && enabled);
        libraryLabel.setVisible(injectedData == InjectedProtocolPart.DataInjectionMethod.LIBRARY && enabled);
    }
}
