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
import javafx.scene.layout.VBox;
import model.Model;
import model.process.AbstractProcess.State;
import model.process.learn.Process;
import view.controls.LabeledProgressBar;
import view.controls.ProtocolHexDump;
import view.window.FxmlConnection;
import view.window.Navigation;

import java.util.Observable;
import java.util.Observer;

/**
 * This class is the JavaFX based learn page, responsible for visualizing the process of learning the protocol
 * sequence.
 */
public class LearnPage extends VBox implements Observer, Page {

    private Navigation navigation;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    @FXML
    private LabeledProgressBar labeledProgressBar;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    @FXML
    private Button startStopButton;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    @FXML
    private ProtocolHexDump protocolContent;
    private boolean isRunning;

    /**
     * Constructs a new learn page.
     *
     * @param navigation the navigation controls
     */
    public LearnPage(Navigation navigation) {
        super();
        // noinspection HardCodedStringLiteral,ThisEscapedInObjectConstruction
        FxmlConnection.connect(getClass().getResource("/fxml/learnPage.fxml"), this);
        //noinspection ThisEscapedInObjectConstruction
        Model.INSTANCE.getLearnProcess().addObserver(this);
        this.navigation = navigation;
    }

    @FXML
    private void startStop() {
        if (isRunning) {
            Model.INSTANCE.getLearnProcess().stop();
        } else {
            Model.INSTANCE.getLearnProcess()
                    .learnProtocolStructure(Model.INSTANCE.getCollectProcess().getSelectedFiles());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        Process process = (Process) o;
        isRunning = arg == State.RUNNING;

        Platform.runLater(() -> {
            startStopButton.setText(isRunning ? "Stop" : "Start");

            double progress =
                    (process.getTotalWork() == 0) ? 0 : ((1.0 * process.getWorkDone()) / process.getTotalWork());
            labeledProgressBar.update(progress, isRunning);

            synchronized (this) {
                protocolContent.addProtocolText(process.getProtocolStructure());
            }

            navigation.setCancelable(!isRunning, this);
            navigation.setFinishable(process.getTotalWork() == process.getWorkDone(), this);
        });
    }

    @Override
    public void initProcess() {
    }
}
