/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable;

import model.ProtocolPart;
import model.RunnableThread.RunnableState;
import model.TempRecordFile;
import model.runnable.component.*;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

import javax.xml.datatype.Duration;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.List;

/**
 * The Class ReportGenR implements the runnable which is responsible for the
 * generation of the report.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ReportGenR extends AbstractR {

    /**
     * The report generation prefix component.
     */
    final private ReportGenPrefixC reportGenPrefixC;

    /**
     * The report generation records component.
     */
    final private ReportGenRecordsC reportGenRecordsC;

    /**
     * The report generation document component.
     */
    final private ReportGenDocumentC reportGenDocumentC;

    /**
     * The report generation HTML component.
     */
    final private ReportGenHTMLC reportGenHtmlC;

    /**
     * The report generation heading component.
     */
    final private ReportGenHeadingC reportGenHeadingC;

    /**
     * The report generation summary component.
     */
    final private ReportGenSummaryC reportGenSummaryC;

    /**
     * The report generation protocol component.
     */
    final private ReportGenProtocolC reportGenProtocolC;

    /**
     * The report generation crash component.
     */
    final private ReportGenCrashC reportGenCrashC;

    /**
     * The report generation write DOM component.
     */
    final private ReportGenWriteDOMC reportGenWriteDOMC;

    /**
     * The report generation write records component.
     */
    final private ReportGenWriteRecordsC reportGenWriteRecordsC;

    /**
     * The report generation communication component.
     */
    final private ReportGenCommunicationC reportGenCommunicationC;

    /**
     * The crash folder postfix.
     */
    public final static String FOLDER_POSTFIX = "_records";

    /**
     * The save communication flag.
     */
    private boolean saveCommunication;

    /**
     * Instantiates a new collect runnable.
     *
     * @param recordFiles          the recorded files
     * @param outputPath           the output path
     * @param duration             the fuzzing duration
     * @param destination          the destination
     * @param interval             the fuzzing interval
     * @param parts                the protocol parts
     * @param fuzzingProgress      the fuzzing progress
     * @param fuzzingTotalProgress the fuzzing total work
     * @param saveCommunication    flag that indicates whether all communication shall be
     *                             recorded
     * @param timeout              the connection timeout
     */
    public ReportGenR(final List<TempRecordFile> recordFiles,
                      final Path outputPath, final Duration duration,
                      final InetSocketAddress destination, final int interval,
                      final List<ProtocolPart> parts, final int fuzzingProgress,
                      final int fuzzingTotalProgress, final boolean saveCommunication,
                      final int timeout) {
        super();
        this.saveCommunication = saveCommunication;
        reportGenPrefixC = new ReportGenPrefixC(this, outputPath);
        reportGenRecordsC = new ReportGenRecordsC(this, recordFiles,
                outputPath);
        reportGenDocumentC = new ReportGenDocumentC(this);
        reportGenHtmlC = new ReportGenHTMLC(this);
        reportGenHeadingC = new ReportGenHeadingC(this);
        reportGenSummaryC = new ReportGenSummaryC(this, destination, interval,
                recordFiles, duration, fuzzingTotalProgress, fuzzingProgress,
                timeout);
        reportGenProtocolC = new ReportGenProtocolC(this, parts);
        reportGenCrashC = new ReportGenCrashC(this, recordFiles);
        reportGenWriteDOMC = new ReportGenWriteDOMC(this, outputPath);
        reportGenWriteRecordsC = new ReportGenWriteRecordsC(this,
                outputPath, recordFiles);
        reportGenCommunicationC = new ReportGenCommunicationC(this, recordFiles);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        String output = "";
        if (!isInterrupted()) {
            // Determine the output name used for the file and folder name of
            // the result
            output = reportGenPrefixC.find();
            sleep(SLEEPING_TIME);
        }
        if (!isInterrupted()) {
            reportGenRecordsC.setPath(output);
            sleep(SLEEPING_TIME);
        }
        // Create the HTML report
        Document document = null; // NOPMD
        if (!isInterrupted()) {
            // Create the document with the DOM structure
            try {
                document = reportGenDocumentC.create();
            } catch (DOMException | ParserConfigurationException e) {
                interrupt("e:" + e.getMessage());
            }
            sleep(SLEEPING_TIME);
        }
        if (!isInterrupted()) {
            // Create the initial HTML element
            reportGenHtmlC.create(document);
            sleep(SLEEPING_TIME);
        }
        if (!isInterrupted()) {
            // Create the h1 element
            reportGenHeadingC.create(document);
            sleep(SLEEPING_TIME);
        }
        if (!isInterrupted()) {
            // Create the summary elements
            reportGenSummaryC.create(document);
            sleep(SLEEPING_TIME);
        }
        if (!isInterrupted()) {
            // Create the protocol structure overview
            reportGenProtocolC.create(document);
            sleep(SLEEPING_TIME);
        }
        if (!isInterrupted()) {
            // Create the crashes table
            reportGenCrashC.create(document);
            sleep(SLEEPING_TIME);
        }
        if (!isInterrupted() && saveCommunication) {
            // Create the communication table
            reportGenCommunicationC.create(document);
            sleep(SLEEPING_TIME);
        }
        if (!isInterrupted()) {
            try {
                // Write the DOM structure into the specified file
                reportGenWriteDOMC.write(document, output);
                sleep(SLEEPING_TIME);
                // Write all crashes into their files
                reportGenWriteRecordsC.write(output);
            } catch (TransformerFactoryConfigurationError
                    | TransformerException | IOException e) {
                interrupt("e:" + e.getMessage());
            }
        }
        spreadUpdate(isInterrupted() ? RunnableState.CANCELED
                : RunnableState.FINISHED);
    }

    @Override
    protected void setTotalProgress() {
        totalProgress = reportGenPrefixC.getTotalProgress();
        totalProgress += reportGenRecordsC.getTotalProgress();
        totalProgress += reportGenDocumentC.getTotalProgress();
        totalProgress += reportGenHtmlC.getTotalProgress();
        totalProgress += reportGenHeadingC.getTotalProgress();
        totalProgress += reportGenSummaryC.getTotalProgress();
        totalProgress += reportGenProtocolC.getTotalProgress();
        totalProgress += reportGenCrashC.getTotalProgress();
        totalProgress += reportGenWriteDOMC.getTotalProgress();
        totalProgress += reportGenWriteRecordsC.getTotalProgress();
        if (saveCommunication) {
            totalProgress += reportGenCommunicationC.getTotalProgress();
        }
    }

}
