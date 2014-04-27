/*
 * This file is part of ProDisFuzz, modified on 04.04.14 19:38.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.log;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.Model;
import model.logger.Logger;
import model.logger.Message;
import view.window.FxmlConnection;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Log extends ScrollPane implements Observer {

    @FXML
    private TextFlow logTextFlow;

    /**
     * Instantiates a new log area responsible for visualizing the log.
     */
    public Log() {
        super();
        FxmlConnection.connect(getClass().getResource("/fxml/log.fxml"), this);
        Model.INSTANCE.getLogger().addObserver(this);

        logTextFlow.heightProperty().addListener((observable, oldvalue, newValue) -> setVvalue((Double) newValue));
    }

    @Override
    public void update(Observable o, Object arg) {
        Logger logger = (Logger) o;
        Platform.runLater(() -> {
            List<Message> messages = logger.getUnreadEntries();
            if (messages.size() == 0) {
                return;
            }
            for (Message each : messages) {
                logTextFlow.getChildren().add(styleText(each));
            }
            purge();

        });
    }

    /**
     * Returns a styled text based on the given log message.
     *
     * @param message the log message
     * @return the styled text
     */
    private Text styleText(Message message) {
        StringBuilder stringBuilder = new StringBuilder();
        Text result = new Text();
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
        stringBuilder.append('[').append(dateFormat.format(message.getTime())).append("] ");
        switch (message.getType()) {
            case ERROR:
                result.getStyleClass().add("error");
                stringBuilder.append("[ERROR] ");
                break;
            case WARNING:
                result.getStyleClass().add("warning");
                stringBuilder.append("[WARNING] ");
                break;
            case FINE:
                result.getStyleClass().add("success");
                stringBuilder.append("[SUCCESS] ");
                break;
            default:
                result.getStyleClass().add("info");
                stringBuilder.append("[INFO] ");
                break;
        }
        stringBuilder.append(message.getText()).append(System.lineSeparator());
        result.setText(stringBuilder.toString());
        return result;
    }

    /**
     * Deletes old messages if the number of messages is greater than the given threshold.
     */
    private void purge() {
        while (logTextFlow.getChildren().size() > 500) {
            logTextFlow.getChildren().remove(0);
        }
    }
}
