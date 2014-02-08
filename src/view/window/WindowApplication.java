/*
 * This file is part of ProDisFuzz, modified on 08.02.14 23:31.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.window;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class WindowApplication extends Application {

    private Window window;

    /**
     * Instantiates a new window responsible for handling the basic JavaFX window.
     */
    public WindowApplication() {
        super();
        initControllers();
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(window);
        stage.setScene(scene);
        stage.setTitle("ProDisFuzz");
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                window.onClose();
            }
        });
    }

    /**
     * Makes the basic window visible.
     */
    public void show() {
        launch();
    }

    /**
     * Initializes the window.
     */
    private void initControllers() {
        window = new Window();
    }
}
