/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package controller.listener;

import model.Model;
import model.process.OptionsP;
import model.process.OptionsP.Mode;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import view.View;
import view.composite.OptionsWindowCo;

/**
 * The Class XmlGenListeners encapsulates all listeners for loading the XML
 * file.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class OptionsListeners extends WindowListeners { // NOPMD

    /**
     * The corresponding view component.
     */
    private final OptionsWindowCo viewComponent;

    /**
     * The corresponding model component.
     */
    private final OptionsP modelComponent;

    /**
     * The timeout for the timer.
     */
    private static final int TIMEOUT = 2000;

    /**
     * Instantiates a new options listeners.
     *
     * @param view  the view
     * @param model the model
     */
    public OptionsListeners(final View view, final Model model) {
        super(view, model);
        viewComponent = view.getWindow().getOptionsWindow();
        modelComponent = model.getOptionsProcess();
    }

    /**
     * Adds the listeners.
     */
    public void addListeners() {
        super.addListeners(viewComponent, createNextAdapter());
        viewComponent.addListenerToModeButtons(createModeAdapter());
        final ModifyListener destListener = createDestListener();
        viewComponent.addListenerToDestAddressText(destListener);
        viewComponent.addListenerToDestPortSpinner(destListener);
        viewComponent.addListenerToIntervalSpinner(createIntervalListener());
        viewComponent
                .addListenerToCommunicationButton(createCommunicationAdapter());
        viewComponent.addListenerToTimeoutSpinner(createTimeoutListener());
    }

    /**
     * Adds dynamic listeners which can not be added at the beginning like other
     * listeners because they refer elements which are created during the
     * options process.
     */
    public void addDynamicListeners() {
        viewComponent
                .addListenerToDataCompositeButtons(createDataButtonsAdapter());
        viewComponent.addListenerToLibraryTexts(createLibraryTextListener());
    }

    /**
     * Sets the fuzzing mode to the data field of the actual radio button
     *
     * @return Selection adapter
     */
    private SelectionAdapter createModeAdapter() {
        return new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                modelComponent.setMode((Mode) event.widget.getData());
            }
        };
    }

    /**
     * Sets the fuzzing type for a protocol part to the selection of the
     * corresponding button
     *
     * @return Selection adapter
     */
    private SelectionAdapter createDataButtonsAdapter() {
        return new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                final int index = (Integer) event.widget.getData();
                modelComponent.setDataMode(viewComponent.getDataComposites()
                        .get(index).getMode(), index);
            }
        };
    }

    /**
     * Sets the destination server address and port.
     *
     * @return Modify listener
     */
    private ModifyListener createDestListener() {
        return new ModifyListener() {
            public void modifyText(final ModifyEvent event) {
                // Only if there was no change on the input for TIMEOUT ms the
                // runnable for changing the model is executed
                viewComponent.getDisplay().timerExec(TIMEOUT, new Runnable() {
                    @Override
                    public void run() {
                        modelComponent.setDestination(
                                viewComponent.getDestAddressText(),
                                viewComponent.getDestPortNumber());
                    }
                });
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
                model.getFuzzingProcess().initParts(modelComponent.getParts());
                model.getFuzzingProcess().initOptions(
                        modelComponent.getDestination(),
                        modelComponent.getMode(), modelComponent.getInterval(),
                        modelComponent.isSaveCommunication(),
                        modelComponent.getTimeout());
                view.getWindow().showFuzzingWindow();
            }
        };
    }

    /**
     * Sets the fuzzing interval to the value of the spinner.
     *
     * @return Modify listener
     */
    private ModifyListener createIntervalListener() {
        return new ModifyListener() {
            public void modifyText(final ModifyEvent event) {
                modelComponent.setInterval(viewComponent.getInterval());
            }
        };
    }

    /**
     * Sets the connection timeout to the value of the spinner.
     *
     * @return Modify listener
     */
    private ModifyListener createTimeoutListener() {
        return new ModifyListener() {
            public void modifyText(final ModifyEvent event) {
                modelComponent.setTimeout(viewComponent.getTimeout());
            }
        };
    }

    /**
     * Sets the library path of a protocol file to the value of the
     * corresponding text field.
     *
     * @return Modify listener
     */
    private ModifyListener createLibraryTextListener() {
        return new ModifyListener() {
            public void modifyText(final ModifyEvent event) {
                // The index (0-based) of the text field that was modified
                final int index = (Integer) event.widget.getData();
                modelComponent.setLibraryPath(viewComponent.getDataComposites()
                        .get(index).libraryText.getText(), index);
            }
        };
    }

    /**
     * Determines whether all fuzzing communication shall be saved to files
     *
     * @return Selection adapter
     */
    private SelectionAdapter createCommunicationAdapter() {
        return new SelectionAdapter() {
            public void widgetSelected(final SelectionEvent event) {
                modelComponent.setSaveCommunication(viewComponent
                        .isCommunicationSelection());
            }
        };
    }

}