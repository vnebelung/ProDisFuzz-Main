/*
 * This file is part of ProDisFuzz, modified on 08.02.14 23:36.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page.fuzzOptions;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.InjectedProtocolPart;
import model.Model;
import model.ProtocolPart;
import model.process.fuzzOptions.FuzzOptionsProcess;
import view.controls.partinjection.PartInjection;
import view.controls.protocolcontent.ProtocolContent;
import view.page.Page;
import view.window.ConnectionHelper;
import view.window.Navigation;

import java.util.*;

public class FuzzOptionsPage extends GridPane implements Observer, Page {

    private Navigation navigation;
    @FXML
    private TextField targetAddressTextField;
    @FXML
    private TextField targetPortTextField;
    @FXML
    private TextField intervalTextField;
    @FXML
    private TextField timeoutTextField;
    @FXML
    private RadioButton simultaneousRadioButton;
    @FXML
    private RadioButton separateRadioButton;
    @FXML
    private RadioButton criticalRadioButton;
    @FXML
    private RadioButton allRadioButton;
    private Timer targetTimer;
    @FXML
    private ProtocolContent protocolContent;
    @FXML
    private VBox partInjections;

    /**
     * Instantiates a new fuzz options area responsible for visualizing the process of setting the fuzz options used
     * for fuzzing.
     *
     * @param n the navigation interface
     */
    public FuzzOptionsPage(Navigation n) {
        super();
        ConnectionHelper.connect(getClass().getResource("fuzzOptionsPage.fxml"), this);
        Model.INSTANCE.getFuzzOptionsProcess().addObserver(this);
        navigation = n;

        targetAddressTextField.textProperty().addListener(targetListener());
        targetPortTextField.textProperty().addListener(targetListener());
        timeoutTextField.textProperty().addListener(timeoutListener());
        intervalTextField.textProperty().addListener(intervalListener());
    }

    @Override
    public void update(Observable o, Object arg) {
        FuzzOptionsProcess process = (FuzzOptionsProcess) o;

        targetPortTextField.getStyleClass().removeAll("text-field-success", "text-field-fail");
        targetAddressTextField.getStyleClass().removeAll("text-field-success", "text-field-fail");
        if (process.isTargetReachable()) {
            targetPortTextField.getStyleClass().add("text-field-success");
            targetAddressTextField.getStyleClass().add("text-field-success");
        } else {
            targetPortTextField.getStyleClass().add("text-field-fail");
            targetAddressTextField.getStyleClass().add("text-field-fail");
        }

        timeoutTextField.setText(String.valueOf(process.getTimeout()));

        intervalTextField.setText(String.valueOf(process.getInterval()));

        simultaneousRadioButton.setSelected(process.getInjectionMethod() == FuzzOptionsProcess.InjectionMethod
                .SIMULTANEOUS);
        separateRadioButton.setSelected(process.getInjectionMethod() == FuzzOptionsProcess.InjectionMethod.SEPARATE);

        criticalRadioButton.setSelected(process.getSaveCommunication() == FuzzOptionsProcess.CommunicationSave
                .CRITICAL);
        allRadioButton.setSelected(process.getSaveCommunication() == FuzzOptionsProcess.CommunicationSave.ALL);

        synchronized (this) {
            List<ProtocolPart> parts = new ArrayList<>(process.getInjectedProtocolParts().size());
            for (InjectedProtocolPart each : process.getInjectedProtocolParts()) {
                parts.add(each.getProtocolPart());
            }
            protocolContent.addProtocolText(parts);
        }

        List<InjectedProtocolPart> injectedProtocolParts = process.getInjectedProtocolParts();
        List<InjectedProtocolPart> injectedVariableProtocolParts = process.filterVarParts(injectedProtocolParts);
        // Update the part injections section only if the number of current part injection modules is different from
        // the number of variable injected protocol parts
        if (process.filterVarParts(injectedProtocolParts).size() != partInjections.getChildren().size()) {
            partInjections.getChildren().clear();
            // Create new part injection modules for every variable protocol part
            for (int i = 0; i < injectedProtocolParts.size(); i++) {
                if (injectedProtocolParts.get(i).getProtocolPart().getType() == ProtocolPart.Type.VAR) {
                    partInjections.getChildren().add(new PartInjection(i));
                }
            }
        }

        for (int i = 0; i < injectedVariableProtocolParts.size(); i++) {
            InjectedProtocolPart.DataInjectionMethod dataInjectionMethod = injectedVariableProtocolParts.get(i)
                    .getDataInjectionMethod();
            boolean enabled = (process.getInjectionMethod() != FuzzOptionsProcess.InjectionMethod.SIMULTANEOUS || i
                    == 0);
            boolean validLibrary = injectedVariableProtocolParts.get(i).getLibrary() != null;
            ((PartInjection) partInjections.getChildren().get(i)).update(dataInjectionMethod, enabled, validLibrary);
        }

        boolean finishable = process.isTargetReachable();
        for (InjectedProtocolPart each : injectedProtocolParts) {
            if (each.getProtocolPart().getType() == ProtocolPart.Type.VAR && each.getDataInjectionMethod() ==
                    InjectedProtocolPart.DataInjectionMethod.LIBRARY && each.getLibrary() == null) {
                finishable = false;
                break;
            }
        }
        navigation.setFinishable(finishable, this);
    }

    @FXML
    private void dataInjection() {
        if (simultaneousRadioButton.isSelected()) {
            Model.INSTANCE.getFuzzOptionsProcess().setSimultaneousInjectionMode();
        } else if (separateRadioButton.isSelected()) {
            Model.INSTANCE.getFuzzOptionsProcess().setSeparateInjectionMode();
        }
    }

    /**
     * Creates the listener for the target address and port. Every change will start a timer so that the user has
     * time to finish his changes before the input is processed.
     *
     * @return the change listener
     */
    private ChangeListener<String> targetListener() {
        return new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                targetTimer();
            }
        };
    }

    /**
     * Creates the change listener for the timeout text field. Every change is immediately forwarded to the model.
     *
     * @return the change listener
     */
    private ChangeListener<String> timeoutListener() {
        return new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                try {
                    int timeout = Integer.parseInt(timeoutTextField.getText());
                    Model.INSTANCE.getFuzzOptionsProcess().setTimeout(timeout);
                } catch (NumberFormatException e) {
                    // Nothing to do here
                }
            }
        };
    }

    /**
     * Creates the change listener for the interval text field. Every change is immediately forwarded to the model.
     *
     * @return the change listener
     */
    private ChangeListener<String> intervalListener() {
        return new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
                try {
                    int interval = Integer.parseInt(intervalTextField.getText());
                    Model.INSTANCE.getFuzzOptionsProcess().setInterval(interval);
                } catch (NumberFormatException e) {
                    // Nothing to do here
                }
            }
        };
    }

    /**
     * Starts the timer for proceeding the target input. If the timer is already running, it will be reset.
     */
    private void targetTimer() {
        if (targetTimer != null) {
            targetTimer.cancel();
        }
        targetTimer = new Timer();
        targetTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                int port;
                try {
                    port = Integer.parseInt(targetPortTextField.getText());
                } catch (NumberFormatException e) {
                    port = 0;
                }
                Model.INSTANCE.getFuzzOptionsProcess().setTarget(targetAddressTextField.getText(), port);
            }
        }, 1500);
    }

    @FXML
    private void saveCommunication() {
        if (criticalRadioButton.isSelected()) {
            Model.INSTANCE.getFuzzOptionsProcess().setSaveCriticalCommunication();
        } else if (allRadioButton.isSelected()) {
            Model.INSTANCE.getFuzzOptionsProcess().setSaveAllCommunication();
        }
    }

    @Override
    public void initProcess() {
        Model.INSTANCE.getFuzzOptionsProcess().init();
    }
}
