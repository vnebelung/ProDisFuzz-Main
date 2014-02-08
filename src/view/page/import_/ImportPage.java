/*
 * This file is part of ProDisFuzz, modified on 08.02.14 23:36.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page.import_;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import model.Model;
import model.process.import_.ImportProcess;
import view.controls.protocolcontent.ProtocolContent;
import view.page.Page;
import view.window.ConnectionHelper;
import view.window.Navigation;

import java.io.File;
import java.nio.file.Path;
import java.util.Observable;
import java.util.Observer;

public class ImportPage extends GridPane implements Observer, Page {

    private Navigation navigation;
    @FXML
    private TextField fileTextField;
    private Path loadPath;
    @FXML
    private ProtocolContent protocolContent;

    /**
     * Instantiates a new import area responsible for visualizing the process of importing the protocol structure
     * from an XML file.
     */
    public ImportPage(Navigation n) {
        super();
        ConnectionHelper.connect(getClass().getResource("importPage.fxml"), this);
        Model.INSTANCE.getImportProcess().addObserver(this);
        navigation = n;
    }

    @Override
    public void update(Observable o, Object arg) {
        ImportProcess process = (ImportProcess) o;

        fileTextField.getStyleClass().removeAll("text-field-success", "text-field-fail");
        if (process.isImported()) {
            fileTextField.getStyleClass().add("text-field-success");
            fileTextField.setText("Successfully imported from '" + loadPath.toString() + "'");
            protocolContent.addProtocolText(process.getProtocolParts());
        } else {
            fileTextField.getStyleClass().add("text-field-fail");
            fileTextField.setText(loadPath == null ? "Please choose the file path the protocol structure will be " +
                    "imported from" : "Could not import from '" + loadPath.toString() + "'");
        }

        navigation.setCancelable(true, ImportPage.this);
        navigation.setFinishable(process.isImported(), ImportPage.this);
    }

    @FXML
    private void browse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
        File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file == null) {
            return;
        }
        loadPath = file.toPath().toAbsolutePath();
        Model.INSTANCE.getImportProcess().importXML(loadPath);
    }

    @Override
    public void initProcess() {
        Model.INSTANCE.getImportProcess().init();
    }
}
