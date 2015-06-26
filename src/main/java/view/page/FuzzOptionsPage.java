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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.Model;
import model.process.fuzzoptions.FuzzOptionsProcess;
import model.process.fuzzoptions.FuzzOptionsProcess.CommunicationSave;
import model.process.fuzzoptions.FuzzOptionsProcess.InjectionMethod;
import model.protocol.InjectedProtocolBlock.DataInjectionMethod;
import model.protocol.InjectedProtocolStructure;
import view.controls.BlockInjection;
import view.controls.ProtocolHexDump;
import view.window.FxmlConnection;
import view.window.Navigation;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

public class FuzzOptionsPage extends VBox implements Observer, Page {

    private final Navigation navigation;
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
    private ProtocolHexDump protocolHexDump;
    @FXML
    private VBox blockInjections;

    /**
     * Instantiates a new fuzz options area responsible for visualizing the process of setting the fuzz options used for
     * fuzzing.
     *
     * @param navigation the navigation controls
     */
    public FuzzOptionsPage(Navigation navigation) {
        super();
        //noinspection HardCodedStringLiteral
        FxmlConnection.connect(getClass().getResource("/fxml/fuzzOptionsPage.fxml"), this);
        Model.INSTANCE.getFuzzOptionsProcess().addObserver(this);
        this.navigation = navigation;

        targetAddressTextField.textProperty().addListener(targetListener());
        targetPortTextField.textProperty().addListener(targetListener());
        timeoutTextField.textProperty().addListener(timeoutListener());
        intervalTextField.textProperty().addListener(intervalListener());
    }

    @Override
    public void update(Observable o, Object arg) {
        FuzzOptionsProcess process = (FuzzOptionsProcess) o;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //noinspection HardCodedStringLiteral
                targetPortTextField.getStyleClass().removeAll("text-field-success", "text-field-fail");
                //noinspection HardCodedStringLiteral
                targetAddressTextField.getStyleClass().removeAll("text-field-success", "text-field-fail");
                if (process.isTargetReachable()) {
                    //noinspection HardCodedStringLiteral
                    targetPortTextField.getStyleClass().add("text-field-success");
                    //noinspection HardCodedStringLiteral
                    targetAddressTextField.getStyleClass().add("text-field-success");
                } else {
                    //noinspection HardCodedStringLiteral
                    targetPortTextField.getStyleClass().add("text-field-fail");
                    //noinspection HardCodedStringLiteral
                    targetAddressTextField.getStyleClass().add("text-field-fail");
                }

                timeoutTextField.setText(String.valueOf(process.getTimeout()));

                intervalTextField.setText(String.valueOf(process.getInterval()));

                simultaneousRadioButton.setSelected(process.getInjectionMethod() == InjectionMethod.SIMULTANEOUS);
                separateRadioButton.setSelected(process.getInjectionMethod() == InjectionMethod
                        .SEPARATE);

                criticalRadioButton.setSelected(process.getSaveCommunication() == CommunicationSave.CRITICAL);
                allRadioButton.setSelected(process.getSaveCommunication() == CommunicationSave.ALL);

                InjectedProtocolStructure injectedProtocolStructure = process.getInjectedProtocolStructure();
                synchronized (this) {
                    protocolHexDump.addProtocolText(injectedProtocolStructure.toProtocolStructure());
                }

                // Update the block injections section only if the number of current block injection modules differs
                // from the number of variable injected protocol blocks
                if (injectedProtocolStructure.getVarSize() != blockInjections.getChildren().size()) {
                    blockInjections.getChildren().clear();
                    // Create new part injection modules for every variable protocol block
                    for (int i = 0; i < injectedProtocolStructure.getVarSize(); i++) {
                        blockInjections.getChildren().add(new BlockInjection(i));
                    }
                }

                for (int i = 0; i < injectedProtocolStructure.getVarSize(); i++) {
                    //noinspection UnqualifiedInnerClassAccess
                    DataInjectionMethod dataInjectionMethod = injectedProtocolStructure
                            .getVarBlock(i).getDataInjectionMethod();
                    boolean enabled = (process.getInjectionMethod() != InjectionMethod.SIMULTANEOUS) || (i == 0);
                    boolean validLibrary = injectedProtocolStructure.getVarBlock(i).getLibrary() != null;
                    ((BlockInjection) blockInjections.getChildren().get(i)).update(dataInjectionMethod, enabled,
                            validLibrary);
                }

                boolean finishable = process.isTargetReachable();
                for (int i = 0; i < injectedProtocolStructure.getVarSize(); i++) {
                    if ((injectedProtocolStructure.getVarBlock(i).getDataInjectionMethod() == DataInjectionMethod
                            .LIBRARY) && (injectedProtocolStructure.getVarBlock(i).getLibrary() == null)) {
                        finishable = false;
                        break;
                    }
                }
                navigation.setFinishable(finishable, FuzzOptionsPage.this);
            }
        });
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
     * Creates the listener for the target address and port. Every change will start a timer so that the user has time
     * to finish his changes before the input is processed.
     *
     * @return the change listener
     */
    private ChangeListener<String> targetListener() {
        return (observableValue, s, s2) -> targetTimer();
    }

    /**
     * Creates the change listener for the timeout text field. Every change is immediately forwarded to the model.
     *
     * @return the change listener
     */
    private ChangeListener<String> timeoutListener() {
        return (observableValue, s, s2) -> {
            try {
                int timeout = Integer.parseInt(timeoutTextField.getText());
                Model.INSTANCE.getFuzzOptionsProcess().setTimeout(timeout);
            } catch (NumberFormatException ignored) {
                // Nothing to do here
            }
        };
    }

    /**
     * Creates the change listener for the interval text field. Every change is immediately forwarded to the model.
     *
     * @return the change listener
     */
    private ChangeListener<String> intervalListener() {
        return (observableValue, s, s2) -> {
            try {
                int interval = Integer.parseInt(intervalTextField.getText());
                Model.INSTANCE.getFuzzOptionsProcess().setInterval(interval);
            } catch (NumberFormatException ignored) {
                // Nothing to do here
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
                } catch (NumberFormatException ignored) {
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
        Model.INSTANCE.getFuzzOptionsProcess().init(Model.INSTANCE.getImportProcess().getProtocolStructure());
    }
}
