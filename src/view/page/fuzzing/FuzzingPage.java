/*
 * This file is part of ProDisFuzz, modified on 08.02.14 23:36.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page.fuzzing;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import model.Model;
import model.process.fuzzing.FuzzingProcess;
import view.controls.progressbar.LabeledProgressBar;
import view.page.Page;
import view.window.ConnectionHelper;
import view.window.Navigation;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

public class FuzzingPage extends GridPane implements Observer, Page {

    private Navigation navigationPage;
    @FXML
    private Button startStopButton;
    @FXML
    private LabeledProgressBar labeledProgressBar;
    private Timer processTimer;
    @FXML
    private Label timeLabel;

    /**
     * Instantiates a new fuzzing area responsible for visualizing the process of fuzzing the target.
     */
    public FuzzingPage(Navigation n) {
        super();
        ConnectionHelper.connect(getClass().getResource("fuzzingPage.fxml"), this);
        Model.INSTANCE.getFuzzingProcess().addObserver(this);

        navigationPage = n;
    }

    @Override
    public void update(Observable o, Object arg) {
        final FuzzingProcess process = (FuzzingProcess) o;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                startStopButton.setText(process.isRunning() ? "Stop" : "Start");

                double progress = process.getWorkTotal() == 0 ? 0 : 1.0 * process.getWorkProgress() / process
                        .getWorkTotal();
                labeledProgressBar.update(progress, process.isRunning());

                if (process.isRunning()) {
                    startTimer(process.getStartTime());
                } else {
                    stopTimer();
                    if (process.getWorkProgress() == 0) {
                        timeLabel.setText("00:00:00");
                    }
                }

                navigationPage.setCancelable(!process.isRunning(), FuzzingPage.this);
                navigationPage.setFinishable(process.getWorkTotal() > 0 && !process.isRunning(), FuzzingPage.this);
            }
        });
    }

    /**
     * Handles the action of the start/stop button and starts oder stops the fuzzing process.
     */
    @FXML
    private void startStop() {
        if (Model.INSTANCE.getFuzzingProcess().isRunning()) {
            Model.INSTANCE.getFuzzingProcess().interrupt();
        } else {
            Model.INSTANCE.getFuzzingProcess().start();
        }
    }

    /**
     * Starts the timer for displaying the duration of the fuzzing process once it is started. If the timer is already
     * running, it will be reset.
     */
    private void startTimer(final long time) {
        if (processTimer != null) {
            return;
        }
        processTimer = new Timer();
        processTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Fill the time values up to 2 chars with preceding zeros
                final DecimalFormat timeFormat = new DecimalFormat("00");
                // The duration of the current fuzzing process is the current time minus the given start time
                try {
                    final Duration duration = DatatypeFactory.newInstance().newDuration(System.currentTimeMillis() -
                            time);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            timeLabel.setText(timeFormat.format(duration.getHours()) + ":" + timeFormat.format
                                    (duration.getMinutes()) + ":" + timeFormat.format(duration.getSeconds()));
                        }
                    });
                } catch (DatatypeConfigurationException e) {
                    Model.INSTANCE.getLogger().error(e);
                }
            }
        }, 0, 1000);

    }

    /**
     * Stops the timer displaying the fuzzing duration.
     */
    private void stopTimer() {
        if (processTimer == null) {
            return;
        }
        processTimer.cancel();
        processTimer = null;
    }

    @Override
    public void initProcess() {
        Model.INSTANCE.getFuzzingProcess().init();
    }
}
