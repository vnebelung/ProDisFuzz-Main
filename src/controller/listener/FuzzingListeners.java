/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package controller.listener;

import model.Model;
import model.RunnableThread.RunnableState;
import model.process.FuzzingP;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import view.View;
import view.composite.FuzzingWindowCo;

/**
 * The Class FuzzingListeners encapsulates all listeners for the fuzzing
 * process.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class FuzzingListeners extends WindowListeners {

    /**
     * The corresponding view component.
     */
    private final FuzzingWindowCo viewComponent;

    /**
     * The corresponding model component.
     */
    private final FuzzingP modelComponent;

    /**
     * Instantiates a new fuzzing listeners.
     *
     * @param view  the view
     * @param model the model
     */
    public FuzzingListeners(final View view, final Model model) {
        super(view, model);
        viewComponent = view.getWindow().getFuzzingWindow();
        modelComponent = model.getFuzzingProcess();
    }

    /**
     * Adds the listeners.
     */
    public void addListeners() {
        super.addListeners(viewComponent, createNextAdapter());
        viewComponent.addListenerToStartStopButton(createStartStopAdapter());
    }

    /**
     * Starts oder stops the thread responsible for fuzzing.
     *
     * @return Selection adapter
     */
    private SelectionAdapter createStartStopAdapter() {
        return new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                if (modelComponent.getThreadState() == RunnableState.FINISHED
                        || modelComponent.getThreadState() == RunnableState.CANCELED) {
                    modelComponent.start();
                } else {
                    modelComponent.stop();
                }
            }
        };
    }

    /**
     * Collects all relevant variables and proceed to the next window.
     *
     * @return Selection adapter
     */
    private SelectionAdapter createNextAdapter() {
        return new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                // Copies all necessary variables to the report generation
                // process
                model.getReportGenProcess().initResults(
                        modelComponent.getRecordFiles(),
                        modelComponent.getDuration(),
                        modelComponent.getDestination(),
                        modelComponent.getInterval(),
                        modelComponent.getParts(),
                        modelComponent.getLastProgress(),
                        modelComponent.getTotalProgress(),
                        modelComponent.isSaveCommunication(),
                        modelComponent.getTimeout());
                view.getWindow().showReportGenWindow();
            }
        };
    }

}
