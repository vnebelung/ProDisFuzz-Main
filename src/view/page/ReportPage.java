/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:35.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page;

import info.clearthought.layout.TableLayout;
import model.Model;
import model.process.ReportProcess;
import view.component.Frame;
import view.icons.ImageRepository;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

public class ReportPage extends AbstractPage implements Observer {

    private final JLabel statusIcon;
    private final JLabel statusLabel;

    /**
     * Instantiates a new report page responsible for visualizing the report process.
     *
     * @param f the parent frame
     */
    public ReportPage(final Frame f) {
        super(f);
        Model.INSTANCE.getReportProcess().addObserver(this);
        final double[][] areaLayout = {{0.1, 30, TableLayout.FILL}, {TableLayout.FILL}};
        area.setLayout(new TableLayout(areaLayout));

        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        final JButton exportButton = new JButton(new AbstractAction("Save Report") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int choose = fileChooser.showOpenDialog(f);
                if (choose == JFileChooser.APPROVE_OPTION) {
                    Model.INSTANCE.getReportProcess().write(fileChooser.getSelectedFile().toPath());
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
        final ReportProcess data = (ReportProcess) o;

        if (data.isWritten()) {
            statusIcon.setIcon(ImageRepository.INSTANCE.getOkIcon());
            statusLabel.setText("Report generated");
        } else {
            statusIcon.setIcon(ImageRepository.INSTANCE.getErrorIcon());
            statusLabel.setText("Report not generated");
        }

        setCancelEnabled(!data.isWritten());
        setFinishEnabled(data.isWritten());
    }
}
