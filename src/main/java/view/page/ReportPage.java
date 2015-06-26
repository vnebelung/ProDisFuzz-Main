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
import javafx.stage.DirectoryChooser;
import model.Model;
import model.process.report.ReportProcess;
import view.window.FxmlConnection;
import view.window.Navigation;

import java.io.File;
import java.nio.file.Path;
import java.util.Observable;
import java.util.Observer;

public class ReportPage extends VBox implements Observer, Page {

    private final Navigation navigation;
    @FXML
    private TextField fileTextField;
    private Path savePath;

    /**
     * Instantiates a new report page responsible for visualizing the process of generating the report of the
     * fuzz-testing.
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
        ReportProcess process = (ReportProcess) o;

        //noinspection HardCodedStringLiteral
        fileTextField.getStyleClass().removeAll("text-field-success", "text-field-fail");
        if (process.isWritten()) {
            //noinspection HardCodedStringLiteral
            fileTextField.getStyleClass().add("text-field-success");
            fileTextField.setText("Successfully exported to '" + savePath + '\'');
        } else {
            //noinspection HardCodedStringLiteral
            fileTextField.getStyleClass().add("text-field-fail");
            fileTextField.setText((savePath == null) ? ("Please choose the file the protocol structure will be " +
                    "exported to") : ("Could not export to '" + savePath + '\''));
        }

        navigation.setCancelable(!process.isWritten(), this);
        navigation.setFinishable(process.isWritten(), this);
    }

    @FXML
    private void browse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(getScene().getWindow());
        if (file == null) {
            return;
        }
        savePath = file.toPath().toAbsolutePath();
        Model.INSTANCE.getReportProcess().write(savePath);
    }

    @Override
    public void initProcess() {
        Model.INSTANCE.getReportProcess().init(Model.INSTANCE.getFuzzingProcess().getRecordings(), Model.INSTANCE
                .getFuzzingProcess().getDuration(), Model.INSTANCE.getFuzzOptionsProcess().getTarget(), Model
                .INSTANCE.getFuzzOptionsProcess().getInterval(), Model.INSTANCE.getFuzzOptionsProcess()
                .getInjectedProtocolStructure(), Model.INSTANCE.getFuzzingProcess().getWorkProgress(), Model.INSTANCE
                .getFuzzingProcess().getWorkTotal(), Model.INSTANCE.getFuzzOptionsProcess().getSaveCommunication(),
                Model.INSTANCE.getFuzzOptionsProcess().getTimeout());
    }
}
