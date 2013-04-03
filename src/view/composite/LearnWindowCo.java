/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view.composite;

import model.Model;
import model.ProtocolFile;
import model.process.LearnP;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import view.ImageRepository;
import view.ProtocolCanvas;

import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

/**
 * The Class LearnWindowCo displays all widgets which are necessary for learning
 * the protocol from all files.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class LearnWindowCo extends WindowCo implements Observer {

    /**
     * The graphic group.
     */
    private final Group graphicGroup;

    /**
     * The start/stop button composite.
     */
    private final Button startStopButton;

    /**
     * The progress bar.
     */
    private final ProgressBar progressBar;

    /**
     * The file list.
     */
    private final List fileList;

    /**
     * The status info label.
     */
    private final Label statusInfoLabel;

    /**
     * The status icon label.
     */
    private final Label statusIconLabel;

    /**
     * The protocol canvas.
     */
    private final ProtocolCanvas protocolCanvas;

    /**
     * The detail text.
     */
    private final StyledText detailText;

    /**
     * The memory usage label.
     */
    private final Label memoryUsageLabel;

    /**
     * The scrollable image composite.
     */
    private ScrolledComposite imgScrolledComposite; // NOPMD

    /**
     * Instantiates a new learn window composite.
     *
     * @param parent    the parent
     * @param style     the style
     * @param page      the page number
     * @param totalPage the total page number
     */
    public LearnWindowCo(final Composite parent, final int style,
                         final int page, final int totalPage) {
        super(parent, style, page, totalPage);

        setLayout(new GridLayout(1, false));

        final Group progressGroup = new Group(this, SWT.NONE);
        progressGroup.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
                false));
        progressGroup.setLayout(new GridLayout(3, false));
        progressGroup.setText("Learn Protocol");

        startStopButton = new Button(progressGroup, SWT.PUSH);
        startStopButton.setLayoutData(new GridData(SWT.NONE, SWT.NONE, false,
                false));

        progressBar = new ProgressBar(progressGroup, SWT.HORIZONTAL);
        progressBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
                false));
        progressBar.setMinimum(0);

        final Composite filesComposite = new Composite(progressGroup, SWT.NONE);
        filesComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
                3));
        filesComposite.setLayout(new GridLayout(1, false));

        final Label filesLabel = new Label(filesComposite, SWT.NONE);
        filesLabel.setText("Untouched files:");

        fileList = new List(filesComposite, SWT.BORDER | SWT.SINGLE
                | SWT.V_SCROLL);
        fileList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Label emptyLabel = new Label(progressGroup, SWT.NONE);
        emptyLabel
                .setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));

        final Composite statusComposite = new Composite(progressGroup, SWT.NONE);
        statusComposite.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
                false));
        statusComposite.setLayout(new GridLayout(2, false));

        statusIconLabel = new Label(statusComposite, SWT.NONE);
        statusIconLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
                false, false));

        statusInfoLabel = new Label(statusComposite, SWT.NONE);
        statusInfoLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
                false));

        emptyLabel = new Label(statusComposite, SWT.NONE);
        emptyLabel
                .setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));

        memoryUsageLabel = new Label(statusComposite, SWT.NONE);
        memoryUsageLabel.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true,
                false));

        graphicGroup = new Group(this, SWT.NONE);
        graphicGroup
                .setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
        graphicGroup.setLayout(new GridLayout(1, false));
        graphicGroup.setText("Graphical Representation");

        final SashForm sashForm = new SashForm(graphicGroup, SWT.VERTICAL);
        sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final Composite grapicComposite = new Composite(sashForm, SWT.NONE);
        grapicComposite.setLayout(new GridLayout(2, false));

        imgScrolledComposite = new ScrolledComposite(grapicComposite, SWT.V_SCROLL);
        imgScrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL,
                true, true));
        imgScrolledComposite.setLayout(new GridLayout(1, false));
        imgScrolledComposite.setAlwaysShowScrollBars(true);

        protocolCanvas = new ProtocolCanvas(imgScrolledComposite,
                SWT.NO_REDRAW_RESIZE);
        protocolCanvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseUp(MouseEvent event) { // NOPMD
            }

            @Override
            public void mouseDown(final MouseEvent event) {
                protocolCanvas.highlightPixels(event.x, event.y);
                final int[] range = protocolCanvas.getRangeForPos(event.x,
                        event.y);
                final Color color = protocolCanvas.getColorForPos(event.x,
                        event.y);
                StyleRange styleRange = new StyleRange(0, detailText.getText()
                        .length(), new Color(Display.getDefault(), 0, 0, 0),
                        null);
                detailText.setStyleRange(styleRange);
                styleRange = new StyleRange(range[0], range[1], null, color);
                detailText.setStyleRange(styleRange);
                detailText.setTopIndex(detailText.getLineAtOffset(range[0]));
            }

            @Override
            public void mouseDoubleClick(final MouseEvent event) { // NOPMD
            }
        });
        protocolCanvas.setSize(10, 10);

        imgScrolledComposite.setContent(protocolCanvas);

        @SuppressWarnings("unused")
        final Composite expComposite = new ExplanationC(grapicComposite, SWT.NONE);

        detailText = new StyledText(sashForm, SWT.WRAP | SWT.MULTI | SWT.BORDER
                | SWT.V_SCROLL);
        detailText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true,
                2, 1));
        detailText.setEditable(false);

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

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable observable, final Object arg) { // NOPMD
        final LearnP data = (LearnP) observable;

        startStopButton.setText(data.isThreadRunning() ? " Stop " : " Start ");

        progressBar.setMaximum(data.getTotalProgress());
        progressBar.setSelection(data.getProgress());

        // Remove all files from the list and add not yet learned files to it
        fileList.removeAll();
        for (ProtocolFile file : data.getFiles()) {
            if (!file.isLearned()) {
                fileList.add(file.getName());
            }
        }

        if (data.isThreadRunning()) {
            statusIconLabel.setImage(ImageRepository.getInstance()
                    .getWorkingIcon());
            statusInfoLabel.setText(data.getNumOfTmpSequences()
                    + " sequences are still in the queue");
        } else if (data.getProgress() == data.getTotalProgress()) {
            statusIconLabel.setImage(ImageRepository.getInstance().getOkIcon());
            statusInfoLabel.setText(data.getFiles().size()
                    + " files have been analyzed");
        } else {
            statusIconLabel.setImage(ImageRepository.getInstance()
                    .getErrorIcon());
            statusInfoLabel.setText(data.getNumOfTmpSequences() + " of "
                    + data.getFiles().size() + " files were not analyzed yet");
        }

        // Dispaly the amount of used memory in MB
        final DecimalFormat numberFormat = new DecimalFormat("#0.00");
        memoryUsageLabel
                .setText(numberFormat.format(Model.getMemoryUsage() / 1024 / 1024)
                        + " MB memory usage");

        protocolCanvas.setProtocolParts(data.getParts());

        detailText.setText(protocolCanvas.getText());

        setCancelable(!data.isThreadRunning());
        setComplete(data.getTotalProgress() == data.getProgress());

        graphicGroup.layout();

    }

}
