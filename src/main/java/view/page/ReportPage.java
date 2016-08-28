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
import javafx.stage.DirectoryChooser;
import model.Model;
import model.process.report.Process;
import view.window.FxmlConnection;
import view.window.Navigation;

import java.io.File;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Observable;
import java.util.Observer;

/**
 * This class is the JavaFX based report page, responsible for visualizing the process of generating the report of the
 * fuzz-testing.
 */
public class ReportPage extends VBox implements Observer, Page {

    private final Navigation navigation;
    @FXML
    private TextField fileTextField;
    private Path savePath;

    /**
     * Constructs a new report page.
     *
     * @param navigation the navigation controls
     */
    public ReportPage(Navigation navigation) {
        super();
        //noinspection HardCodedStringLiteral
        FxmlConnection.connect(getClass().getResource("/fxml/reportPage.fxml"), this);
        Model.INSTANCE.getReportProcess().addObserver(this);
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
                fileTextField.setText("Successfully exported to '" + savePath + '\'');
            } else {
                //noinspection HardCodedStringLiteral
                fileTextField.getStyleClass().add("text-field-fail");
                fileTextField.setText((savePath == null) ?
                        ("Please choose the file the protocol structure will be " + "exported to") :
                        ("Could not export to '" + savePath + '\''));
            }

            navigation.setCancelable(!process.isComplete(), this);
            navigation.setFinishable(process.isComplete(), this);
        });
    }

    @FXML
    private void browse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(getScene().getWindow());
        if (file == null) {
            return;
        }
        savePath = file.toPath().toAbsolutePath();
        Model.INSTANCE.getReportProcess().save(Model.INSTANCE.getFuzzingProcess().getRecordings(),
                Duration.between(Model.INSTANCE.getFuzzingProcess().getStartTime(),
                        Model.INSTANCE.getFuzzingProcess().getEndTime()),
                Model.INSTANCE.getFuzzOptionsProcess().getTarget(),
                Model.INSTANCE.getFuzzOptionsProcess().getInterval(),
                Model.INSTANCE.getFuzzOptionsProcess().getInjectedProtocolStructure(),
                Model.INSTANCE.getFuzzingProcess().getWorkDone(), Model.INSTANCE.getFuzzingProcess().getTotalWork(),
                Model.INSTANCE.getFuzzOptionsProcess().getRecordingMethod(),
                Model.INSTANCE.getFuzzOptionsProcess().getTimeout(), savePath);
    }

    @Override
    public void initProcess() {
    }
}
