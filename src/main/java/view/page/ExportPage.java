/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.Model;
import model.process.export.ExportProcess;
import view.window.FxmlConnection;
import view.window.Navigation;

import java.io.File;
import java.nio.file.Path;
import java.util.Observable;
import java.util.Observer;

public class ExportPage extends VBox implements Observer, Page {

    private final Navigation navigation;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    @FXML
    private TextField fileTextField;
    @SuppressWarnings("InstanceVariableMayNotBeInitialized")
    private Path savePath;

    /**
     * Instantiates a new export area responsible for visualizing the process of exporting the protocol structure to
     * XML.
     *
     * @param navigation the navigation controls
     */
    public ExportPage(Navigation navigation) {
        super();
        //noinspection HardcodedFileSeparator,ThisEscapedInObjectConstruction,HardCodedStringLiteral
        FxmlConnection.connect(getClass().getResource("/fxml/exportPage.fxml"), this);
        //noinspection ThisEscapedInObjectConstruction
        Model.INSTANCE.getExportProcess().addObserver(this);
        this.navigation = navigation;
    }

    @Override
    public void update(Observable o, Object arg) {
        ExportProcess process = (ExportProcess) o;

        // noinspection HardCodedStringLiteral
        fileTextField.getStyleClass().removeAll("text-field-success", "text-field-fail");
        if (process.isExported()) {
            //noinspection HardCodedStringLiteral
            fileTextField.getStyleClass().add("text-field-success");
            fileTextField.setText("Successfully exported to '" + savePath + '\'');
        } else {
            //noinspection HardCodedStringLiteral
            fileTextField.getStyleClass().add("text-field-fail");
            fileTextField.setText((savePath == null) ? ("Please choose the file the protocol structure will be " +
                    "exported " + "to") : ("Could not export to '" + savePath + '\''));
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
        //noinspection HardCodedStringLiteral
        if (!savePath.toString().endsWith(".xml")) {
            //noinspection HardCodedStringLiteral
            savePath = savePath.getParent().resolve(savePath.getFileName() + ".xml");
        }
        Model.INSTANCE.getExportProcess().exportXML(savePath);
    }

    @Override
    public void initProcess() {
        Model.INSTANCE.getExportProcess().init(Model.INSTANCE.getLearnProcess().getProtocolStructure());
    }
}
