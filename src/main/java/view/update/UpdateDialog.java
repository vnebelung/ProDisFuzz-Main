/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.update;


import javafx.scene.control.Alert;

/**
 * This class is the JavaFX based update dialog, responsible for displaying a dialog containing the note about a new
 * version of ProDisFuzz available from website. It also shows the changelog of newer versions.
 */
public class UpdateDialog extends Alert {

    /**
     * Constructs a new update dialog.
     */
    public UpdateDialog() {
        super(AlertType.WARNING);
        //noinspection HardCodedStringLiteral
        getDialogPane().getStylesheets().add("/css/dialog.css");
    }

    /**
     * Sets the text to show as changelog details.
     *
     * @param changelog The string containing the newer program version and the changelog
     */
    public void setChangelog(String changelog) {
        getDialogPane().setExpandableContent(new UpdateInformation(changelog));

    }
}
