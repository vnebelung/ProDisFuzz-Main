/*
 * This file is part of ProDisFuzz, modified on 08.02.14 22:54.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page.export;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import model.Model;
import model.process.export.ExportProcess;
import view.page.Page;
import view.window.ConnectionHelper;
import view.window.Navigation;

import java.io.File;
import java.nio.file.Path;
import java.util.Observable;
import java.util.Observer;

public class ExportPage extends GridPane implements Observer, Page {

    private Navigation navigation;
    @FXML
    private TextField fileTextField;
    private Path savePath;

    /**
     * Instantiates a new export area responsible for visualizing the process of exporting the protocol structure to
     * XML.
     */
    public ExportPage(Navigation n) {
        super();
        ConnectionHelper.connect(getClass().getResource("exportPage.fxml"), this);
        Model.INSTANCE.getExportProcess().addObserver(this);
        navigation = n;
    }

    @Override
    public void update(Observable o, Object arg) {
        ExportProcess process = (ExportProcess) o;

        fileTextField.getStyleClass().removeAll("text-field-success", "text-field-fail");
        if (process.isExported()) {
            fileTextField.getStyleClass().add("text-field-success");
            fileTextField.setText("Successfully exported to '" + savePath.toString() + "'");
        } else {
            fileTextField.getStyleClass().add("text-field-fail");
            fileTextField.setText(savePath == null ? "Please choose the file the protocol structure will be exported " +
                    "" + "to" : "Could not export to '" + savePath.toString() + "'");
        }

        navigation.setCancelable(!process.isExported(), this);
        navigation.setFinishable(process.isExported(), this);
    }

    @FXML
    private void browse() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file == null) {
            return;
        }
        savePath = file.toPath().toAbsolutePath();
        if (!savePath.toString().endsWith(".xml")) {
            savePath = savePath.getParent().resolve(savePath.getFileName().toString() + ".xml");
        }
        Model.INSTANCE.getExportProcess().exportXML(savePath);
    }

    @Override
    public void initProcess() {
        Model.INSTANCE.getExportProcess().init();
    }
}