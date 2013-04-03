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
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * The Class ReportGenCommunicationC implements the functionality to generate
 * the communication table.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ReportGenCommunicationC extends AbstractC { // NOPMD

    /**
     * The recorded files.
     */
    private final List<TempRecordFile> recordFiles;

    /**
     * Instantiates a new report generation communication component.
     *
     * @param runnable    the runnable
     * @param recordFiles the recorded files
     */
    public ReportGenCommunicationC(final AbstractR runnable,
                                   final List<TempRecordFile> recordFiles) {
        super(runnable);
        this.recordFiles = recordFiles;
    }

    /**
     * Creates the summary table.
     *
     * @param document the DOM document
     */
    public void create(final Document document) { // NOPMD
        runnable.setStateMessage("i:Creating records table ...",
                RunnableState.RUNNING);
        final Element heading = document.createElement("h2");
        heading.appendChild(document
                .createTextNode("Complete communication bytes"));
        document.getElementsByTagName("body").item(0).appendChild(heading);
        final Element table = document.createElement("table");
        Element[] trs = new Element[numOfIterations() + 1];
        // Create all tr elements
        for (int i = 0; i < trs.length; i++) {
            trs[i] = document.createElement("tr");
        }
        Element[] ths = new Element[4];
        // Create all th elements and fill them with values
        for (int i = 0; i < ths.length; i++) {
            ths[i] = document.createElement("th");
        }
        ths[0].appendChild(document.createTextNode("#"));
        ths[1].appendChild(document.createTextNode("Bytes sent"));
        ths[2].appendChild(document.createTextNode("Bytes received"));
        ths[3].appendChild(document.createTextNode("Time"));
        // Append all th elements to the first tr element
        for (int i = 0; i < ths.length; i++) {
            trs[0].appendChild(ths[i]);
        }
        Element[][] tds = new Element[numOfIterations()][ths.length];
        // Create all td elements
        for (int i = 0; i < tds.length; i++) {
            for (int j = 0; j < tds[i].length; j++) {
                tds[i][j] = document.createElement("td");
            }
        }
        final Element[] as = new Element[recordFiles.size()]; // NOPMD
        // Create all a elements
        for (int i = 0; i < as.length; i++) {
            as[i] = document.createElement("a");
        }
        final DateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        int count = 0;
        // For each crash fill the particular td and a elements with values
        for (int i = 0; i < recordFiles.size(); i++) {
            tds[count][0].appendChild(document.createTextNode(String
                    .valueOf(count + 1)));
            // This is the link to the sent bytes file
            as[i].setAttribute("href", recordFiles.get(i).getOutputPath()
                    .toString());
            as[i].appendChild(document.createTextNode(recordFiles.get(i)
                    .getOutputPath().getFileName().toString()));
            // Append the a element to the first td element
            tds[count][1].appendChild(as[i]);
            if (recordFiles.get(i).isCrash()) {
                for (int j = 0; j < tds[count].length; j++) {
                    tds[count][j].setAttribute("class", "crash");
                }
                tds[count][2].appendChild(document.createTextNode("CRASHED"));
            } else {
                i++;
                as[i].setAttribute("href", recordFiles.get(i).getOutputPath()
                        .toString());
                as[i].appendChild(document.createTextNode(recordFiles.get(i)
                        .getOutputPath().getFileName().toString()));
                tds[count][2].appendChild(as[i]);
            }
            tds[count][3].appendChild(document.createTextNode(dateFormat
                    .format(new Date(recordFiles.get(i).getTime())))); // NOPMD
            count++;
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
     * Gets the number of iterations, that is half the number of recorded files
     * +1 for each recorded crash.
     *
     * @return the number of iterations
     */
    private int numOfIterations() {
        int num = 0;
        for (TempRecordFile file : recordFiles) {
            num++;
            if (file.isCrash()) {
                num++;
            }
        }
        return num / 2;
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
