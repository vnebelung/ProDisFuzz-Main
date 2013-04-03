/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package controller.listener;

import model.Model;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import view.View;
import view.composite.WindowCo;

/**
 * The Class WindowListeners encapsulates all listeners for the navigation
 * buttons.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class WindowListeners {

    /**
     * The view.
     */
    protected final View view;

    /**
     * The model.
     */
    protected final Model model;

    /**
     * Instantiates the window listeners.
     *
     * @param view  the view
     * @param model the model
     */
    public WindowListeners(final View view, final Model model) {
        this.view = view;
        this.model = model;
    }

    /**
     * Performs a cancel and redirects to the start window.
     *
     * @return Selection Adapter
     */
    protected SelectionAdapter createCancelAdapter() {
        return new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                model.reset();
                view.getWindow().showStartWindow();
            }
        };
    }

    /**
     * Performs the last finish step and redirects to the start window.
     *
     * @return Selection Adapter
     */
    protected SelectionAdapter createFinishAdapter() {
        return new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                model.reset();
                view.getWindow().showStartWindow();
            }
        };
    }

    /**
     * Adds the listeners.
     *
     * @param window      the target window
     * @param nextAdapter the adapter for the listener of the next button
     */
    public void addListeners(final WindowCo window,
                             final SelectionAdapter nextAdapter) {
        window.addListenerToCancelButton(createCancelAdapter());
        window.addListenerToFinishButton(createFinishAdapter());
        if (nextAdapter != null) {
            window.addListenerToNextButton(nextAdapter);
        }
    }

}