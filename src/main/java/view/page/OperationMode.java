/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import view.window.FxmlConnection;
import view.window.NavigationControl;

/**
 * This class is the JavaFX based operation mode page, responsible for displaying various navigation controls and
 * information elements.
 */
public class OperationMode extends VBox {

    private final NavigationControl navigationControl;

    /**
     * Constructs a new operation mode page.
     *
     * @param navigationControl the navigation control used to navigate
     */
    public OperationMode(NavigationControl navigationControl) {
        super();
        //noinspection HardCodedStringLiteral
        FxmlConnection.connect(getClass().getResource("/fxml/operationMode.fxml"), this);
        this.navigationControl = navigationControl;
    }

    /**
     * Handles the action of the learn button.
     */
    @FXML
    private void learnButtonAction() {
        navigationControl.enterLearnMode();
    }

    /**
     * Handles the action of the fuzzing button.
     */
    @FXML
    private void fuzzingButtonAction() {
        navigationControl.enterFuzzMode();
    }
}
