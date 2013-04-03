/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view.composite;

import model.process.OptionsP;
import model.process.OptionsP.Mode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import view.ImageRepository;
import view.ProtocolCanvas;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * The Class OptionsWindowCo displays all widgets which are necessary to select
 * all fuzzing options.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class OptionsWindowCo extends WindowCo implements Observer { // NOPMD

    /**
     * The data composite.
     */
    private final List<DataCo> dataComposites;

    /**
     * The separate button.
     */
    private final Button separateButton;

    /**
     * The simultaneous button.
     */
    private final Button simultaneousButton; // NOPMD

    /**
     * The destination address text.
     */
    private final Text destAddressText;

    /**
     * The destination port spinner.
     */
    private final Spinner destPortSpinner;

    /**
     * The timeout spinner.
     */
    private final Spinner timeoutSpinner;

    /**
     * The interval spinner.
     */
    private final Spinner intervalSpinner;

    /**
     * The destination icon label.
     */
    private final Label destIconLabel;

    /**
     * The destination info label.
     */
    private final Label destInfoLabel;

    /**
     * The communication button.
     */
    private final Button comButton;

    /**
     * The protocol canvas.
     */
    private final ProtocolCanvas protocolCanvas;

    /**
     * The data container composite.
     */
    private final Composite dataContainerComposite; // NOPMD

    /**
     * The data scrolled composite.
     */
    private final ScrolledComposite dataScrolledComposite; // NOPMD

    /**
     * Instantiates a new options window composite.
     *
     * @param parent    the parent
     * @param style     the style
     * @param page      the page number
     * @param totalPage the total page number
     */
    public OptionsWindowCo(final Composite parent, final int style,
                           final int page, final int totalPage) {
        super(parent, style, page, totalPage);

        this.setLayout(new GridLayout(1, false));

        final Group destGroup = new Group(this, SWT.NONE);
        destGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        destGroup.setLayout(new GridLayout(4, false));
        destGroup.setText("Destination");

        final Label destAddressLabel = new Label(destGroup, SWT.NONE);
        destAddressLabel.setText("Target:");

        destAddressText = new Text(destGroup, SWT.BORDER);
        destAddressText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false));

        final Label destPortLabel = new Label(destGroup, SWT.NONE);
        destPortLabel.setText("Port:");

        destPortSpinner = new Spinner(destGroup, SWT.BORDER);
        destPortSpinner.setIncrement(1);
        destPortSpinner.setPageIncrement(50);
        destPortSpinner.setMinimum(OptionsP.PORT_MIN);
        destPortSpinner.setMaximum(OptionsP.PORT_MAX);

        @SuppressWarnings("unused")
        final Label emptyLabel1 = new Label(destGroup, SWT.NONE);

        final Composite destComposite = new Composite(destGroup, SWT.NONE);
        destComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
                false, 3, 1));
        destComposite.setLayout(new GridLayout(2, false));

        destIconLabel = new Label(destComposite, SWT.NONE);
        destIconLabel.setLayoutData(new GridData(SWT.RIGHT));

        destInfoLabel = new Label(destComposite, SWT.NONE);
        destInfoLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
                false));

        final Group optionsGroup = new Group(this, SWT.NONE);
        optionsGroup
                .setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        optionsGroup.setLayout(new GridLayout(2, false));
        optionsGroup.setText("Fuzzing Options");

        final Label timeoutLabel = new Label(optionsGroup, SWT.NONE);
        timeoutLabel.setText("Connection timeout in ms:");

        timeoutSpinner = new Spinner(optionsGroup, SWT.NONE);
        timeoutSpinner.setIncrement(1);
        timeoutSpinner.setPageIncrement(50);
        timeoutSpinner.setMinimum(OptionsP.TIMEOUT_MIN);
        timeoutSpinner.setMaximum(OptionsP.TIMEOUT_MAX);

        final Label intervalLabel = new Label(optionsGroup, SWT.NONE);
        intervalLabel.setText("Fuzzing Interval in ms:");

        intervalSpinner = new Spinner(optionsGroup, SWT.NONE);
        intervalSpinner.setIncrement(1);
        intervalSpinner.setPageIncrement(50);
        intervalSpinner.setMinimum(OptionsP.INTERVAL_MIN);
        intervalSpinner.setMaximum(OptionsP.INTERVAL_MAX);

        final Label modeLabel = new Label(optionsGroup, SWT.NONE);
        modeLabel.setText("Fuzzing variable parts:");

        simultaneousButton = new Button(optionsGroup, SWT.RADIO);
        simultaneousButton.setText("Simultaneous");
        simultaneousButton.setData(Mode.SIMULTANEOUS);

        @SuppressWarnings("unused")
        final Label emptyLabel2 = new Label(optionsGroup, SWT.NONE);

        separateButton = new Button(optionsGroup, SWT.RADIO);
        separateButton.setText("Separate");
        separateButton.setData(Mode.SEPARATE);

        comButton = new Button(optionsGroup, SWT.CHECK);
        comButton.setText("Save all communication");

        final Group dataModeGroup = new Group(this, SWT.NONE);
        dataModeGroup
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        dataModeGroup.setLayout(new GridLayout(4, false));
        dataModeGroup.setText("Fuzzing Data Mode");

        final ScrolledComposite imgScrolledComposite = new ScrolledComposite( // NOPMD
                dataModeGroup, SWT.V_SCROLL);
        imgScrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
                true, true));
        imgScrolledComposite.setLayout(new GridLayout(1, false));
        imgScrolledComposite.setAlwaysShowScrollBars(true);

        protocolCanvas = new ProtocolCanvas(imgScrolledComposite,
                SWT.NO_REDRAW_RESIZE);
        protocolCanvas.setSize(10, 10);

        imgScrolledComposite.setContent(protocolCanvas);

        new ExplanationC(dataModeGroup, SWT.NONE);

        final Label seperator = new Label(dataModeGroup, SWT.SEPARATOR
                | SWT.VERTICAL);
        seperator.setLayoutData(new GridData(SWT.NONE, SWT.FILL, false, true));

        dataScrolledComposite = new ScrolledComposite(dataModeGroup,
                SWT.V_SCROLL);
        dataScrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
                true, true));
        dataScrolledComposite.setLayout(new GridLayout(1, false));
        dataScrolledComposite.setAlwaysShowScrollBars(true);
        dataScrolledComposite.setExpandVertical(true);
        dataScrolledComposite.setExpandHorizontal(true);

        dataContainerComposite = new Composite(dataScrolledComposite, SWT.NONE);
        dataContainerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
                true, true));
        dataContainerComposite.setLayout(new GridLayout(1, false));
        dataComposites = new ArrayList<DataCo>();

        dataScrolledComposite.setContent(dataContainerComposite);

        insertButtons();
        setCancelable(true);
    }

    /**
     * Adds the listener to all data composites buttons.
     *
     * @param adapter the adapter
     */
    public void addListenerToDataCompositeButtons(final SelectionAdapter adapter) {
        for (DataCo dataComposite : dataComposites) {
            dataComposite.randomButton.addSelectionListener(adapter);
            dataComposite.libraryButton.addSelectionListener(adapter);
        }
    }

    /**
     * Adds the listener to all library text fields.
     *
     * @param listener the listener
     */
    public void addListenerToLibraryTexts(final ModifyListener listener) {
        for (DataCo dataComposite : dataComposites) {
            dataComposite.libraryText.addModifyListener(listener);
        }
    }

    /**
     * Adds the listener to the mode buttons.
     *
     * @param adapter the adapter
     */
    public void addListenerToModeButtons(final SelectionAdapter adapter) {
        separateButton.addSelectionListener(adapter);
        simultaneousButton.addSelectionListener(adapter);
    }

    /**
     * Adds the listener to the destination address text.
     *
     * @param listener the listener
     */
    public void addListenerToDestAddressText(final ModifyListener listener) {
        destAddressText.addModifyListener(listener);
    }

    /**
     * Adds the listener to the destination port spinner.
     *
     * @param listener the listener
     */
    public void addListenerToDestPortSpinner(final ModifyListener listener) {
        destPortSpinner.addModifyListener(listener);
    }

    /**
     * Adds the listener to the timeout spinner.
     *
     * @param listener the listener
     */
    public void addListenerToTimeoutSpinner(final ModifyListener listener) {
        timeoutSpinner.addModifyListener(listener);
    }

    /**
     * Adds the listener to the interval spinner.
     *
     * @param listener the listener
     */
    public void addListenerToIntervalSpinner(final ModifyListener listener) {
        intervalSpinner.addModifyListener(listener);
    }

    /**
     * Adds the listener to the communication button.
     *
     * @param adapter the adapter
     */
    public void addListenerToCommunicationButton(final SelectionAdapter adapter) {
        comButton.addSelectionListener(adapter);
    }

    /**
     * Returns the destination server address.
     *
     * @return the destination address text
     */
    public String getDestAddressText() {
        return destAddressText.getText();
    }

    /**
     * Returns the destination server port.
     *
     * @return the destination port number
     */
    public int getDestPortNumber() {
        return destPortSpinner.getSelection();
    }

    /**
     * Returns the timeout to the destination.
     *
     * @return the timeout in ms
     */
    public int getTimeout() {
        return timeoutSpinner.getSelection();
    }

    /**
     * Returns the fuzzing interval.
     *
     * @return the fuzzing interval in ms
     */
    public int getInterval() {
        return intervalSpinner.getSelection();
    }

    /**
     * Returns the selection of the communication button.
     *
     * @return the communication selection
     */
    public boolean isCommunicationSelection() {
        return comButton.getSelection();
    }

    /**
     * Gets the data composites.
     *
     * @return data composites
     */
    public List<DataCo> getDataComposites() {
        return dataComposites;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable observable, final Object arg) { // NOPMD
        final OptionsP data = (OptionsP) observable;
        final boolean reseted = (Boolean) arg;

        if (reseted) {
            destAddressText.setText("");
        }

        destAddressText.setEnabled(true);
        destPortSpinner.setEnabled(true);
        if (data.isDestiantionOk()) {
            destIconLabel.setImage(ImageRepository.getInstance().getOkIcon());
            destInfoLabel.setText("Valid destination");
        } else {
            destIconLabel
                    .setImage(ImageRepository.getInstance().getErrorIcon());
            destInfoLabel.setText("Please enter a valid destination");
        }

        destPortSpinner.setSelection(reseted ? OptionsP.PORT_MIN : data
                .getDestination().getPort());

        timeoutSpinner.setSelection(data.getTimeout());

        intervalSpinner.setSelection(data.getInterval());

        separateButton.setSelection(data.getMode() == Mode.SEPARATE);
        simultaneousButton.setSelection(data.getMode() == Mode.SIMULTANEOUS);

        comButton.setSelection(data.isSaveCommunication());

        // Only update the data composites if the size of the current list is
        // different from the stored list, because it is relatively slow
        if (data.getVarParts().size() != dataComposites.size()) {
            protocolCanvas.setProtocolParts(data.getParts());
            // Dispose all data composites and clear the list
            for (DataCo dataComposite : dataComposites) {
                dataComposite.dispose();
            }
            dataComposites.clear();
            // Create new data composites
            for (int i = 0; i < data.getVarParts().size(); i++) {
                final DataCo dataComposite = new DataCo( // NOPMD
                        dataContainerComposite, SWT.NONE, i);
                final int varIndex = i;
                dataComposite.addMouseTrackListener(new MouseTrackListener() { // NOPMD

                    @Override
                    public void mouseHover(final MouseEvent event) { // NOPMD
                    }

                    @Override
                    public void mouseExit(final MouseEvent event) { // NOPMD
                    }

                    @Override
                    public void mouseEnter(final MouseEvent event) {
                        protocolCanvas.highlightPixels(varIndex);
                    }
                });
                dataComposites.add(dataComposite);
            }
        }

        for (int i = 0; i < data.getVarParts().size(); i++) {
            dataComposites.get(i).update(data, reseted, i);
        }

        setComplete(data.isDestiantionOk() && data.varPartsOk());

        // Set the min height for the scrolled composite
        dataScrolledComposite.setMinHeight(dataContainerComposite.computeSize(
                SWT.DEFAULT, SWT.DEFAULT).y);
        dataContainerComposite.layout();
        layout();
    }

}
