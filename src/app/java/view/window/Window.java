/*
 * This file is part of ProDisFuzz, modified on 13.03.14 20:16.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
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
import view.controls.control.ControlBar;
import view.page.Page;
import view.page.collect.CollectPage;
import view.page.export.ExportPage;
import view.page.fuzzOptions.FuzzOptionsPage;
import view.page.fuzzing.FuzzingPage;
import view.page.import_.ImportPage;
import view.page.learn.LearnPage;
import view.page.operationMode.OperationMode;
import view.page.report.ReportPage;

public class Window extends VBox implements NavigationControl, Navigation {

    @FXML
    private ScrollPane mainScrollPane;
    @FXML
    private ControlBar controlArea;
    private final OperationMode operationMode;
    private Pages pages;

    /**
     * Instantiates a new window area responsible for displaying various pages.
     */
    public Window() {
        super();
        FxmlConnection.connect(getClass().getResource("/fxml/window.fxml"), this);
        controlArea.setNavigationControl(this);
        operationMode = new OperationMode(this);

        showOperationModePage();
    }

    /**
     * Makes the operation mode page visible.
     */
    private void showOperationModePage() {
        controlArea.setNavigationVisible(false);
        mainScrollPane.setContent(operationMode);
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
     * is existing in mainScrollPane, this page is faded out before the given node is faded in.
     *
     * @param n the page node to fade in
     */
    private void fadeInPage(Node n) {
        FadeTransition transition = new FadeTransition(Duration.millis(500), n);
        transition.setFromValue(0);
        transition.setToValue(1);
        transition.play();
    }

    /**
     * Fades out a given page node, that means decreasing its opacity from 1 to 0 in a specific time interval.
     *
     * @param n the page node to fade out
     */
    private void fadeOutPage(Node n) {
        FadeTransition transition = new FadeTransition(Duration.millis(500), n);
        transition.setFromValue(1);
        transition.setToValue(0);
        transition.play();
    }

    /**
     * Makes the first page visible. The first page is determined by the defined order of the pages.
     */
    private void firstPage() {
        fadeOutPage(mainScrollPane.getContent());
        controlArea.setNavigationVisible(true);
        mainScrollPane.setContent(pages.getFirst());
        fadeInPage(mainScrollPane.getContent());
    }

    @Override
    public void nextPage() {
        fadeOutPage(mainScrollPane.getContent());
        mainScrollPane.setContent(pages.getNext());
        Page currentPage = (Page) pages.getCurrent();
        currentPage.initProcess();
        fadeInPage(mainScrollPane.getContent());
    }

    @Override
    public void previousPage() {
        // TODO: Not used yet.
        fadeOutPage(mainScrollPane.getContent());
        controlArea.setNavigationVisible(true);
        mainScrollPane.setContent(pages.getPrevious());
        fadeInPage(mainScrollPane.getContent());
    }

    @Override
    public void resetPage() {
        fadeOutPage(mainScrollPane.getContent());
        controlArea.setNavigationVisible(false);
        mainScrollPane.setContent(operationMode);
        fadeInPage(operationMode);
    }

    /**
     * Handles all actions necessary when the parent application is about to close.
     */
    public void onClose() {
        controlArea.onClose();
    }

    @Override
    public void setCancelable(boolean b, Node n) {
        if (!n.equals(pages.getCurrent())) {
            return;
        }
        controlArea.setCancelEnabled(b);
    }

    @Override
    public void setFinishable(boolean b, Node n) {
        if (!n.equals(pages.getCurrent())) {
            return;
        }
        controlArea.setFinishEnabled(!pages.hasNext() && b);
        controlArea.setNextEnabled(pages.hasNext() && b);
    }
}
