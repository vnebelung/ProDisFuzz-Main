/*
 * This file is part of ProDisFuzz, modified on 02.03.14 00:25.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.window;

import javafx.fxml.FXMLLoader;
import model.Model;

import java.io.IOException;
import java.net.URL;

public class FxmlConnection {

    /**
     * Connect a given object to a FXML file defined by the given URL.
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
