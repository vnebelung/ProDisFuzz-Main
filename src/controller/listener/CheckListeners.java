/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package controller.listener;

import model.Model;
import model.process.CheckP;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import view.View;
import view.composite.CheckWindowCo;

/**
 * The Class CheckListeners encapsulates all listeners for the CheckProcess
 * class.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class CheckListeners extends WindowListeners {

    /**
     * The corresponding view component.
     */
    private final CheckWindowCo viewComponent;

    /**
     * The corresponding model component.
     */
    private final CheckP modelComponent;

    /**
     * Instantiates a new collect listeners.
     *
     * @param view  the view
     * @param model the model
     */
    public CheckListeners(final View view, final Model model) {
        super(view, model);
        viewComponent = view.getWindow().getCheckWindow();
        modelComponent = model.getCheckProcess();
    }

    /**
     * Adds the listeners.
     */
    public void addListeners() {
        super.addListeners(viewComponent, createNextAdapter());
        viewComponent.addListenerToTable(createFileTableAdapter());
    }

    /**
     * Collects all relevant variables and proceed to the next window.
     *
     * @return Selection adapter
     */
    private SelectionAdapter createNextAdapter() {
        return new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                model.getLearnProcess()
                        .cleanFileList(modelComponent.getFiles());
                view.getWindow().showLearnWindow();
            }
        };
    }

    /**
     * Detects a check in the table and sets the checked status of the items.
     *
     * @return the selection adapter
     */
    private SelectionAdapter createFileTableAdapter() {
        return new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                // If event is a click on the checkbox
                if (event.detail == SWT.CHECK) {
                    boolean[] isChecked = new boolean[viewComponent
                            .getFileTable().getItemCount()];
                    // Set the check state for every table item according to the
                    // state of the particular SWT checkbox
                    for (int i = 0; i < viewComponent.getFileTable()
                            .getItemCount(); i++) {
                        isChecked[i] = viewComponent.getFileTable().getItem(i)
                                .getChecked();
                    }
                    modelComponent.setChecked(isChecked);
                }
            }
        };
    }
}
