/*
 * This file is part of ProDisFuzz, modified on 02.03.14 00:25.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.controls.control;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import view.window.FxmlConnection;
import view.window.NavigationControl;

public class ControlBar extends HBox {

    private final MemoryTimer memoryTimer;
    @FXML
    private Button cancelButton;
    @FXML
    private Button nextButton;
    @FXML
    private Button finishButton;
    @FXML
    private Label memoryUsage;
    private NavigationControl navigationControl;

    /**
     * Instantiates a new control area responsible for displaying various navigation controls and information elements.
     */
    public ControlBar() {
        super();
        FxmlConnection.connect(getClass().getResource("controlBar.fxml"), this);
        memoryTimer = new MemoryTimer(memoryUsage);
        memoryTimer.start();
    }

    /**
     * Sets the visibility status of the navigation buttons. All navigation buttons can be hidden if they are not
     * needed.
     *
     * @param b true, if the buttons shall be visible. Default is true.
     */
    public void setNavigationVisible(boolean b) {
        cancelButton.setVisible(b);
        nextButton.setVisible(b);
        finishButton.setVisible(b);
    }

    /**
     * Stops the memory timer.
     */
    public void onClose() {
        memoryTimer.stop();
    }

    /**
     * Handles the action of the cancel button.
     */
    @FXML
    private void cancel() {
        navigationControl.resetPage();
    }

    /**
     * Handles the action of the finish button.
     */
    @FXML
    private void finish() {
        navigationControl.resetPage();
    }

    /**
     * Handles the action of the next button.
     */
    @FXML
    private void next() {
        navigationControl.nextPage();
    }

    /**
     * Sets the enabled status of the next button.
     *
     * @param b true, if the button should be enabled
     */
    public void setNextEnabled(boolean b) {
        nextButton.setDisable(!b);
    }

    /**
     * Sets the enabled status of the cancel button.
     *
     * @param b true, if the button should be enabled
     */
    public void setCancelEnabled(boolean b) {
        cancelButton.setDisable(!b);
    }

    /**
     * Sets the enabled status of the finish button.
     *
     * @param b true, if the button should be enabled
     */
    public void setFinishEnabled(boolean b) {
        finishButton.setDisable(!b);
    }

    /**
     * Sets the navigation control that handles the actions to navigate through pages.
     *
     * @param n the navigation control
     */
    public void setNavigationControl(NavigationControl n) {
        navigationControl = n;
    }
}
