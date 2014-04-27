/*
 * This file is part of ProDisFuzz, modified on 13.03.14 22:09.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.page.collect;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import model.Model;
import model.ProtocolFile;
import model.process.collect.CollectProcess;
import view.controls.table.NumericTableCell;
import view.page.Page;
import view.window.FxmlConnection;
import view.window.Navigation;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class CollectPage extends VBox implements Observer, Page {

    @FXML
    private TextField folderTextField;
    @FXML
    private TableView<TableRow> filesTableView;
    @FXML
    private TableColumn<TableRow, Boolean> columnUse;
    @FXML
    private TableColumn<TableRow, String> columnFileName;
    @FXML
    private TableColumn<TableRow, String> columnLastModified;
    @FXML
    private TableColumn columnSize;
    @FXML
    private TableColumn<TableRow, String> columnSHA256;
    private int statusHash;
    private final Navigation navigation;


    /**
     * Instantiates a new collect area responsible for visualizing the process of collecting sequence files.
     *
     * @param navigation the navigation controls
     */
    public CollectPage(Navigation navigation) {
        super();
        FxmlConnection.connect(getClass().getResource("/fxml/collectPage.fxml"), this);
        Model.INSTANCE.getCollectProcess().addObserver(this);
        this.navigation = navigation;

        filesTableView.setEditable(true);
        columnUse.setCellValueFactory(new PropertyValueFactory<>("selected"));
        columnUse.setCellFactory(CheckBoxTableCell.forTableColumn(columnUse));
        columnUse.setEditable(true);
        columnUse.prefWidthProperty().bind(filesTableView.widthProperty().multiply(0.04));
        columnFileName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnFileName.prefWidthProperty().bind(filesTableView.widthProperty().multiply(0.22));
        columnSize.setCellValueFactory(new PropertyValueFactory<TableRow, String>("size"));
        columnSize.prefWidthProperty().bind(filesTableView.widthProperty().multiply(0.09));
        columnSize.setCellFactory(p -> new NumericTableCell());
        columnLastModified.setCellValueFactory(new PropertyValueFactory<>("lastModified"));
        columnLastModified.prefWidthProperty().bind(filesTableView.widthProperty().multiply(0.12));
        columnSHA256.setCellValueFactory(new PropertyValueFactory<>("sha256"));
        columnSHA256.prefWidthProperty().bind(filesTableView.widthProperty().multiply(0.51));
    }

    /**
     * Handles the action of the browse button and displays a window where the user can choose a directory that stores
     * the protocol files.
     */
    @FXML
    private void browse() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        File folder = dirChooser.showDialog(getScene().getWindow());
        if (folder == null) {
            return;
        }
        folderTextField.setText(folder.getAbsolutePath());
        Model.INSTANCE.getCollectProcess().setFolder(folder.getAbsolutePath());
    }

    @Override
    public void update(Observable o, Object arg) {
        CollectProcess process = (CollectProcess) o;

        int newStatusHash = calcStatusHash(process);
        if (newStatusHash != statusHash) {
            statusHash = newStatusHash;
            filesTableView.getItems().clear();
            for (ProtocolFile each : process.getFiles()) {
                filesTableView.getItems().add(new TableRow(process.isSelected(each.getName()), each.getName(),
                        each.getSize(), each.getLastModified(), each.getSha256()));
            }
        }

        folderTextField.getStyleClass().removeAll("text-field-success", "text-field-fail");
        if (process.isFolderValid()) {
            folderTextField.getStyleClass().add("text-field-success");
        } else {
            folderTextField.getStyleClass().add("text-field-fail");
            folderTextField.setText("Please select a valid folder that contains the protocol recordings");
        }

        filesTableView.setVisible(process.isFolderValid());

        navigation.setCancelable(true, this);
        navigation.setFinishable(process.getNumOfSelectedFiles() >= 2, this);
    }

    /**
     * Calculates a hash code of the current folder path and all file names in this folder.
     *
     * @param p the collect process containing the files
     * @return the hash code
     */
    private int calcStatusHash(CollectProcess p) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(folderTextField.getText());
        for (ProtocolFile each : p.getFiles()) {
            stringBuilder.append(each.getName());
        }
        return stringBuilder.toString().hashCode();
    }

    @Override
    public void initProcess() {
        Model.INSTANCE.getCollectProcess().init();
    }
}
