/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view.composite;

import model.process.LoadXMLP;
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
 * The Class LoadXMLWindowCo displays all widgets which are necessary to load a
 * XML file.
 *
 * @author Volker Nebelung
 */
public class LoadXMLWindowCo extends WindowCo implements Observer { // NOPMD

    /**
     * The XML load group.
     */
    private final Group xmlLoadGroup;

    /**
     * The start/stop button.
     */
    private final Button startStopButton;

    /**
     * The progress bar.
     */
    private final ProgressBar progressBar;

    /**
     * The file text.
     */
    private final Text fileText;

    /**
     * The status text that displays the XML generation status.
     */
    private final MessageLog statusLog;

    /**
     * The status info label.
     */
    private final Label statusInfoLabel;

    /**
     * The status icon label.
     */
    private final Label statusIconLabel;

    /**
     * The browse button.
     */
    private final Button browseButton;

    /**
     * Instantiates a new load XML window composite.
     *
     * @param parent    the parent
     * @param style     the style
     * @param page      the page number
     * @param totalPage the total page number
     */
    public LoadXMLWindowCo(final Composite parent, final int style,
                           final int page, final int totalPage) {
        super(parent, style, page, totalPage);

        this.setLayout(new GridLayout(1, false));

        xmlLoadGroup = new Group(this, SWT.NONE);
        xmlLoadGroup
                .setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        xmlLoadGroup.setLayout(new GridLayout(4, false));
        xmlLoadGroup.setText("Load XML file");

        startStopButton = new Button(xmlLoadGroup, SWT.PUSH);
        startStopButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false,
                false));

        final Label fileLabel = new Label(xmlLoadGroup, SWT.NONE);
        fileLabel.setText("XML File:");

        fileText = new Text(xmlLoadGroup, SWT.BORDER);
        fileText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

        browseButton = new Button(xmlLoadGroup, SWT.PUSH);
        browseButton.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false,
                false));
        browseButton.setText(" Browse... ");
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent event) {
                final FileDialog fileDialog = new FileDialog(parent.getShell());
                fileDialog.setFilterPath(fileText.getText());
                fileDialog.setFilterNames(new String[]{"XML Files"});
                fileDialog.setFilterExtensions(new String[]{"*.xml"});
                final String file = fileDialog.open();
                if (file != null) {
                    fileText.setText(file);
                }
            }
        });

        final Label emptyLabel = new Label(xmlLoadGroup, SWT.NONE);
        emptyLabel.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false,
                2, 1));

        final Composite statusComposite = new Composite(xmlLoadGroup, SWT.NONE);
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
    public void addListenerToFileText(final ModifyListener listener) {
        fileText.addModifyListener(listener);
    }

    /**
     * Gets the file text.
     *
     * @return the file text
     */
    public String getFileText() {
        return fileText.getText();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable observable, final Object arg) {
        final LoadXMLP data = (LoadXMLP) observable;
        final boolean reseted = (Boolean) arg;

        fileText.setEnabled(!data.isThreadRunning());
        if (reseted) {
            fileText.setText("");
        }

        browseButton.setEnabled(!data.isThreadRunning());

        if (data.getFilePath() == null) {
            statusIconLabel.setImage(ImageRepository.getInstance()
                    .getErrorIcon());
            statusInfoLabel.setText("Please choose a valid XML file.");
        } else if (data.isThreadRunning()) {
            statusIconLabel.setImage(ImageRepository.getInstance()
                    .getWorkingIcon());
            statusInfoLabel
                    .setText("Loading XML file and building XML structure ...");
        } else {
            statusIconLabel.setImage(ImageRepository.getInstance().getOkIcon());
            statusInfoLabel
                    .setText("Valid XML file chosen. Ready to start loading.");
        }

        startStopButton.setText(data.isThreadRunning() ? " Stop " : " Start ");
        startStopButton.setEnabled(data.getFilePath() != null);

        progressBar.setMaximum(data.getTotalProgress());
        progressBar.setSelection(data.getProgress());

        statusLog.addText(data.getStateMessage());

        setCancelable(!data.isThreadRunning());
        setComplete(data.getProgress() == data.getTotalProgress()
                && !data.isThreadRunning() && data.getProgress() > 0);

        xmlLoadGroup.layout();
    }

}
