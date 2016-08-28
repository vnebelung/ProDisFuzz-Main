/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package view.window;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Model;
import view.controls.ControlBar;
import view.page.*;

/**
 * This class is the JavaFx based window, responsible for displaying various pages.
 */
public class Window extends VBox implements NavigationControl, Navigation {

    private final OperationMode operationMode;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ControlBar controlBar;
    private Pages pages;

    /**
     * Constructs a new window.
     */
    public Window() {
        super();
        //noinspection HardCodedStringLiteral
        FxmlConnection.connect(getClass().getResource("/fxml/window.fxml"), this);
        controlBar.setNavigationControl(this);
        operationMode = new OperationMode(this);
        showOperationModePage();
    }

    /**
     * Makes the operation mode page visible.
     */
    private void showOperationModePage() {
        controlBar.setNavigationVisible(false);
        scrollPane.setContent(operationMode);
        fadeInPage(operationMode);
    }

    @Override
    public void enterLearnMode() {
        pages = new Pages();

        CollectPage collectPage = new CollectPage(this);
        pages.add(collectPage);

        LearnPage learnPage = new LearnPage(this);
        pages.add(learnPage);

        ExportPage exportPage = new ExportPage(this);
        pages.add(exportPage);

        Model.INSTANCE.reset();

        firstPage();
    }

    @Override
    public void enterFuzzMode() {
        pages = new Pages();

        ImportPage importPage = new ImportPage(this);
        pages.add(importPage);
        // TODO: Remove comment
        //        MonitorPage monitorPage = new MonitorPage(this);
        //        pages.add(monitorPage);

        FuzzOptionsPage fuzzOptionsPage = new FuzzOptionsPage(this);
        pages.add(fuzzOptionsPage);

        FuzzingPage fuzzingPage = new FuzzingPage(this);
        pages.add(fuzzingPage);

        ReportPage reportPage = new ReportPage(this);
        pages.add(reportPage);

        Model.INSTANCE.reset();

        firstPage();
    }

    /**
     * Fades in a given page node, that means increasing its opacity from 0 to 1 in a specific time interval. If a page
     * is existing in scrollPane, this page is faded out before the given node is faded in.
     *
     * @param node the page node to fade in
     */
    private static void fadeInPage(Node node) {
        FadeTransition transition = new FadeTransition(Duration.millis(500), node);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.play();
    }

    /**
     * Fades out a given page node, that means decreasing its opacity from 1 to 0 in a specific time interval.
     *
     * @param node the page node to fade out
     */
    private static void fadeOutPage(Node node) {
        FadeTransition transition = new FadeTransition(Duration.millis(500), node);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.play();
    }

    /**
     * Makes the first page visible. The first page is determined by the defined order of the pages.
     */
    private void firstPage() {
        fadeOutPage(scrollPane.getContent());
        controlBar.setNavigationVisible(true);
        scrollPane.setContent(pages.getFirst());
        fadeInPage(scrollPane.getContent());
    }

    @Override
    public void nextPage() {
        fadeOutPage(scrollPane.getContent());
        scrollPane.setContent(pages.getNext());
        Page currentPage = (Page) pages.getCurrent();
        currentPage.initProcess();
        fadeInPage(scrollPane.getContent());
    }

    @Override
    public void previousPage() {
        // TODO: Not used yet.
        fadeOutPage(scrollPane.getContent());
        controlBar.setNavigationVisible(true);
        scrollPane.setContent(pages.getPrevious());
        fadeInPage(scrollPane.getContent());
    }

    @Override
    public void resetPage() {
        fadeOutPage(scrollPane.getContent());
        showOperationModePage();
    }

    /**
     * Handles all actions necessary when the parent application is about to close.
     */
    public void onClose() {
        controlBar.onClose();
    }

    @Override
    public void setCancelable(boolean enabled, Node node) {
        if (!node.equals(pages.getCurrent())) {
            return;
        }
        controlBar.setCancelEnabled(enabled);
    }

    @Override
    public void setFinishable(boolean enabled, Node node) {
        if (!node.equals(pages.getCurrent())) {
            return;
        }
        controlBar.setFinishEnabled(!pages.hasNext() && enabled);
        controlBar.setNextEnabled(pages.hasNext() && enabled);
    }
}
