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
import model.process.ExportProcess;
import view.component.Frame;
import view.icons.ImageRepository;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class ExportPage extends AbstractPage implements Observer {

    private final JLabel statusIcon;
    private final JLabel statusLabel;

    /**
     * Instantiates a basic abstract page.
     *
     * @param f the parent frame
     */
    public ExportPage(final Frame f) {
        super(f);
        setNextEnabled(false);
        setFinishEnabled(true);
        Model.INSTANCE.getExportProcess().addObserver(this);
        final double[][] areaLayout = {{0.1, 30, TableLayout.FILL}, {TableLayout.FILL}};
        area.setLayout(new TableLayout(areaLayout));

        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("XML files", "xml"));
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.setSelectedFile(new File("prodisfuzz.xml"));

        final JButton exportButton = new JButton(new AbstractAction("Export") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int choose = fileChooser.showSaveDialog(f);
                if (choose == JFileChooser.APPROVE_OPTION) {
                    Model.INSTANCE.getExportProcess().export(fileChooser.getSelectedFile().toPath());
                }
            }
        });
        area.add(exportButton, "0, 0, c, c");

        statusIcon = new JLabel();
        area.add(statusIcon, "1, 0, c, c");

        statusLabel = new JLabel();
        area.add(statusLabel, "2, 0, l, c");
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final ExportProcess data = (ExportProcess) o;

        if (data.isExported()) {
            statusIcon.setIcon(ImageRepository.INSTANCE.getOkIcon());
            statusLabel.setText("Protocol structure saved");
        } else {
            statusIcon.setIcon(ImageRepository.INSTANCE.getErrorIcon());
            statusLabel.setText("Protocol structure not saved");
        }

        setFinishEnabled(data.isExported());
    }
}
