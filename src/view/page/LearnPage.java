/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:28.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page;

import info.clearthought.layout.TableLayout;
import model.Model;
import model.process.LearnProcess;
import view.ImageRepository;
import view.component.CaptionPanel;
import view.component.Frame;
import view.component.ProtocolPane;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

public class LearnPage extends AbstractPage implements Observer {

    private final JButton startStopButton;
    private final JProgressBar progressBar;
    private final JLabel statusIcon;
    private final JLabel statusLabel;
    private final JLabel memoryLabel;
    private final ProtocolPane protocolPane;

    /**
     * Instantiates the page.
     *
     * @param frame the parent frame
     */
    public LearnPage(final Frame frame) {
        super(frame);
        Model.getInstance().getLearnProcess().addObserver(this);
        final double[][] areaLayout = {{0.1, 30, TableLayout.FILL}, {0.1, 0.1, 0.1, 0.2, TableLayout.FILL}};
        area.setLayout(new TableLayout(areaLayout));

        startStopButton = new JButton();
        area.add(startStopButton, "0, 0, r, c");

        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        area.add(progressBar, "1, 0, 2, 0, f, c");

        statusIcon = new JLabel();
        area.add(statusIcon, "1, 1, c, c");

        statusLabel = new JLabel();
        area.add(statusLabel, "2, 1, l, c");

        memoryLabel = new JLabel();
        area.add(memoryLabel, "2, 2, l, c");

        final JPanel captionPanel = new CaptionPanel();
        area.add(captionPanel, "0, 3, 2, 3, c, b");

        protocolPane = new ProtocolPane(32);

        final JScrollPane scrollPane = new JScrollPane(protocolPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        area.add(scrollPane, "0, 4, 2, 4, f, f");
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final LearnProcess data = (LearnProcess) o;

        startStopButton.setAction(data.isRunning() ? stopAction() : startAction());

        progressBar.setMaximum(data.getWorkTotal());
        progressBar.setValue(data.getWorkProgress());

        if (data.isRunning()) {
            statusIcon.setIcon(ImageRepository.getInstance().getWorkingIcon());
            statusLabel.setText(data.getWorkProgress() + "/" + data.getWorkTotal());
        } else if (data.getWorkProgress() == data.getWorkTotal()) {
            statusIcon.setIcon(ImageRepository.getInstance().getOkIcon());
            statusLabel.setText("All protocol sequences have been processed");
        } else {
            statusIcon.setIcon(ImageRepository.getInstance().getOkIcon());
            statusLabel.setText("Ready for learning the protocol structure");
        }

        final DecimalFormat numberFormat = new DecimalFormat("#0.00");
        memoryLabel.setText(numberFormat.format(Model.getMemoryUsage() / 1024 / 1024) + " MB memory usage");

        synchronized (this) {
            protocolPane.addProtocolText(data.getProtocolParts());
        }

        if (data.isRunning()) {
            disableCancel();
        } else {
            enableCancel();
        }

        if (data.getWorkTotal() == data.getWorkProgress()) {
            enableNext();
        } else {
            disableNext();
        }
    }

    /**
     * Starts the learn process.
     *
     * @return the start action
     */
    private Action startAction() {
        return new AbstractAction("Start") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.getInstance().getLearnProcess().start();
            }
        };
    }

    /**
     * Stops the learn process.
     *
     * @return the complete action
     */
    private Action stopAction() {
        return new AbstractAction("Stop") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.getInstance().getLearnProcess().interrupt();
            }
        };
    }

    @Override
    protected AbstractAction nextAction(final Frame frame) {
        return new AbstractAction("Next >") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.getInstance().getExportProcess().init();
                frame.showExportPage();
            }
        };
    }
}
