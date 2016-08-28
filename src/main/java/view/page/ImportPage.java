/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import model.Model;
import model.process.import_.Process;
import view.controls.ProtocolHexDump;
import view.window.FxmlConnection;
import view.window.Navigation;

import java.io.File;
import java.nio.file.Path;
import java.util.Observable;
import java.util.Observer;

/**
 * This class is the JavaFX based import page, responsible for visualizing the process of importing the protocol
 * structure from an XML file.
 */
public class ImportPage extends VBox implements Observer, Page {

    private final Navigation navigation;
    @FXML
    private TextField fileTextField;
    private Path loadPath;
    @FXML
    private ProtocolHexDump protocolContent;

    /**
     * Constructs a new import page.
     *
     * @param navigation the navigation controls
     */
    public ImportPage(Navigation navigation) {
        super();
        //noinspection HardCodedStringLiteral
        FxmlConnection.connect(getClass().getResource("/fxml/importPage.fxml"), this);
        Model.INSTANCE.getImportProcess().addObserver(this);
        this.navigation = navigation;
    }

    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(() -> {
            Process process = (Process) o;

            //noinspection HardCodedStringLiteral
            fileTextField.getStyleClass().removeAll("text-field-success", "text-field-fail");

            if (process.isComplete()) {
                //noinspection HardCodedStringLiteral
                fileTextField.getStyleClass().add("text-field-success");
                fileTextField.setText("Successfully imported from '" + loadPath + '\'');
            } else {
                //noinspection HardCodedStringLiteral
                fileTextField.getStyleClass().add("text-field-fail");
                fileTextField.setText((loadPath == null) ?
                        ("Please choose the file path the protocol structure will be " + "imported from") :
                        ("Could not import from '" + loadPath + '\''));
            }

            protocolContent.addProtocolText(process.getProtocolStructure());

            navigation.setCancelable(true, this);
            navigation.setFinishable(process.isComplete(), this);
        });
    }

    @FXML
    private void browse() {
        FileChooser fileChooser = new FileChooser();
        //noinspection HardCodedStringLiteral
        fileChooser.getExtensionFilters().add(new ExtensionFilter("XML", "*.xml"));
        File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file == null) {
            return;
        }
        loadPath = file.toPath().toAbsolutePath();
        Model.INSTANCE.getImportProcess().importProtocolStructure(loadPath);
    }

    @Override
    public void initProcess() {
    }
}
