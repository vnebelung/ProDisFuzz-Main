/*
 * This file is part of ProDisFuzz, modified on 12.07.15 11:08.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.update;


import javafx.scene.control.Alert;

public class UpdateDialog extends Alert {

    /**
     * Instantiates an update dialog responsible for displaying a dialog containing the note about a new version of
     * ProDisFuzz available from website. It also shows the changelog of newer versions.
     */
    public UpdateDialog() {
        super(AlertType.WARNING);
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
