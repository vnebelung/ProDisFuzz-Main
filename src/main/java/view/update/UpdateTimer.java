/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.update;

import javafx.application.Platform;
import model.Model;
import model.updater.ReleaseInformation;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateTimer {

    private final Timer timer;

    /**
     * Instantiates a new timer for displaying an update dialog if there is a newer version available and ProDisFuzz is
     * able to connect to its remote update information. If there is no newer version available or the remote source is
     * not reachable, the dialog will not be displayed.
     */
    public UpdateTimer() {
        timer = new Timer();
    }

    /**
     * Starts the timer.
     */
    public void start() {
        timer.schedule(new MyTimerTask(), 500);
    }

    private static class MyTimerTask extends TimerTask {
        /**
         * Reads all information about newer program releases. Every release is listed with its release date, its
         * release number and name change log items. Every of the release's change log items is listed with a leading
         * bullet point.
         *
         * @return the aggregated release information
         */
        private static String gatherReleaseInformation() {
            ReleaseInformation[] releaseInformation = Model.INSTANCE.getUpdateCheck().getReleaseInformation();
            StringBuilder updates = new StringBuilder();
            for (ReleaseInformation eachRelease : releaseInformation) {
                updates.append(System.lineSeparator());
                updates.append(eachRelease.getDate()).append(", Version ").append(eachRelease.getNumber()).append(" \"")
                        .append(eachRelease.getName()).append("\":").append(System.lineSeparator());
                updates.append('(').append(eachRelease.getRequirements()).append(')').append(System.lineSeparator());
                for (String eachInformation : eachRelease.getInformation()) {
                    updates.append("- ").append(eachInformation).append(System.lineSeparator());
                }
            }
            return updates.toString();
        }

        @Override
        public void run() {
            Platform.runLater(() -> {
                if (!Model.INSTANCE.getUpdateCheck().hasUpdate()) {
                    return;
                }
                UpdateDialog updateDialog = new UpdateDialog();
                updateDialog.setTitle("Update Information");
                updateDialog.setHeaderText("Newer version of ProDisFuzz available");
                updateDialog.setContentText("Download the new version at http://prodisfuzz.net.");
                updateDialog.setChangelog(gatherReleaseInformation());
                updateDialog.showAndWait();
            });
        }
    }
}
