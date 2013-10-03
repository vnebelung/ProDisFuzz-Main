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
import model.process.ReportProcess;
import view.ImageRepository;
import view.component.Frame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Observable;
import java.util.Observer;

public class ReportPage extends AbstractPage implements Observer {

    private final JLabel statusIcon;
    private final JLabel statusLabel;

    /**
     * Instantiates a basic version of a page.
     *
     * @param frame the parent frame
     */
    public ReportPage(final Frame frame) {
        super(frame);
        Model.INSTANCE.getReportProcess().addObserver(this);
        final double[][] areaLayout = {{0.1, 30, TableLayout.FILL}, {TableLayout.FILL}};
        area.setLayout(new TableLayout(areaLayout));

        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        final JButton exportButton = new JButton(new AbstractAction("Save Report") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                final int choose = fileChooser.showOpenDialog(frame);
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
            statusIcon.setIcon(ImageRepository.getInstance().getOkIcon());
            statusLabel.setText("Report generated");
        } else {
            statusIcon.setIcon(ImageRepository.getInstance().getErrorIcon());
            statusLabel.setText("Report not generated");
        }

        if (data.isWritten()) {
            disableCancel();
            enableFinish();
        } else {
            enableCancel();
            disableFinish();
        }
    }
}
