/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package controller.listener;

import model.Model;
import model.process.LoadXMLP;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import view.View;
import view.composite.LoadXMLWindowCo;

/**
 * The Class XmlGenListeners encapsulates all listeners for loading the XML
 * file.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class LoadXmlListeners extends WindowListeners {

    /**
     * The corresponding view component.
     */
    private final LoadXMLWindowCo viewComponent;

    /**
     * The corresponding model component.
     */
    private final LoadXMLP modelComponent;

    /**
     * The options listener.
     */
    private OptionsListeners optionsListeners;

    /**
     * Instantiates a new collect listeners.
     *
     * @param view  the view
     * @param model the model
     */
    public LoadXmlListeners(final View view, final Model model) {
        super(view, model);
        viewComponent = view.getWindow().getLoadXmlWindow();
        modelComponent = model.getLoadXmlProcess();
    }

    /**
     * Adds the listeners.
     */
    public void addListeners() {
        super.addListeners(viewComponent, createNextAdapter());
        viewComponent.addListenerToStartStopButton(createStartStopAdapter());
        viewComponent.addListenerToFileText(createFileAdapter());
    }

    /**
     * Stores the path of the file for the XML to load.
     *
     * @return the modify listener
     */
    private ModifyListener createFileAdapter() {
        return new ModifyListener() {
            public void modifyText(final ModifyEvent event) {
                modelComponent.setFile(
                        viewComponent.getFileText());
            }
        };
    }

    /**
     * Starts oder stops the thread responsible for loading the XML file.
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
     * Collects all relevant variables and proceeds to the next window.
     *
     * @return Selection adapter
     */
    private SelectionAdapter createNextAdapter() {
        return new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                model.getOptionsProcess().initParts(
                        modelComponent.getParts());
                optionsListeners.addDynamicListeners();
                view.getWindow().showOptionsWindow();
            }
        };
    }

    /**
     * Adds a dynamic listener.
     *
     * @param optionsListeners the options listener
     */
    public void addOptionsListeners(final OptionsListeners optionsListeners) {
        this.optionsListeners = optionsListeners;
    }
}
