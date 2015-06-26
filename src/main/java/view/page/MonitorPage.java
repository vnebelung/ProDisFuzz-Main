/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.Model;
import model.process.monitor.MonitorProcess;
import view.window.FxmlConnection;
import view.window.Navigation;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

public class MonitorPage extends VBox implements Observer, Page {

    private final Navigation navigation;
    @FXML
    private TextField monitorAddressTextField;
    @FXML
    private TextField monitorPortTextField;
    private Timer monitorTimer;

    /**
     * Instantiates a new monitor area responsible for visualizing the process of setting the monitor options used for
     * connecting to the external monitor.
     *
     * @param navigation the navigation controls
     */
    public MonitorPage(Navigation navigation) {
        super();
        //noinspection HardCodedStringLiteral
        FxmlConnection.connect(getClass().getResource("/fxml/monitorPage.fxml"), this);
        Model.INSTANCE.getMonitorProcess().addObserver(this);
        this.navigation = navigation;

        monitorAddressTextField.textProperty().addListener(monitorListener());
        monitorPortTextField.textProperty().addListener(monitorListener());
    }

    /**
     * Creates the listener for the monitor address and port. Every change will start a timer so that the user has time
     * to finish his changes before the input is processed.
     *
     * @return the change listener
     */
    private ChangeListener<String> monitorListener() {
        return (observableValue, s, s2) -> monitorTimer();
    }

    /**
     * Starts the timer for proceeding the monitor input. If the timer is already running, it will be reset.
     */
    private void monitorTimer() {
        if (monitorTimer != null) {
            monitorTimer.cancel();
        }
        monitorTimer = new Timer();
        monitorTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int port;
                try {
                    port = Integer.parseInt(monitorPortTextField.getText());
                } catch (NumberFormatException ignored) {
                    port = 0;
                }
                Model.INSTANCE.getMonitorProcess().setMonitor(monitorAddressTextField.getText(), port);
            }
        }, 1500);
    }

    @Override
    public void update(Observable o, Object arg) {
        MonitorProcess process = (MonitorProcess) o;
        Platform.runLater(() -> {
            // noinspection HardCodedStringLiteral
            monitorPortTextField.getStyleClass().removeAll("text-field-success", "text-field-fail");
            //noinspection HardCodedStringLiteral
            monitorAddressTextField.getStyleClass().removeAll("text-field-success", "text-field-fail");
            if (process.isMonitorReachable()) {
                //noinspection HardCodedStringLiteral
                monitorPortTextField.getStyleClass().add("text-field-success");
                //noinspection HardCodedStringLiteral
                monitorAddressTextField.getStyleClass().add("text-field-success");
            } else {
                //noinspection HardCodedStringLiteral
                monitorPortTextField.getStyleClass().add("text-field-fail");
                //noinspection HardCodedStringLiteral
                monitorAddressTextField.getStyleClass().add("text-field-fail");
            }

            navigation.setFinishable(process.isMonitorReachable(), this);
        });
    }

    @Override
    public void initProcess() {
    }
}
