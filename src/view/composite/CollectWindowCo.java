/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view.composite;

import model.process.CollectP;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import view.ImageRepository;

import java.nio.file.Path;
import java.util.Observable;
import java.util.Observer;

/**
 * The Class CollectWindowCo displays all widgets which are necessary for
 * collecting the protocol files.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class CollectWindowCo extends WindowCo implements Observer { // NOPMD

    /**
     * The start/stop button.
     */
    private final Button startStopButton;

    /**
     * The reset button.
     */
    private final Button resetButton;

    /**
     * The browse button.
     */
    private final Button browseButton;

    /**
     * The idle progress bar.
     */
    private final ProgressBar idleProgressBar;

    /**
     * The busy progress bar.
     */
    private final ProgressBar busyProgressBar;

    /**
     * The file text.
     */
    private final Text directoryText;

    /**
     * The file group.
     */
    private final Group fileGroup;

    /**
     * The status info label.
     */
    private final Label statusInfoLabel;

    /**
     * The progress group.
     */
    private final Group progressGroup;

    /**
     * The status icon label.
     */
    private final Label statusIconLabel;

    /**
     * The list of collected files.
     */
    private final List filesList;

    /**
     * The file info label
     */
    private final Label fileInfoLabel;

    /**
     * The file icon label.
     */
    private final Label fileIconLabel;

    /**
     * Instantiates all widgets for the collect window.
     *
     * @param parent    the parent
     * @param style     the style
     * @param page      the page number
     * @param totalPage the total page number
     */
    public CollectWindowCo(final Composite parent, final int style,
                           final int page, final int totalPage) {
        super(parent, style, page, totalPage);

        this.setLayout(new GridLayout(1, false));

        fileGroup = new Group(this, SWT.NONE);
        fileGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        fileGroup.setLayout(new GridLayout(4, false));
        fileGroup.setText("File Collecting");

        startStopButton = new Button(fileGroup, SWT.PUSH);
        startStopButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false,
                false));

        final Label directoryLabel = new Label(fileGroup, SWT.NONE);
        directoryLabel.setText("Monitored Directory:");

        directoryText = new Text(fileGroup, SWT.BORDER);
        directoryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false));

        browseButton = new Button(fileGroup, SWT.PUSH);
        browseButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false,
                false));
        browseButton.setText(" Browse... ");
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                final DirectoryDialog directoryDialog = new DirectoryDialog(
                        parent.getShell());
                directoryDialog.setFilterPath(directoryText.getText());
                final String directory = directoryDialog.open();
                if (directory != null) {
                    directoryText.setText(directory);
                }
            }
        });

        resetButton = new Button(fileGroup, SWT.PUSH);
        resetButton.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false,
                false, 2, 1));
        resetButton.setText(" Reset ");

        final Composite statusComposite = new Composite(fileGroup, SWT.NONE);
        statusComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
                false, 2, 1));
        statusComposite.setLayout(new GridLayout(2, false));

        statusIconLabel = new Label(statusComposite, SWT.NONE);
        statusIconLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false,
                false));

        statusInfoLabel = new Label(statusComposite, SWT.NONE);
        statusInfoLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
                false));

        progressGroup = new Group(this, SWT.NONE);
        progressGroup
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        progressGroup.setLayout(new GridLayout(3, true));
        progressGroup.setText("Progress Information");

        idleProgressBar = new ProgressBar(progressGroup, SWT.HORIZONTAL);
        idleProgressBar.setMinimum(0);
        idleProgressBar.setMaximum(1);

        busyProgressBar = new ProgressBar(progressGroup, SWT.HORIZONTAL
                | SWT.INDETERMINATE);

        filesList = new List(progressGroup, SWT.BORDER | SWT.SINGLE
                | SWT.V_SCROLL);
        filesList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
                2));

        fileIconLabel = new Label(progressGroup, SWT.NONE);
        fileIconLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false,
                false));

        fileInfoLabel = new Label(progressGroup, SWT.NONE);
        fileInfoLabel.setLayoutData(new GridData(SWT.NONE, SWT.CENTER, false,
                true));

        insertButtons();
    }

    /**
     * Adds the listener to the start/stop button.
     *
     * @param adapter the adapter
     */
    public void addListenerToStartStopButton(final SelectionAdapter adapter) {
        startStopButton.addSelectionListener(adapter);
    }

    /**
     * Adds the listener to the directory text field.
     *
     * @param listener the listener
     */
    public void addListenerToDirectoryText(final ModifyListener listener) {
        directoryText.addModifyListener(listener);
    }

    /**
     * Adds the listener to the reset button.
     *
     * @param adapter the adapter
     */
    public void addListenerToResetButton(final SelectionAdapter adapter) {
        resetButton.addSelectionListener(adapter);
    }

    /**
     * Gets the directory text.
     *
     * @return the directory text
     */
    public String getDirectoryText() {
        return directoryText.getText();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable observable, final Object arg) { // NOPMD
        final CollectP data = (CollectP) observable;
        final boolean reseted = (Boolean) arg;

        final GridData gridDataHidden = new GridData(SWT.FILL, SWT.CENTER,
                true, false, 2, 1);
        gridDataHidden.exclude = true;
        final GridData gridDataVisible = new GridData(SWT.FILL, SWT.CENTER,
                true, false, 2, 1);
        gridDataVisible.exclude = false;

        startStopButton.setText(data.isThreadRunning() ? " Stop " : " Start ");
        startStopButton.setEnabled(data.getDirectoryPath() != null);
        resetButton.setEnabled(!data.isThreadRunning()
                && data.getFilePaths().size() > 0);

        directoryText.setEnabled(!data.isThreadRunning());
        if (reseted) {
            directoryText.setText("");
        }

        browseButton.setEnabled(!data.isThreadRunning());

        if (data.getDirectoryPath() == null) {
            statusIconLabel.setImage(ImageRepository.getInstance()
                    .getErrorIcon());
            statusInfoLabel
                    .setText("Please choose a valid diretory in which the protocol records are stored");
        } else if (data.isThreadRunning()) {
            statusIconLabel.setImage(ImageRepository.getInstance()
                    .getWorkingIcon());
            statusInfoLabel
                    .setText("Collecting of protocol files is running ...");
        } else {
            statusIconLabel.setImage(ImageRepository.getInstance().getOkIcon());
            statusInfoLabel.setText("Valid directory chosen. Ready to start");
        }

        idleProgressBar.setSelection(data.getFilePaths().size() == 0 ? 0 : 1);
        idleProgressBar.setVisible(!data.isThreadRunning());
        idleProgressBar.setLayoutData(data.isThreadRunning() ? gridDataHidden
                : gridDataVisible);
        busyProgressBar.setVisible(data.isThreadRunning());
        busyProgressBar.setLayoutData(data.isThreadRunning() ? gridDataVisible
                : gridDataHidden);

        // Clears all files from the list and adds all current files
        filesList.removeAll();
        for (Path path : data.getFilePaths()) {
            filesList.add(path.getFileName().toString());
        }

        if (data.isThreadRunning()) {
            fileIconLabel.setImage(ImageRepository.getInstance()
                    .getWorkingIcon());
        } else if (data.getFilePaths().size() < 2) {
            fileIconLabel
                    .setImage(ImageRepository.getInstance().getErrorIcon());
        } else {
            fileIconLabel.setImage(ImageRepository.getInstance().getOkIcon());
        }

        fileInfoLabel.setText(data.getFilePaths().size() + " file"
                + (data.getFilePaths().size() == 1 ? "" : "s") + " detected");

        setCancelable(!data.isThreadRunning());
        setComplete(!data.isThreadRunning() && data.getFilePaths().size() > 1);

        fileGroup.layout();
        progressGroup.layout();
    }
}
