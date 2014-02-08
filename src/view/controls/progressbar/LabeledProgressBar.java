/*
 * This file is part of ProDisFuzz, modified on 08.02.14 17:37.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.controls.progressbar;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import view.window.ConnectionHelper;

public class LabeledProgressBar extends StackPane {

    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label label;

    /**
     * Instantiates a new labeled progress bar responsible for visualizing the progress of a process.
     */
    public LabeledProgressBar() {
        super();
        ConnectionHelper.connect(getClass().getResource("labeledProgressBar.fxml"), this);
    }

    /**
     * Updates the labeled progress bar. The update includes setting the visual progress of the progress bar and
     * setting the textual label to display the progress percentage.
     *
     * @param progress the absolute progress. A negative value for progress indicates that the progress is
     *                 indeterminate. A positive value between 0 and 1 indicates the percentage of progress where 0
     *                 is 0% and 1 is 100%. Any value greater than 1 is interpreted as 100%.
     * @param running  true, if the process that is visualized through this progress bar is active
     */
    public void update(double progress, boolean running) {
        progressBar.setProgress(progress);
        progressBar.getStyleClass().removeAll("progress-bar-fail", "progress-bar-success");
        progressBar.setStyle("");
        StringBuilder labelText = new StringBuilder();
        int progressPercentage = (int) Math.ceil(100 * progress);
        if (running) {
            labelText.append("Running … ");
        }
        labelText.append(progress < 0 ? "infinite" : progressPercentage + " %");
        if (progress == 1 || progress < 0) {
            progressBar.getStyleClass().add("progress-bar-success");
        } else if (running) {
            // Set dynamic background color depending on the progress
            int color = (int) (255 * progress);
            progressBar.setStyle("-fx-background-color: rgba(" + (255 - color) + ", " + color + ", 0, 0.4);");
        } else {
            progressBar.getStyleClass().add("progress-bar-fail");
        }
        label.setText(labelText.toString());
    }
}
