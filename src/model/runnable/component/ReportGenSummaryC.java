/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.TempRecordFile;
import model.runnable.AbstractR;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.datatype.Duration;
import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.util.List;

/**
 * The Class ReportGenSummeryC implements the functionality to generate the
 * summary table.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ReportGenSummaryC extends AbstractC {

    /**
     * The recorded files.
     */
    private final List<TempRecordFile> recordFiles;

    /**
     * The destination address and port.
     */
    private final InetSocketAddress destination;

    /**
     * The fuzzing interval in milliseconds.
     */
    private final int interval;

    /**
     * The fuzzing duration.
     */
    private final Duration duration;

    /**
     * The amount of total fuzzing iterations.
     */
    private final int fuzzingTotalProgress; // NOPMD

    /**
     * The number of actually executed fuzzing iterations.
     */
    private final int fuzzingProgress;

    /**
     * The connection timeout in milliseconds.
     */
    private final int timeout;

    /**
     * Instantiates a new report generation summary component.
     *
     * @param runnable             the runnable
     * @param destination          the destination
     * @param interval             the fuzzing interval
     * @param recordFiles          the recorded files
     * @param duration             the fuzzing duration
     * @param fuzzingTotalProgress the total number of fuzzing iterations
     * @param fuzzingProgress      the actually executed number of iterations
     */
    public ReportGenSummaryC(final AbstractR runnable,
                             final InetSocketAddress destination, final int interval,
                             final List<TempRecordFile> recordFiles, final Duration duration,
                             final int fuzzingTotalProgress, final int fuzzingProgress, // NOPMD
                             final int timeout) {
        super(runnable);
        this.destination = destination;
        this.interval = interval;
        this.recordFiles = recordFiles;
        this.duration = duration;
        this.fuzzingProgress = fuzzingProgress;
        this.fuzzingTotalProgress = fuzzingTotalProgress;
        this.timeout = timeout;
    }

    /**
     * Creates the summary table.
     *
     * @param document the DOM document
     */
    public void create(final Document document) { // NOPMD
        runnable.setStateMessage("i:Creating summary table ...",
                RunnableState.RUNNING);
        final Element heading = document.createElement("h2");
        heading.appendChild(document.createTextNode("Summary"));
        document.getElementsByTagName("body").item(0).appendChild(heading);
        final Element table = document.createElement("table");
        final Element[] trs = new Element[2];
        // Create all tr elements
        for (int i = 0; i < trs.length; i++) {
            trs[i] = document.createElement("tr");
        }
        final Element[] ths = new Element[6];
        // Create all th elements that contain the heading cells
        for (int i = 0; i < ths.length; i++) {
            ths[i] = document.createElement("th");
        }
        ths[0].appendChild(document.createTextNode("Destination"));
        ths[1].appendChild(document.createTextNode("Timeout in ms"));
        ths[2].appendChild(document.createTextNode("Interval in ms"));
        ths[3].appendChild(document.createTextNode("# Crashes"));
        ths[4].appendChild(document.createTextNode("# Iterations"));
        ths[5].appendChild(document.createTextNode("Duration"));
        // Append the th elements to the first tr element
        for (Element th : ths) {
            trs[0].appendChild(th);
        }
        final Element[] tds = new Element[6];
        // Create all td elements that contain the data
        for (int i = 0; i < tds.length; i++) {
            tds[i] = document.createElement("td");
            if (i > 0) {
                tds[i].setAttribute("class", "right");
            }
        }
        // Fill the td elements with their values
        tds[0].appendChild(document.createTextNode(destination.getHostString()
                + ":" + destination.getPort()));
        tds[1].appendChild(document.createTextNode(String.valueOf(timeout)));
        tds[2].appendChild(document.createTextNode(String.valueOf(interval)));
        tds[3].appendChild(document.createTextNode(String
                .valueOf(getNumOfCrashes())));
        tds[4].appendChild(document.createTextNode(fuzzingProgress + "/"
                + (fuzzingTotalProgress == -1 ? "inf" : fuzzingTotalProgress)));
        final DecimalFormat timeFormat = new DecimalFormat("00");
        tds[5].appendChild(document.createTextNode(timeFormat.format(duration
                .getHours())
                + ":"
                + timeFormat.format(duration.getMinutes())
                + ":" + timeFormat.format(duration.getSeconds())));
        // Append the td elements to the second tr element
        for (int i = 0; i < tds.length; i++) {
            trs[1].appendChild(tds[i]);
        }
        // Append the both tr elements to the table element
        for (int i = 0; i < trs.length; i++) {
            table.appendChild(trs[i]);
        }
        document.getElementsByTagName("body").item(0).appendChild(table);
        runnable.increaseProgress("s:done.", RunnableState.RUNNING);
    }

    /**
     * Gets the number of recorded crashes.
     *
     * @return the number of crashes
     */
    private int getNumOfCrashes() {
        int num = 0;
        for (TempRecordFile file : recordFiles) {
            if (file.isCrash()) {
                num++;
            }
        }
        return num;
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.component.AbstractComponent#getTotalProgress()
     */
    @Override
    public int getTotalProgress() {
        return 1;
    }

}
