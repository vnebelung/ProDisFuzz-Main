/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package view.composite;

import model.RunnableThread.RunnableState;
import model.process.FuzzingP;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import view.ImageRepository;
import view.MessageLog;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The Class FuzzingWindowCo displays all widgets which are necessary to run the
 * fuzzing.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class FuzzingWindowCo extends WindowCo implements Observer { // NOPMD

    /**
     * The start/stop button.
     */
    private final Button startStopButton;

    /**
     * The status icon label.
     */
    private final Label statusIconLabel;

    /**
     * The status info label.
     */
    private final Label statusInfoLabel;

    /**
     * The finite progress bar.
     */
    private final ProgressBar finiteProgressBar;

    /**
     * The infinite progress bar.
     */
    private final ProgressBar infiniteProgressBar; // NOPMD

    /**
     * The status log.
     */
    private final MessageLog statusLog;

    /**
     * The fuzzing group.
     */
    private final Group fuzzingGroup;

    /**
     * The info composite.
     */
    private final Composite infoComposite;

    /**
     * The time label.
     */
    private final Label timeLabel;

    /**
     * The progress label.
     */
    private final Label progressLabel;

    /**
     * The timer for the elasped time.
     */
    private Timer timer;

    /**
     * Instantiates a new fuzzing window composite.
     *
     * @param parent    the parent
     * @param style     the style
     * @param page      the page number
     * @param totalPage the total page number
     */
    public FuzzingWindowCo(final Composite parent, final int style,
                           final int page, final int totalPage) {
        super(parent, style, page, totalPage);

        this.setLayout(new GridLayout(1, false));

        fuzzingGroup = new Group(this, SWT.NONE);
        fuzzingGroup
                .setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        fuzzingGroup.setLayout(new GridLayout(3, false));
        fuzzingGroup.setText("Fuzzing");

        startStopButton = new Button(fuzzingGroup, SWT.PUSH);

        finiteProgressBar = new ProgressBar(fuzzingGroup, SWT.HORIZONTAL);
        finiteProgressBar.setMinimum(0);

        infiniteProgressBar = new ProgressBar(fuzzingGroup, SWT.HORIZONTAL
                | SWT.INDETERMINATE);

        infoComposite = new Composite(fuzzingGroup, SWT.BORDER);
        infoComposite.setLayoutData(new GridData(SWT.NONE, SWT.FILL, false,
                true, 1, 2));
        infoComposite.setLayout(new GridLayout(2, false));

        final Label timeDescrLabel = new Label(infoComposite, SWT.NONE);
        timeDescrLabel.setText("Time elapsed: ");

        timeLabel = new Label(infoComposite, SWT.NONE);
        timeLabel
                .setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false, false));

        final Label progressDescrLabel = new Label(infoComposite, SWT.NONE); // NOPMD
        progressDescrLabel.setText("Progress: ");

        progressLabel = new Label(infoComposite, SWT.NONE);
        progressLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.NONE, false,
                false));

        final Label emptyLabel = new Label(fuzzingGroup, SWT.NONE);
        emptyLabel
                .setLayoutData(new GridData(SWT.NONE, SWT.NONE, false, false));

        final Composite statusComposite = new Composite(fuzzingGroup, SWT.NONE);
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
        progressGroup.setLayout(new GridLayout(1, false));
        progressGroup.setText("Progress Information");

        statusLog = new MessageLog(progressGroup, SWT.MULTI | SWT.BORDER
                | SWT.WRAP | SWT.V_SCROLL);
        statusLog.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        insertButtons();
        setCancelable(true);
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
     * Starts the timer and displays the time elapsed.
     *
     * @param startTime the start time of the timer
     */
    private void startTimer(final long startTime) {
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!getDisplay().isDisposed()) {
                        // New runnable needed for changing the output of the
                        // timer
                        getDisplay().syncExec(new Runnable() {
                            @Override
                            public void run() {
                                // Fill the time values up to 2 chars with
                                // preceding zeros
                                final DecimalFormat timeFormat = new DecimalFormat(
                                        "00");
                                try {
                                    // The duration of the current fuzzing
                                    // process is the current time minus the
                                    // given start time
                                    final Duration duration = DatatypeFactory
                                            .newInstance().newDuration(
                                                    System.currentTimeMillis()
                                                            - startTime);
                                    timeLabel.setText(timeFormat
                                            .format(duration.getHours())
                                            + ":"
                                            + timeFormat.format(duration
                                            .getMinutes())
                                            + ":"
                                            + timeFormat.format(duration
                                            .getSeconds()));
                                } catch (DatatypeConfigurationException e) {
                                    timeLabel.setText("ERROR");
                                }
                                infoComposite.layout();
                                fuzzingGroup.layout();
                            }
                        });
                    }
                }
            }, 0, 1000);
        }
    }

    /**
     * Stops the timer.
     */
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null; // NOPMD
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable observable, final Object arg) { // NOPMD
        final FuzzingP data = (FuzzingP) observable;
        final boolean reseted = (Boolean) arg;
        final GridData gridDataHidden = new GridData(SWT.FILL, SWT.CENTER,
                true, false);
        gridDataHidden.exclude = true;
        final GridData gridDataVisible = new GridData(SWT.FILL, SWT.CENTER,
                true, false);
        gridDataVisible.exclude = false;

        startStopButton.setText(data.getThreadState() == RunnableState.FINISHED
                || data.getThreadState() == RunnableState.CANCELED ? " Start "
                : " Stop ");

        switch (data.getThreadState()) {
            case RUNNING:
                statusIconLabel.setImage(ImageRepository.getInstance()
                        .getWorkingIcon());
                statusInfoLabel.setText("Fuzzing process is running ...");
                break;
            case STUCK:
                statusIconLabel.setImage(ImageRepository.getInstance()
                        .getErrorIcon());
                statusInfoLabel.setText("Possible crash detected");
                break;
            case FINISHED:
            case CANCELED:
                statusIconLabel.setImage(ImageRepository.getInstance().getOkIcon());
                switch (data.getNumOfCrashes()) {
                    case 0:
                        statusInfoLabel.setText("Ready to start");
                        break;
                    case 1:
                        statusInfoLabel.setText("1 crash detected");
                        break;
                    default:
                        statusInfoLabel.setText(data.getNumOfCrashes()
                                + " crashes detected");
                        break;
                }
            default:
                break;
        }

        finiteProgressBar.setSelection(data.getTotalProgress() == -1 ? 1 : data
                .getProgress());
        finiteProgressBar.setMaximum(data.getTotalProgress() == -1 ? 1 : data
                .getTotalProgress());
        finiteProgressBar
                .setVisible(data.getThreadState() == RunnableState.FINISHED
                        || data.getThreadState() == RunnableState.CANCELED
                        || data.getTotalProgress() >= 0);
        finiteProgressBar
                .setLayoutData(data.getThreadState() == RunnableState.FINISHED
                        || data.getThreadState() == RunnableState.CANCELED
                        || data.getTotalProgress() >= 0 ? gridDataVisible
                        : gridDataHidden);
        infiniteProgressBar
                .setVisible(!(data.getThreadState() == RunnableState.FINISHED || data
                        .getThreadState() == RunnableState.CANCELED)
                        && data.getTotalProgress() < 0);
        infiniteProgressBar
                .setLayoutData(!(data.getThreadState() == RunnableState.FINISHED || data
                        .getThreadState() == RunnableState.CANCELED)
                        && data.getTotalProgress() < 0 ? gridDataVisible
                        : gridDataHidden);

        // If the start time is -1 the timer as to stop and the default time is
        // displayed
        if (data.getStartTime() == -1) {
            stopTimer();
            timeLabel.setText("00:00:00");
        } else {
            if (data.getThreadState() == RunnableState.RUNNING) {
                startTimer(data.getStartTime());
            } else {
                stopTimer();
            }
        }

        progressLabel.setText(data.getProgress()
                + "/"
                + (data.getTotalProgress() == -1 ? "inf" : data
                .getTotalProgress()));
        statusLog.addText(data.getStateMessage());

        if (reseted) {
            stopTimer();
        }

        setCancelable(data.getThreadState() == RunnableState.FINISHED
                || data.getThreadState() == RunnableState.CANCELED);
        setComplete((data.getThreadState() == RunnableState.FINISHED || data
                .getThreadState() == RunnableState.CANCELED)
                && data.getNumOfCrashes() > 0);

        infoComposite.layout();
        fuzzingGroup.layout();
        layout();
    }
}
