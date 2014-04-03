/*
 * This file is part of ProDisFuzz, modified on 03.04.14 20:36.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.controls.blockinjection;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import model.Model;
import model.protocol.InjectedProtocolBlock;
import view.window.FxmlConnection;

import java.io.File;

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
     * Instantiates a new block injection module responsible for displaying all GUI components for one fuzzable protocol
     * block.
     *
     * @param index the index of the corresponding protocol block
     */
    public BlockInjection(int index) {
        super();
        FxmlConnection.connect(getClass().getResource("blockInjection.fxml"), this);
        protocolBlockIndex = index;
    }

    /**
     * Handles the action of the radio buttons by updating the model with the chosen option for the injection method of
     * a protocol block.
     */
    @FXML
    private void injectionSource() {
        if (randomRadioButton.isSelected()) {
            Model.INSTANCE.getFuzzOptionsProcess().setRandomInjection(protocolBlockIndex);
        } else if (libraryRadioButton.isSelected()) {
            Model.INSTANCE.getFuzzOptionsProcess().setLibraryInjection(protocolBlockIndex);
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
        Model.INSTANCE.getFuzzOptionsProcess().setLibrary(file.toPath(), protocolBlockIndex);
    }

    /**
     * Updates all components of this module.
     *
     * @param dataInjectionMethod the data injection method
     * @param enabled             false if this complete module should be greyed out
     * @param isValidLibrary      true, if the library file is valid
     */
    public void update(InjectedProtocolBlock.DataInjectionMethod dataInjectionMethod, boolean enabled,
                       boolean isValidLibrary) {
        randomRadioButton.setSelected(dataInjectionMethod == InjectedProtocolBlock.DataInjectionMethod.RANDOM);
        randomRadioButton.setDisable(!enabled);

        libraryRadioButton.setSelected(dataInjectionMethod == InjectedProtocolBlock.DataInjectionMethod.LIBRARY);
        libraryRadioButton.setDisable(!enabled);

        libraryTextField.getStyleClass().removeAll("text-field-success", "text-field-fail");
        if (enabled && dataInjectionMethod == InjectedProtocolBlock.DataInjectionMethod.LIBRARY) {
            libraryTextField.setDisable(false);
            if (isValidLibrary) {
                libraryTextField.getStyleClass().add("text-field-success");
            } else {
                libraryTextField.getStyleClass().add("text-field-fail");
                libraryTextField.setText("Please choose a valid library file");
            }
        } else {
            libraryTextField.setDisable(true);
            libraryTextField.setText("");
        }

        browseButton.setDisable(!enabled || dataInjectionMethod != InjectedProtocolBlock.DataInjectionMethod.LIBRARY);
    }

}
