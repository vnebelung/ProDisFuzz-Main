/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view.composite;

import model.ProtocolPart.DataMode;
import model.process.OptionsP;
import model.process.OptionsP.Mode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import view.ImageRepository;

/**
 * The Class DataCo displays the widgets for choosing the fuzzing data for a
 * protocol part. There are two methods which can be chosen: random data and a
 * library file.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class DataCo extends Composite { // NOPMD

    /**
     * The random button.
     */
    public final Button randomButton;

    /**
     * The library button.
     */
    public final Button libraryButton;

    /**
     * The browse button.
     */
    public final Button browseButton;

    /**
     * The library text field.
     */
    public final Text libraryText;

    /**
     * The library info label.
     */
    public Label libraryIconLabel;

    /**
     * The library icon label.
     */
    public Label libraryInfoLabel;

    /**
     * The status grid data.
     */
    private final GridData statusGridData;

    /**
     * The empty grid data.
     */
    private final GridData emptyGridData;

    /**
     * Instantiates a new data composite.
     *
     * @param parent the parent composite
     * @param id     an unique index for this composite
     */
    public DataCo(final Composite parent, final int style, final int id) { // NOPMD
        super(parent, style);

        setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        setLayout(new GridLayout(4, false));

        randomButton = new Button(this, SWT.RADIO);
        randomButton.setText("Random");
        randomButton.setData(id);

        libraryButton = new Button(this, SWT.RADIO);
        libraryButton.setText("File:");
        libraryButton.setData(id);

        libraryText = new Text(this, SWT.BORDER);
        libraryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false));
        libraryText.setData(id);

        browseButton = new Button(this, SWT.PUSH);
        browseButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false,
                false));
        browseButton.setText(" Browse... ");
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                final FileDialog fileDialog = new FileDialog(parent.getShell());
                fileDialog.setFilterPath(libraryText.getText());
                final String file = fileDialog.open();
                if (file != null) {
                    libraryText.setText(file);
                }
            }
        });

        final Label emptyLabel = new Label(this, SWT.NONE);
        emptyGridData = new GridData(SWT.NONE, SWT.NONE, false, false, 2, 1);
        emptyLabel.setLayoutData(emptyGridData);

        final Composite statusComposite = new Composite(this, SWT.NONE);
        statusGridData = new GridData(SWT.FILL, SWT.NONE, true, false, 2, 1);
        statusComposite.setLayoutData(statusGridData);
        statusComposite.setLayout(new GridLayout(2, false));

        libraryIconLabel = new Label(statusComposite, SWT.NONE);
        libraryIconLabel.setLayoutData(new GridData(SWT.RIGHT));

        libraryInfoLabel = new Label(statusComposite, SWT.NONE);
        libraryInfoLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
                false));

    }

    /**
     * Gets the mode depending on the button which is selected
     *
     * @return the mode
     */
    public DataMode getMode() {
        DataMode mode = null;
        if (randomButton.getSelection()) {
            mode = DataMode.RANDOM;
        } else if (libraryButton.getSelection()) {
            mode = DataMode.LIBRARY;
        }
        return mode;
    }

    /**
     * Updates all widgets of this data composite. This method may be called
     * within an update method of an observer.
     *
     * @param data    The options process
     * @param reseted true if all widgets shall be reseted
     * @param index   the index of the variable protocol part
     */
    public void update(final OptionsP data, final boolean reseted, // NOPMD
                       final int index) {
        randomButton
                .setSelection(data.getVarParts().get(index).getDataMode() == DataMode.RANDOM);
        libraryButton
                .setSelection(data.getVarParts().get(index).getDataMode() == DataMode.LIBRARY);
        switch (data.getMode()) {
            case SEPARATE:
                randomButton.setEnabled(true);
                libraryButton.setEnabled(true);
                libraryText
                        .setEnabled(data.getVarParts().get(index).getDataMode() == DataMode.LIBRARY);
                browseButton
                        .setEnabled(data.getVarParts().get(index).getDataMode() == DataMode.LIBRARY);
                break;
            case SIMULTANEOUS:
                randomButton.setEnabled(index == 0 ? true : false);
                libraryButton.setEnabled(index == 0 ? true : false);
                libraryText.setEnabled(index == 0 ? data.getVarParts().get(0)
                        .getDataMode() == DataMode.LIBRARY : false);
                browseButton.setEnabled(index == 0 ? data.getVarParts().get(0)
                        .getDataMode() == DataMode.LIBRARY : false);
                break;
            default:
                break;
        }

        if (data.getVarParts().get(index).getLibraryPath() == null) {
            libraryIconLabel.setImage(ImageRepository.getInstance()
                    .getErrorIcon());
            libraryInfoLabel.setText("Please choose a valid library file.");
        } else {
            libraryIconLabel
                    .setImage(ImageRepository.getInstance().getOkIcon());
            libraryInfoLabel.setText("Valid library file chosen.");
        }
        switch (data.getVarParts().get(index).getDataMode()) {
            case RANDOM:
                emptyGridData.exclude = true;
                statusGridData.exclude = true;
                break;
            case LIBRARY:
                emptyGridData.exclude = (index > 0 && data.getMode() == Mode.SIMULTANEOUS);
                statusGridData.exclude = (index > 0 && data.getMode() == Mode.SIMULTANEOUS);
                break;
            default:
                break;
        }

        layout();
    }

}
