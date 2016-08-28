/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.update.UpdateTimer;
import view.window.Window;

/**
 * This class is the application window that defines the JavaFX scene.
 */
public class WindowApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        Window window = new Window();

        Scene scene = new Scene(window);
        //noinspection HardCodedStringLiteral
        scene.getStylesheets().add("/css/scene.css");
        primaryStage.setScene(scene);
        primaryStage.setTitle("ProDisFuzz");
        primaryStage.setOnCloseRequest(windowEvent -> window.onClose());
        primaryStage.minHeightProperty().setValue(768);
        primaryStage.minWidthProperty().setValue(1024);
        primaryStage.show();

        initUpdateCheck();
    }

    /**
     * Starts the check for an update of ProDisFuzz.
     *
     */
    @SuppressWarnings("MethodMayBeStatic")
    private void initUpdateCheck() {
        new UpdateTimer().start();
    }

    /**
     * Makes the basic window visible.
     */
    @SuppressWarnings("MethodMayBeStatic")
    public void show() {
        Application.launch();
    }
}
