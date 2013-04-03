/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package controller.listener;

import model.Model;
import model.process.LearnP;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import view.View;
import view.composite.LearnWindowCo;

/**
 * The Class LearnListeners encapsulates all listeners for the LearnProcess
 * class.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class LearnListeners extends WindowListeners {

    /**
     * The corresponding view component.
     */
    private final LearnWindowCo viewComponent;

    /**
     * The corresponding model component.
     */
    private final LearnP modelComponent;

    /**
     * Instantiates a new learn listeners.
     *
     * @param view  the view
     * @param model the model
     */
    public LearnListeners(final View view, final Model model) {
        super(view, model);
        viewComponent = view.getWindow().getLearnWindow();
        modelComponent = model.getLearnProcess();
    }

    /**
     * Adds the listeners.
     */
    public void addListeners() {
        super.addListeners(viewComponent, createNextAdapter());
        viewComponent.addListenerToStartStopButton(createStartStopAdapter());
    }

    /**
     * Collects all relevant variables and proceed to the next window.
     *
     * @return Selection adapter
     */
    private SelectionAdapter createNextAdapter() {
        return new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                model.getXmlGenProcess().initParts(modelComponent.getParts());
                view.getWindow().showXmlGenWindow();
            }
        };
    }

    /**
     * Starts oder stops the thread responsible for learning the protocol.
     *
     * @return Selection adapter
     */
    private SelectionAdapter createStartStopAdapter() {
        return new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                if (modelComponent.isThreadRunning()) {
                    modelComponent.stop();
                } else {
                    modelComponent.start();
                }
            }
        };
    }
}
