/*
 * This file is part of ProDisFuzz, modified on 6/28/15 12:31 AM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
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

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Observable;
import java.util.Observer;

public class Log extends ScrollPane implements Observer {

    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    @FXML
    private TextFlow textFlow;

    /**
     * Instantiates a new log area responsible for visualizing the log.
     */
    public Log() {
        super();
        //noinspection HardcodedFileSeparator,HardCodedStringLiteral,ThisEscapedInObjectConstruction
        FxmlConnection.connect(getClass().getResource("/fxml/log.fxml"), this);
        //noinspection ThisEscapedInObjectConstruction
        Model.INSTANCE.getLogger().addObserver(this);

        textFlow.heightProperty().addListener((observable, oldValue, newValue) -> setVvalue((Double) newValue));
    }

    /**
     * Returns a styled text based on the given log message.
     *
     * @param message the log message
     * @return the styled text
     */
    private static Text styleText(Message message) {
        StringBuilder stringBuilder = new StringBuilder();
        Text result = new Text();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss").withZone(ZoneId.systemDefault());
        //noinspection HardCodedStringLiteral
        stringBuilder.append('[').append(formatter.format(message.getTime())).append("] ");
        switch (message.getType()) {
            case ERROR:
                //noinspection HardCodedStringLiteral
                result.getStyleClass().add("error");
                stringBuilder.append("[ERROR] ");
                break;
            case WARNING:
                //noinspection HardCodedStringLiteral
                result.getStyleClass().add("warning");
                stringBuilder.append("[WARNING] ");
                break;
            case FINE:
                //noinspection HardCodedStringLiteral
                result.getStyleClass().add("success");
                stringBuilder.append("[SUCCESS] ");
                break;
            default:
                //noinspection HardCodedStringLiteral
                result.getStyleClass().add("info");
                stringBuilder.append("[INFO] ");
                break;
        }
        stringBuilder.append(message.getText()).append(System.lineSeparator());
        result.setText(stringBuilder.toString());
        return result;
    }

    @Override
    public void update(Observable o, Object arg) {
        Logger logger = (Logger) o;
        Platform.runLater(() -> {
            Message[] messages = logger.getUnreadEntries();
            if (messages.length == 0) {
                return;
            }
            for (Message each : messages) {
                textFlow.getChildren().add(styleText(each));
            }
            purge();

        });
    }

    /**
     * Deletes old messages if the number of messages is greater than the given threshold.
     */
    private void purge() {
        while (textFlow.getChildren().size() > 500) {
            textFlow.getChildren().remove(0);
        }
    }
}
