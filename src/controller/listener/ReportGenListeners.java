/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package controller.listener;

import model.Model;
import model.process.ReportGenP;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import view.View;
import view.composite.ReportGenWindowCo;

/**
 * The Class ReportGenListeners encapsulates all listeners for the report
 * generation process class.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ReportGenListeners extends WindowListeners {

    /**
     * The corresponding view component.
     */
    private final ReportGenWindowCo viewComponent;

    /**
     * The corresponding model component.
     */
    private final ReportGenP modelComponent;

    /**
     * Instantiates a new collect listeners.
     *
     * @param view  the view
     * @param model the model
     */
    public ReportGenListeners(final View view, final Model model) {
        super(view, model);
        viewComponent = view.getWindow().getReportGenWindow();
        modelComponent = model.getReportGenProcess();
    }

    /**
     * Adds the listeners.
     */
    public void addListeners() {
        super.addListeners(viewComponent, null);
        viewComponent.addListenerToStartStopButton(createStartStopAdapter());
        viewComponent.addListenerToDirectoryText(createDirectoryAdapter());
    }

    /**
     * Stores the directory for the report output.
     *
     * @return the modify listener
     */
    private ModifyListener createDirectoryAdapter() {
        return new ModifyListener() {
            public void modifyText(final ModifyEvent event) {
                modelComponent.setOutputPath(viewComponent.getDirectoryText());
            }
        };
    }

    /**
     * Starts oder stops the thread responsible for collecting all files.
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
