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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The Class ReportGenCrashC implements the functionality to generate the crash
 * table.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ReportGenCrashC extends AbstractC { // NOPMD

    /**
     * The recorded crashes.
     */
    private final List<TempRecordFile> crashes;

    /**
     * Instantiates a new report generation crash component.
     *
     * @param runnable    the runnable
     * @param recordFiles the recorded files
     */
    public ReportGenCrashC(final AbstractR runnable,
                           final List<TempRecordFile> recordFiles) {
        super(runnable);
        crashes = getRecordedCrashes(recordFiles);
    }

    /**
     * Creates the crash table.
     */
    public void create(final Document document) { // NOPMD
        runnable.setStateMessage("i:Creating crash table ...",
                RunnableState.RUNNING);
        final Element heading = document.createElement("h2");
        heading.appendChild(document.createTextNode("Crashes"));
        document.getElementsByTagName("body").item(0).appendChild(heading);
        final Element table = document.createElement("table");
        Element[] trs = new Element[crashes.size() + 1];
        // Create all tr elements
        for (int i = 0; i < trs.length; i++) {
            trs[i] = document.createElement("tr");
        }
        Element[] ths = new Element[3];
        // Create all th elements and fill them with values
        for (int i = 0; i < ths.length; i++) {
            ths[i] = document.createElement("th");
        }
        ths[0].appendChild(document.createTextNode("# Crash"));
        ths[1].appendChild(document.createTextNode("Time"));
        ths[2].appendChild(document.createTextNode("Message"));
        // Append all th elements to the first tr element
        for (int i = 0; i < ths.length; i++) {
            trs[0].appendChild(ths[i]);
        }
        Element[][] tds = new Element[crashes.size()][ths.length];
        // Create all td elements
        for (int i = 0; i < tds.length; i++) {
            for (int j = 0; j < tds[i].length; j++) {
                tds[i][j] = document.createElement("td");
            }
        }
        final Element[] as = new Element[crashes.size()]; // NOPMD
        // Create all a elements
        for (int i = 0; i < as.length; i++) {
            as[i] = document.createElement("a");
        }
        final DateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        // For each crash fill the particular td and a elements with values
        for (int i = 0; i < crashes.size(); i++) {
            tds[i][0]
                    .appendChild(document.createTextNode(String.valueOf(i + 1)));
            tds[i][1].appendChild(document.createTextNode(dateFormat
                    .format(new Date(crashes.get(i).getTime())))); // NOPMD
            // This is the link to the crash file
            as[i].setAttribute("href", crashes.get(i).getOutputPath()
                    .toString());
            as[i].appendChild(document.createTextNode(crashes.get(i)
                    .getOutputPath().getFileName().toString()));
            // Append the a element to the third td element
            tds[i][2].appendChild(as[i]);
        }
        // Append all td elements to the tr elements
        for (int i = 0; i < tds.length; i++) {
            for (int j = 0; j < tds[i].length; j++) {
                trs[i + 1].appendChild(tds[i][j]);
            }
        }
        // Append all tr elements to the table element
        for (int i = 0; i < trs.length; i++) {
            table.appendChild(trs[i]);
        }
        // Append the table element to the body element
        document.getElementsByTagName("body").item(0).appendChild(table);
        runnable.increaseProgress("s:done.", RunnableState.RUNNING);
    }

    /**
     * Gets the crashes out of the recorded files
     *
     * @param recordFiles the recorded files
     * @return the recorded crashes
     */
    private List<TempRecordFile> getRecordedCrashes(
            final List<TempRecordFile> recordFiles) {
        final List<TempRecordFile> crashes = new ArrayList<TempRecordFile>();
        for (TempRecordFile file : recordFiles) {
            if (file.isCrash()) {
                crashes.add(file);
            }
        }
        return crashes;
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
