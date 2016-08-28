/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import model.Model;
import model.process.AbstractProcess.State;
import model.process.fuzzing.Process;
import view.controls.LabeledProgressBar;
import view.window.FxmlConnection;
import view.window.Navigation;

import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is the JavaFX based fuzzing page, responsible for visualizing the process of fuzzing the target.
 */
public class FuzzingPage extends VBox implements Observer, Page {

    private final Navigation navigation;
    @FXML
    private Button startStopButton;
    @FXML
    private LabeledProgressBar labeledProgressBar;
    private Timer processTimer;
    @FXML
    private Label timeLabel;
    private boolean isRunning;

    /**
     * Constructs a new fuzzing page.
     *
     * @param navigation the navigation controls
     */
    public FuzzingPage(Navigation navigation) {
        super();
        //noinspection HardCodedStringLiteral
        FxmlConnection.connect(getClass().getResource("/fxml/fuzzingPage.fxml"), this);
        Model.INSTANCE.getFuzzingProcess().addObserver(this);

        this.navigation = navigation;
    }

    /**
     * Handles the action of the start/stop button and starts or stops the fuzzing process.
     */
    @FXML
    private void startStop() {
        if (isRunning) {
            Model.INSTANCE.getFuzzingProcess().stop();
        } else {
            Model.INSTANCE.getFuzzingProcess()
                    .startFuzzing(Model.INSTANCE.getFuzzOptionsProcess().getInjectedProtocolStructure(),
                            Model.INSTANCE.getFuzzOptionsProcess().getTarget(),
                            Model.INSTANCE.getFuzzOptionsProcess().getInterval(),
                            Model.INSTANCE.getFuzzOptionsProcess().getTimeout(),
                            Model.INSTANCE.getFuzzOptionsProcess().getRecordingMethod(),
                            Model.INSTANCE.getFuzzOptionsProcess().getInjectionMethod());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(() -> {
            Process process = (Process) o;
            isRunning = arg == State.RUNNING;

            startStopButton.setText(isRunning ? "Stop" : "Start");

            double progress;
            switch (process.getTotalWork()) {
                case 0:
                    progress = 0;
                    break;
                case -1:
                    progress = isRunning ? -1 : 0;
                    break;
                default:
                    progress = (1.0 * process.getWorkDone()) / process.getTotalWork();
                    break;
            }
            labeledProgressBar.update(progress, isRunning);

            if (isRunning) {
                startTimer(process.getStartTime());
            } else {
                stopTimer();
                if (process.getWorkDone() == 0) {
                    timeLabel.setText("00:00:00");
                }
            }

            navigation.setCancelable(!isRunning, this);
            navigation.setFinishable((process.getWorkDone() > 0) && !isRunning, this);
        });
    }

    /**
     * Starts the timer for displaying the duration of the fuzzing process once it is started. If the timer is already
     * running, it will be reset.
     *
     * @param time the readDirectory time
     */
    private void startTimer(Temporal time) {
        if (processTimer != null) {
            return;
        }
        processTimer = new Timer();
        processTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Fill the time values up to 2 chars with preceding zeros
                DecimalFormat timeFormat = new DecimalFormat("00");
                // The duration of the current fuzzing process is the current time minus the given readDirectory time
                Duration duration = Duration.between(time, Instant.now());
                Platform.runLater(() -> timeLabel.setText(timeFormat.format(duration.toHours()) + ':' +
                        timeFormat.format(duration.toMinutes()) + ':' + timeFormat.format(duration.getSeconds())));
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
    }
}
