/*
 * This file is part of ProDisFuzz, modified on 30.03.14 17:49.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.window;

import javafx.application.Platform;
import javafx.stage.Stage;
import model.Model;
import model.updater.ReleaseInformation;
import org.controlsfx.dialog.Dialogs;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateTimer {

    private final Timer timer;
    private final Stage stage;

    /**
     * Instantiates a new timer for displaying an update dialog if there is a newer version available and ProDisFuzz is
     * able to connect to its remote update information. If there is no newer version available or the remote source is
     * not reachable, the dialog will not be displayed.
     *
     * @param stage the stage the update information will be displayed on
     */
    public UpdateTimer(Stage stage) {
        this.stage = stage;
        timer = new Timer();
    }

    /**
     * Starts the timer.
     */
    public void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (!Model.INSTANCE.getUpdateCheck().hasUpdate()) {
                        return;
                    }
                    ReleaseInformation[] releaseInformation = Model.INSTANCE.getUpdateCheck().getReleaseInformation();
                    StringBuilder updates = new StringBuilder();
                    for (ReleaseInformation eachRelease : releaseInformation) {
                        updates.append(System.lineSeparator());
                        updates.append(eachRelease.getDate()).append(", Version ").append(eachRelease.getNumber())
                                .append(" \"").append(eachRelease.getName()).append("\":").append(System
                                .lineSeparator());
                        updates.append("(").append(eachRelease.getRequirements()).append(")").append(System
                                .lineSeparator());
                        for (String eachInformation : eachRelease.getInformation()) {
                            updates.append("- ").append(eachInformation).append(System.lineSeparator());
                        }
                    }
                    Dialogs dialog = Dialogs.create();
                    dialog.lightweight();
                    dialog.owner(stage);
                    dialog.title("Update Available");
                    dialog.masthead(updates.toString());
                    dialog.message("Download the new version at http://prodisfuzz.net.");
                    dialog.showInformation();
                });
            }
        }, 2000);
    }
}
