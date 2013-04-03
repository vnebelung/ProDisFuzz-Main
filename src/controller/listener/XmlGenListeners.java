/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package controller.listener;

import model.Model;
import model.process.XMLGenP;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import view.View;
import view.composite.XMLGenWindowCo;

/**
 * The Class XmlGenListeners encapsulates all listeners for the XML generation
 * process class.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class XmlGenListeners extends WindowListeners {

    /**
     * The corresponding view component.
     */
    private final XMLGenWindowCo viewComponent;

    /**
     * The corresponding model component.
     */
    private final XMLGenP modelComponent;

    /**
     * Instantiates a new collect listeners.
     *
     * @param view  the view
     * @param model the model
     */
    public XmlGenListeners(final View view, final Model model) {
        super(view, model);
        viewComponent = view.getWindow().getXmlGenWindow();
        modelComponent = model.getXmlGenProcess();
    }

    /**
     * Adds the listeners.
     */
    public void addListeners() {
        super.addListeners(viewComponent, null);
        viewComponent.addListenerToStartStopButton(createStartStopAdapter());
        viewComponent.addListenerToFileText(createFileAdapter());
    }

    /**
     * Stores the path of the file for the XML output.
     *
     * @return the modify listener
     */
    private ModifyListener createFileAdapter() {
        return new ModifyListener() {
            public void modifyText(final ModifyEvent event) {
                model.getXmlGenProcess().setOutputPath(
                        viewComponent.getFileText());
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
