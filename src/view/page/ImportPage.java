/*
 * This file is part of ProDisFuzz, modified on 03.10.13 19:50.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page;

import info.clearthought.layout.TableLayout;
import model.Model;
import model.process.ImportProcess;
import view.component.CaptionPanel;
import view.component.Frame;
import view.component.ProtocolPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class ImportPage extends AbstractPage implements Observer {

    private final JTextField pathText;
    private final ProtocolPane protocolPane;

    /**
     * Instantiates the page.
     *
     * @param frame the parent frame
     */
    public ImportPage(final Frame frame) {
        super(frame);
        Model.INSTANCE.getImportProcess().addObserver(this);
        final double[][] areaLayout = {{0.2, 10, TableLayout.FILL, 10, 0.2}, {0.1, 0.2, TableLayout.FILL}};
        area.setLayout(new TableLayout(areaLayout));

        final JLabel directoryLabel = new JLabel("XML file:");
        area.add(directoryLabel, "0, 0, r, c");

        pathText = new JTextField();
        area.add(pathText, "2, 0, f, c");
        pathText.getDocument().addDocumentListener(pathListener());

        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
        fileChooser.setAcceptAllFileFilterUsed(false);

        final JButton browseButton = new JButton(new AbstractAction("Browse...") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int choose = fileChooser.showOpenDialog(frame);
                if (choose == JFileChooser.APPROVE_OPTION) {
                    final File directory = fileChooser.getSelectedFile();
                    pathText.setText(directory.getAbsolutePath());
                }
            }
        });
        area.add(browseButton, "4, 0, l, c");

        final JPanel captionPanel = new CaptionPanel();
        area.add(captionPanel, "0, 1, 4, 1, c, b");

        protocolPane = new ProtocolPane(32);

        final JScrollPane scrollPane = new JScrollPane(protocolPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        area.add(scrollPane, "0, 2, 4, 2, f, f");
    }

    /**
     * Creates the listener for the path text field and forwards every change to the corresponding process of the
     * model.
     *
     * @return the listener
     */
    private DocumentListener pathListener() {
        return new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                Model.INSTANCE.getImportProcess().importFile(pathText.getText());
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                Model.INSTANCE.getImportProcess().importFile(pathText.getText());
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                Model.INSTANCE.getImportProcess().importFile(pathText.getText());
            }
        };
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final ImportProcess data = (ImportProcess) o;

        synchronized (this) {
            protocolPane.addProtocolText(data.getProtocolParts());
        }

        if (data.isImported()) {
            enableNext();
        } else {
            disableNext();
        }
    }

    @Override
    protected Action nextAction(final Frame frame) {
        return new AbstractAction("Next >") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.INSTANCE.getFuzzOptionsProcess().init();
                frame.showFuzzOptionsPage();
            }
        };
    }
}
