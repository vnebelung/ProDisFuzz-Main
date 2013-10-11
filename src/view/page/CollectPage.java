/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:13.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page;

import info.clearthought.layout.TableLayout;
import model.Model;
import model.process.CollectProcess;
import view.component.Frame;
import view.table.CheckTableBooleanRenderer;
import view.table.CheckTableDecimalRenderer;
import view.table.CheckTableModel;
import view.table.TableColumnsSizer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class CollectPage extends AbstractPage implements Observer {

    private final JTextField pathText;
    private final JTable filesTable;

    /**
     * Instantiates the collect page responsible for visualizing the process of collecting sequence files.
     *
     * @param f the parent frame
     */
    public CollectPage(final Frame f) {
        super(f);
        Model.INSTANCE.getCollectProcess().addObserver(this);
        final double[][] areaLayout = {{0.2, 10, TableLayout.FILL, 10, 0.2}, {0.2, TableLayout.FILL}};
        area.setLayout(new TableLayout(areaLayout));

        final JLabel directoryLabel = new JLabel("Directory:");
        area.add(directoryLabel, "0, 0, r, c");

        pathText = new JTextField();
        area.add(pathText, "2, 0, f, c");
        pathText.getDocument().addDocumentListener(pathListener(Model.INSTANCE.getCollectProcess()));

        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        final JButton browseButton = new JButton(new AbstractAction("Browse...") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int choose = fileChooser.showOpenDialog(f);
                if (choose == JFileChooser.APPROVE_OPTION) {
                    final File directory = fileChooser.getSelectedFile();
                    pathText.setText(directory.getAbsolutePath());
                }
            }
        });
        area.add(browseButton, "4, 0, l, c");

        final JPanel tablePanel = new JPanel(new BorderLayout());
        area.add(tablePanel, "0, 1, 4, 1, f, c");
        filesTable = new JTable(new CheckTableModel(Model.INSTANCE.getCollectProcess()));
        filesTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        filesTable.getColumnModel().getColumn(0).setCellRenderer(new CheckTableBooleanRenderer());
        filesTable.getColumnModel().getColumn(3).setCellRenderer(new CheckTableDecimalRenderer());
        tablePanel.add(filesTable.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(filesTable, BorderLayout.CENTER);
    }

    /**
     * Creates the listener for the path text field and forwards every change to the corresponding process of the
     * model.
     *
     * @param p the corresponding process
     * @return the listener
     */
    private DocumentListener pathListener(final CollectProcess p) {
        return new DocumentListener() {
            @Override
            public void insertUpdate(final DocumentEvent e) {
                p.setDirectory(pathText.getText());
            }

            @Override
            public void removeUpdate(final DocumentEvent e) {
                p.setDirectory(pathText.getText());
            }

            @Override
            public void changedUpdate(final DocumentEvent e) {
                p.setDirectory(pathText.getText());
            }
        };
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final CollectProcess data = (CollectProcess) o;

        TableColumnsSizer.optimizeWidths(filesTable);

        setNextEnabled(data.getNumOfSelectedFiles() >= 2);

    }

    @Override
    protected AbstractAction nextAction(final Frame f) {
        return new AbstractAction("Next >") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.INSTANCE.getLearnProcess().init();
                f.showLearnPage();
            }
        };
    }
}
