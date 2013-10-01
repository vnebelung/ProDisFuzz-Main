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
import model.logger.Logger;
import model.process.FuzzingProcess;
import view.ImageRepository;
import view.component.Frame;

import javax.swing.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

public class FuzzingPage extends AbstractPage implements Observer {
    private final JProgressBar progressBar;
    private final JButton startStopButton;
    private final JLabel statusIcon;
    private final JLabel statusLabel;
    private final JLabel timeCountLabel;
    private final JLabel progressShowLabel;
    private Timer timer;

    /**
     * Instantiates a basic version of a page.
     *
     * @param frame the parent frame
     */
    public FuzzingPage(final Frame frame) {
        super(frame);
        Model.getInstance().getFuzzingProcess().addObserver(this);
        final double[][] areaLayout = {{TableLayout.MINIMUM, TableLayout.MINIMUM, TableLayout.FILL},
                {TableLayout.MINIMUM, TableLayout.MINIMUM, TableLayout.MINIMUM, TableLayout.MINIMUM}};
        area.setLayout(new TableLayout(areaLayout));

        startStopButton = new JButton();
        area.add(startStopButton, "0, 0, c, c");

        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        area.add(progressBar, "1, 0, 2, 0, f, c");

        final JLabel timeLabel = new JLabel("Time elapsed:");
        area.add(timeLabel, "1, 1, l, c");

        timeCountLabel = new JLabel();
        area.add(timeCountLabel, "2, 1, l, c");

        final JLabel progressLabel = new JLabel();
        area.add(progressLabel, "1, 2, l, c");

        progressShowLabel = new JLabel();
        area.add(progressShowLabel, "2, 2, l, c");

        statusIcon = new JLabel();
        area.add(statusIcon, "1, 3, c, c");

        statusLabel = new JLabel();
        area.add(statusLabel, "2, 3, l, c");
    }

    @Override
    public void update(final Observable o, final Object arg) {
        final FuzzingProcess data = (FuzzingProcess) o;

        startStopButton.setAction(data.isRunning() ? stopAction() : startAction());

        progressBar.setMaximum(data.getWorkTotal());
        progressBar.setValue(data.getWorkProgress());
        progressBar.setIndeterminate(data.getWorkTotal() < 0 && data.isRunning());

        // If the start time is -1 the timer will be reset and the default time will be displayed
        if (data.getStartTime() == -1) {
            stopTimer();
            timeCountLabel.setText("00:00:00");
        } else if (data.isRunning()) {
            startTimer(data.getStartTime());
        } else {
            stopTimer();
        }

        progressShowLabel.setText(data.getWorkProgress() + "/" + (data.getWorkTotal() == -1 ? "inf" : data
                .getWorkTotal()));

        statusLabel.setText(data.isRunning() ? "Fuzzing process is running ..." : "Ready to start");
        statusIcon.setIcon(data.isRunning() ? ImageRepository.getInstance().getWorkingIcon() : ImageRepository
                .getInstance().getOkIcon());

        if (data.isRunning()) {
            disableCancel();
        } else {
            enableCancel();
        }

        if (data.getNumOfRecords() > 0 && !data.isRunning()) {
            enableNext();
        } else {
            disableNext();
        }
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    /**
     * Starts the fuzzing process.
     *
     * @return the start action
     */
    private Action startAction() {
        return new AbstractAction("Start") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.getInstance().getFuzzingProcess().start();
            }
        };
    }

    /**
     * Stops the fuzzing process.
     *
     * @return the stop action
     */
    private Action stopAction() {
        return new AbstractAction("Stop") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.getInstance().getFuzzingProcess().interrupt();
            }
        };
    }

    private void startTimer(final long startTime) {
        if (timer == null) {
            timer = new javax.swing.Timer(1000, new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    // Fill the time values up to 2 chars with preceding zeros
                    final DecimalFormat timeFormat = new DecimalFormat("00");
                    // The duration of the current fuzzing process is the current time minus the given start time
                    try {
                        final Duration duration = DatatypeFactory.newInstance().newDuration(System.currentTimeMillis
                                () - startTime);
                        timeCountLabel.setText(timeFormat.format(duration.getHours()) + ":" + timeFormat.format
                                (duration.getMinutes()) + ":" + timeFormat.format(duration.getSeconds()));
                    } catch (DatatypeConfigurationException e1) {
                        Logger.getInstance().error(e1);
                    }

                }
            });
            timer.setInitialDelay(0);
            timer.setRepeats(true);
        }
        if (!timer.isRunning()) {
            timer.start();
        }
    }

    @Override
    protected AbstractAction nextAction(final Frame frame) {
        return new AbstractAction("Next >") {
            @Override
            public void actionPerformed(final ActionEvent e) {
                Model.getInstance().getReportProcess().init();
                frame.showReportPage();
            }
        };
    }
}
