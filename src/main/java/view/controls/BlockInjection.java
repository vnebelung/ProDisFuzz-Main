/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.controls;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import model.Model;
import model.protocol.InjectedProtocolBlock.DataInjection;
import view.window.FxmlConnection;

import java.io.File;

/**
 * This class is a JavaFX block injection component responsible for displaying all GUI components for one fuzzable
 * protocol block.
 */
public class BlockInjection extends GridPane {

    private final int protocolBlockIndex;

    @FXML
    private RadioButton randomRadioButton;
    @FXML
    private RadioButton libraryRadioButton;
    @FXML
    private TextField libraryTextField;
    @FXML
    private Button browseButton;

    /**
     * Constructs a new block injection module.
     *
     * @param index the index of the corresponding protocol block
     */
    public BlockInjection(int index) {
        super();
        // noinspection HardCodedStringLiteral
        FxmlConnection.connect(getClass().getResource("/fxml/blockInjection.fxml"), this);
        protocolBlockIndex = index;
    }

    /**
     * Handles the action of the radio buttons by updating the model with the chosen option for the injection method of
     * a protocol block.
     */
    @FXML
    private void injectionSource() {
        if (randomRadioButton.isSelected()) {
            Model.INSTANCE.getFuzzOptionsProcess()
                    .setInjectionDataForVarProtocolBlock(protocolBlockIndex, DataInjection.RANDOM);
        } else if (libraryRadioButton.isSelected()) {
            Model.INSTANCE.getFuzzOptionsProcess()
                    .setInjectionDataForVarProtocolBlock(protocolBlockIndex, DataInjection.LIBRARY);
        }
    }

    /**
     * Handles the action of the browse button by displaying a window where the user can choose a file that contains the
     * fuzz strings for the corresponding protocol block.
     */
    @FXML
    private void browse() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file == null) {
            return;
        }
        libraryTextField.setText(file.getAbsolutePath());
        Model.INSTANCE.getFuzzOptionsProcess().setLibraryForVarProtocolBlock(protocolBlockIndex, file.toPath());
    }

    /**
     * Updates all components of this module.
     *
     * @param dataInjection the data injection method
     * @param enabled             false if this complete module should be greyed out
     * @param isValidLibrary      true, if the library file is valid
     */
    public void update(DataInjection dataInjection, boolean enabled, boolean isValidLibrary) {
        randomRadioButton.setSelected(dataInjection == DataInjection.RANDOM);
        randomRadioButton.setDisable(!enabled);

        libraryRadioButton.setSelected(dataInjection == DataInjection.LIBRARY);
        libraryRadioButton.setDisable(!enabled);

        //noinspection HardCodedStringLiteral
        libraryTextField.getStyleClass().removeAll("text-field-success", "text-field-fail");
        if (enabled && (dataInjection == DataInjection.LIBRARY)) {
            libraryTextField.setDisable(false);
            if (isValidLibrary) {
                //noinspection HardCodedStringLiteral
                libraryTextField.getStyleClass().add("text-field-success");
            } else {
                //noinspection HardCodedStringLiteral
                libraryTextField.getStyleClass().add("text-field-fail");
                libraryTextField.setText("Please choose a valid library file");
            }
        } else {
            libraryTextField.setDisable(true);
            libraryTextField.setText("");
        }

        browseButton.setDisable(!enabled || (dataInjection != DataInjection.LIBRARY));
    }

}
