/*
 * This file is part of ProDisFuzz, modified on 02.03.14 00:25.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page.learn;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import model.Model;
import model.process.learn.LearnProcess;
import view.controls.progressbar.LabeledProgressBar;
import view.controls.protocolcontent.ProtocolContent;
import view.page.Page;
import view.window.FxmlConnection;
import view.window.Navigation;

import java.util.Observable;
import java.util.Observer;

public class LearnPage extends VBox implements Observer, Page {

    @FXML
    private LabeledProgressBar labeledProgressBar;
    @FXML
    private Button startStopButton;
    private Navigation navigation;
    @FXML
    private ProtocolContent protocolContent;

    /**
     * Instantiates a new learn area responsible for visualizing the process of learning the protocol sequence.
     */
    public LearnPage(Navigation n) {
        super();
        FxmlConnection.connect(getClass().getResource("learnPage.fxml"), this);
        Model.INSTANCE.getLearnProcess().addObserver(this);
        navigation = n;
    }

    @Override
    public void update(Observable o, Object arg) {
        final LearnProcess process = (LearnProcess) o;

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                startStopButton.setText(process.isRunning() ? "Stop" : "Start");

                double progress = process.getWorkTotal() == 0 ? 0 : 1.0 * process.getWorkProgress() / process
                        .getWorkTotal();
                labeledProgressBar.update(progress, process.isRunning());

                synchronized (this) {
                    protocolContent.addProtocolText(process.getProtocolParts());
                }

                navigation.setCancelable(!process.isRunning(), LearnPage.this);
                navigation.setFinishable(process.getWorkTotal() == process.getWorkProgress(), LearnPage.this);
            }
        });
    }

    @FXML
    private void startStop() {
        if (Model.INSTANCE.getLearnProcess().isRunning()) {
            Model.INSTANCE.getLearnProcess().interrupt();
        } else {
            Model.INSTANCE.getLearnProcess().start();
        }
    }

    @Override
    public void initProcess() {
        Model.INSTANCE.getLearnProcess().init();
    }
}
