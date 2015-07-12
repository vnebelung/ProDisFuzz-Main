/*
 * This file is part of ProDisFuzz, modified on 12.07.15 12:00.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.update;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import view.window.FxmlConnection;

public class UpdateInformation extends VBox {

    @FXML
    private TextArea textArea;

    /**
     * Instantiates an area responsible for visualizing the changelog of newer versions of ProDisFuzz than the current
     * one.
     *
     * @param text the text containing the changelog
     */
    public UpdateInformation(String text) {
        super();
        //noinspection HardCodedStringLiteral
        FxmlConnection.connect(getClass().getResource("/fxml/updateInformation.fxml"), this);
        textArea.setText(text);
    }


}
