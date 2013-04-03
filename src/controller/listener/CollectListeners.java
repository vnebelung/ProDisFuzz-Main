/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package controller.listener;

import model.Model;
import model.process.CollectP;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import view.View;
import view.composite.CollectWindowCo;

/**
 * The Class CollectListeners encapsulates all listeners for the CollectProcess
 * class.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class CollectListeners extends WindowListeners { // NOPMD

    /**
     * The corresponding view component.
     */
    private final CollectWindowCo viewComponent;

    /**
     * The corresponding model component.
     */
    private final CollectP modelComponent;

    /**
     * Instantiates a new collect listeners.
     *
     * @param view  the view
     * @param model the model
     */
    public CollectListeners(final View view, final Model model) {
        super(view, model);
        viewComponent = view.getWindow().getCollectWindow();
        modelComponent = model.getCollectProcess();
    }

    /**
     * Adds the listeners.
     */
    public void addListeners() {
        super.addListeners(viewComponent, createNextAdapter());
        viewComponent.addListenerToStartStopButton(createStartStopAdapter());
        viewComponent.addListenerToDirectoryText(createDirectoryAdapter());
        viewComponent.addListenerToResetButton(createResetAdapter());
    }

    /**
     * Resets the list of collected files back to an empty list.
     *
     * @return the modify listener
     */
    private SelectionAdapter createResetAdapter() {
        return new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                modelComponent.reset();
            }
        };
    }

    /**
     * Stores the path of the directory for monitoring.
     *
     * @return the modify listener
     */
    private ModifyListener createDirectoryAdapter() {
        return new ModifyListener() {
            public void modifyText(final ModifyEvent event) {
                modelComponent.setDirectory(viewComponent.getDirectoryText());
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

    /**
     * Collects all relevant variables and proceed to the next window.
     *
     * @return Selection adapter
     */
    private SelectionAdapter createNextAdapter() {
        return new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                model.getCheckProcess()
                        .initFiles(modelComponent.getFilePaths());
                view.getWindow().showCheckWindow();
            }
        };
    }

}
