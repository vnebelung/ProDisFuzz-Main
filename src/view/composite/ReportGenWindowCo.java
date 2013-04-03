/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view.composite;

import model.process.ReportGenP;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import view.ImageRepository;
import view.MessageLog;

import java.util.Observable;
import java.util.Observer;

/**
 * The Class ReportGenWindowCo displays all widgets which are necessary to
 * visualize the generation of the report containing all information about the
 * detected crashes.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ReportGenWindowCo extends WindowCo implements Observer {

    /**
     * The report generation group.
     */
    private final Group reportGenGroup;

    /**
     * The start/stop button.
     */
    private final Button startStopButton;

    /**
     * The directory text.
     */
    private final Text directoryText;

    /**
     * The browse button.
     */
    private final Button browseButton;

    /**
     * The status info label.
     */
    private final Label statusInfoLabel;

    /**
     * The status icon label.
     */
    private final Label statusIconLabel;

    /**
     * The progress bar.
     */
    private final ProgressBar progressBar;

    /**
     * The status text that displays the report generation status.
     */
    private final MessageLog statusLog;

    /**
     * Instantiates a new check window composite.
     *
     * @param parent    the parent
     * @param style     the style
     * @param page      the page number
     * @param totalPage the total page number
     */
    public ReportGenWindowCo(final Composite parent, final int style,
                             final int page, final int totalPage) {
        super(parent, style, page, totalPage);
        this.setLayout(new GridLayout(1, false));

        reportGenGroup = new Group(this, SWT.NONE);
        reportGenGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
                false));
        reportGenGroup.setLayout(new GridLayout(4, false));
        reportGenGroup.setText("Report generation");

        startStopButton = new Button(reportGenGroup, SWT.PUSH);
        startStopButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false,
                false));

        final Label directoryLabel = new Label(reportGenGroup, SWT.NONE);
        directoryLabel.setText("Output File:");

        directoryText = new Text(reportGenGroup, SWT.BORDER);
        directoryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false));

        browseButton = new Button(reportGenGroup, SWT.PUSH);
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

        final Label emptyLabel = new Label(reportGenGroup, SWT.NONE);
        emptyLabel.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false,
                2, 1));

        final Composite statusComposite = new Composite(reportGenGroup,
                SWT.NONE);
        statusComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
                false));
        statusComposite.setLayout(new GridLayout(2, false));

        statusIconLabel = new Label(statusComposite, SWT.NONE);
        statusIconLabel.setLayoutData(new GridData(SWT.RIGHT));

        statusInfoLabel = new Label(statusComposite, SWT.NONE);
        statusInfoLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
                false));

        final Group progressGroup = new Group(this, SWT.NONE);
        progressGroup
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        progressGroup.setLayout(new GridLayout(1, true));
        progressGroup.setText("Progress Information");

        progressBar = new ProgressBar(progressGroup, SWT.HORIZONTAL);
        progressBar
                .setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        progressBar.setMinimum(0);

        statusLog = new MessageLog(progressGroup, SWT.MULTI | SWT.BORDER
                | SWT.WRAP | SWT.V_SCROLL);
        statusLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        insertButtons();
    }

    /**
     * Gets the directory.
     *
     * @return the directory text
     */
    public String getDirectoryText() {
        return directoryText.getText();
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
     * Gets the file text.
     *
     * @return the file text
     */
    public String getFileText() {
        return directoryText.getText();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable observable, final Object arg) { // NOPMD
        final ReportGenP data = (ReportGenP) observable;
        final boolean reseted = (Boolean) arg;

        directoryText.setEnabled(!data.isThreadRunning());
        if (reseted) {
            directoryText.setText("");
        }

        browseButton.setEnabled(!data.isThreadRunning());

        if (data.getOutputPath() == null) {
            statusIconLabel.setImage(ImageRepository.getInstance()
                    .getErrorIcon());
            statusInfoLabel
                    .setText("Please choose a valid directory to which the report will be written.");
        } else if (data.isThreadRunning()) {
            statusIconLabel.setImage(ImageRepository.getInstance()
                    .getWorkingIcon());
            statusInfoLabel.setText("Report generation is running ...");
        } else {
            statusIconLabel.setImage(ImageRepository.getInstance().getOkIcon());
            statusInfoLabel.setText("Valid directory chosen. Ready to start.");
        }

        startStopButton.setText(data.isThreadRunning() ? " Stop " : " Start ");
        startStopButton.setEnabled(data.getOutputPath() != null);

        progressBar.setMaximum(data.getTotalProgress());
        progressBar.setSelection(data.getProgress());

        statusLog.addText(data.getStateMessage());

        setCancelable(!data.isThreadRunning() && data.getProgress() == 0);
        setFinish(data.getProgress() == data.getTotalProgress()
                && !data.isThreadRunning() && data.getProgress() > 0);

        reportGenGroup.layout();
    }
}
