/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.window;

import javafx.fxml.FXMLLoader;
import model.Model;

import java.io.IOException;
import java.net.URL;

/**
 * This class is a helper class to connect a JavaFX class to an FXML file.
 */
public enum FxmlConnection {
    ;

    /**
     * Connect the given object to an FXML file defined by the given URL.
     *
     * @param url    the URL to the FXML file
     * @param object the object
     */
    public static void connect(URL url, Object object) {
        FXMLLoader fxmlLoader = new FXMLLoader(url);
        fxmlLoader.setRoot(object);
        fxmlLoader.setController(object);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error(e);
        }
    }
}
