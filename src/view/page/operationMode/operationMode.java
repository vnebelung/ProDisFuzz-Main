/*
 * This file is part of ProDisFuzz, modified on 08.02.14 23:31.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page.operationMode;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import view.window.ConnectionHelper;
import view.window.NavigationControl;

public class OperationMode extends GridPane {

    private NavigationControl navigationControl;

    /**
     * Instantiates a new control area responsible for displaying various navigation controls and information elements.
     *
     * @param navigationControl the navigation control used to navigate
     */
    public OperationMode(NavigationControl navigationControl) {
        super();
        ConnectionHelper.connect(getClass().getResource("operationMode.fxml"), this);
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
