/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
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
import model.Model;
import model.process.export.Process;
import view.window.FxmlConnection;
import view.window.Navigation;

import java.io.File;
import java.nio.file.Path;
import java.util.Observable;
import java.util.Observer;

/**
 * This class is the JavaFX based export page, responsible for visualizing the process of exporting the protocol
 * structure to XML.
 */
public class ExportPage extends VBox implements Observer, Page {

    private final Navigation navigation;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    @FXML
    private TextField fileTextField;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private Path savePath;

    /**
     * Constructs a new export page.
     *
     * @param navigation the navigation controls
     */
    public ExportPage(Navigation navigation) {
        super();
        // noinspection ThisEscapedInObjectConstruction,HardCodedStringLiteral
        FxmlConnection.connect(getClass().getResource("/fxml/exportPage.fxml"), this);
        //noinspection ThisEscapedInObjectConstruction
        Model.INSTANCE.getExportProcess().addObserver(this);
        this.navigation = navigation;
    }

    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(() -> {
            Process process = (Process) o;

            // noinspection HardCodedStringLiteral
            fileTextField.getStyleClass().removeAll("text-field-success", "text-field-fail");
            if (process.isComplete()) {
                //noinspection HardCodedStringLiteral
                fileTextField.getStyleClass().add("text-field-success");
                fileTextField.setText("Successfully exported to '" + savePath + '\'');
            } else {
                //noinspection HardCodedStringLiteral
                fileTextField.getStyleClass().add("text-field-fail");
                fileTextField.setText((savePath == null) ?
                        ("Please choose the file the protocol structure will be " + "exported " + "to") :
                        ("Could not export to '" + savePath + '\''));
            }

            navigation.setCancelable(!process.isComplete(), this);
            navigation.setFinishable(process.isComplete(), this);
        });
    }

    @FXML
    private void browse() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file == null) {
            return;
        }
        savePath = file.toPath().toAbsolutePath();
        //noinspection HardCodedStringLiteral
        if (!savePath.toString().endsWith(".xml")) {
            //noinspection HardCodedStringLiteral
            savePath = savePath.getParent().resolve(savePath.getFileName() + ".xml");
        }
        Model.INSTANCE.getExportProcess().exportProtocolStructure(savePath);
    }

    @Override
    public void initProcess() {
        Model.INSTANCE.getExportProcess().init(Model.INSTANCE.getLearnProcess().getProtocolStructure());
    }
}
