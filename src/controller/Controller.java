/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package controller;

import controller.listener.*;
import model.Model;
import view.View;

/**
 * The Class Controller implements the controller of the MVC pattern.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class Controller {

    /**
     * The model.
     */
    private final Model model;

    /**
     * The view.
     */
    private final View view;

    /**
     * The collect files listeners.
     */
    private final CollectListeners collectListeners;

    /**
     * The check files listeners.
     */
    private final CheckListeners checkListeners;

    /**
     * The learn protocol listeners.
     */
    private final LearnListeners learnListeners;

    /**
     * The XML generation listeners.
     */
    private final XmlGenListeners xmlGenListeners;

    /**
     * The XML loading listeners.
     */
    private final LoadXmlListeners loadXmlListeners;

    /**
     * The fuzzing options listeners.
     */
    private final OptionsListeners optionsListeners;

    /**
     * The fuzzing listeners.
     */
    private final FuzzingListeners fuzzingListeners;

    /**
     * The report generation listeners.
     */
    private final ReportGenListeners reportGenListeners; // NOPMD

    /**
     * Instantiates a new controller.
     *
     * @param model the model
     * @param view  the view
     */
    public Controller(final Model model, final View view) {
        this.model = model;
        this.view = view;
        collectListeners = new CollectListeners(this.view, this.model);
        checkListeners = new CheckListeners(this.view, this.model);
        learnListeners = new LearnListeners(this.view, this.model);
        xmlGenListeners = new XmlGenListeners(this.view, this.model);
        loadXmlListeners = new LoadXmlListeners(this.view, this.model);
        optionsListeners = new OptionsListeners(this.view, this.model);
        fuzzingListeners = new FuzzingListeners(this.view, this.model);
        reportGenListeners = new ReportGenListeners(this.view, this.model);
    }

    /**
     * Adds the listeners.
     */
    public void addListeners() {
        collectListeners.addListeners();
        checkListeners.addListeners();
        learnListeners.addListeners();
        xmlGenListeners.addListeners();
        loadXmlListeners.addListeners();
        optionsListeners.addListeners();
        fuzzingListeners.addListeners();
        reportGenListeners.addListeners();
        // Adds dynamic listeners, that is these listeners are hooked to SWT
        // elements which are not available from the beginning but created
        // during runtime
        addDynamicListeners();
    }

    /**
     * Adds the dynamic listeners. Dynamic listeners are listeners that are
     * hooked to SWT elements which are not available from the beginning but
     * created during runtime
     */
    private void addDynamicListeners() {
        loadXmlListeners.addOptionsListeners(optionsListeners);
    }

    /**
     * Assigns observers.
     */
    public void assignObservers() {
        model.getCollectProcess().addObserver(
                view.getWindow().getCollectWindow());
        model.getCheckProcess().addObserver(view.getWindow().getCheckWindow());
        model.getLearnProcess().addObserver(view.getWindow().getLearnWindow());
        model.getXmlGenProcess()
                .addObserver(view.getWindow().getXmlGenWindow());
        model.getLoadXmlProcess().addObserver(
                view.getWindow().getLoadXmlWindow());
        model.getOptionsProcess().addObserver(
                view.getWindow().getOptionsWindow());
        model.getFuzzingProcess().addObserver(
                view.getWindow().getFuzzingWindow());
        model.getReportGenProcess().addObserver(
                view.getWindow().getReportGenWindow());
    }

}
