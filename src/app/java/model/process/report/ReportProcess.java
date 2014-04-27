/*
 * This file is part of ProDisFuzz, modified on 03.04.14 20:36.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.report;

import model.Model;
import model.process.AbstractProcess;
import model.process.fuzzOptions.FuzzOptionsProcess;
import model.protocol.InjectedProtocolStructure;
import model.record.Recordings;
import model.xml.XmlExchange;
import nu.xom.Attribute;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;

import javax.xml.datatype.Duration;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportProcess extends AbstractProcess {

    private final static String DIR_POSTFIX = "_records";
    public final static String NAMESPACE = "http://www.w3.org/1999/xhtml";
    private Recordings recordings;
    private InetSocketAddress target;
    private int interval;
    private InjectedProtocolStructure injectedProtocolStructure;
    private Duration duration;
    private int workTotal;
    private int workProgress;
    private FuzzOptionsProcess.CommunicationSave saveCommunication;
    private int timeout;
    private boolean written;

    /**
     * Instantiates a new process responsible for generating the final report with all fuzzing results.
     */
    public ReportProcess() {
        super();
        recordings = new Recordings();
    }

    @Override
    public void reset() {
        recordings.clear();
    }

    /**
     * Writes the final report to the given path.
     *
     * @param path the output path
     */
    public void write(Path path) {
        // Determine the output name used for the file and folder name of the result
        String outputName = findOutputName(path);
        setOutputPath(outputName, path);

        Document document = new Document(createHtmlRoot());
        DocType doctype = new DocType("html", "-//W3C//DTD XHTML 1.0 Strict//EN",
                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
        document.insertChild(doctype, 0);
        Path directory = path.resolve(outputName + DIR_POSTFIX);
        // Create the directory
        try {
            Files.createDirectory(directory);
            // Write every recorded file in a file in the specified directory
            for (int i = 0; i < recordings.getSize(); i++) {
                // Move the temporary file to the output directory
                Files.move(recordings.getRecord(i).getFilePath(), recordings.getRecord(i).getOutputPath());
            }
            written = XmlExchange.exportXML(document, path.resolve(outputName + ".html"));
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error(e);
            written = false;
        }
        spreadUpdate();
    }

    /**
     * Creates the html element including its children.
     *
     * @return the html element
     */
    private Element createHtmlRoot() {
        Element result = new Element("html", NAMESPACE);
        result.appendChild(createHead());
        result.appendChild(createBody());
        return result;
    }

    /**
     * Creates the head element including its children.
     *
     * @return the head element
     */
    private Element createHead() {
        Element result = new Element("head", NAMESPACE);

        Element title = new Element("title", NAMESPACE);
        title.appendChild("ProDisFuzz Results");
        result.appendChild(title);

        // Create the meta element with the date of the generation
        Element metaDate = new Element("meta", NAMESPACE);
        metaDate.addAttribute(new Attribute("name", "date"));
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        String date = dateFormat.format(new Date());
        // The time zone has to be separated by a colon
        date = date.substring(0, date.length() - 2) + ":" + date.substring(date.length() - 2);
        metaDate.addAttribute(new Attribute("content", date));
        result.appendChild(metaDate);

        result.appendChild(createCss());

        return result;
    }

    /**
     * Creates the CSS elements.
     *
     * @return the CSS element
     */
    private Element createCss() {
        Element style = new Element("style", NAMESPACE);
        style.addAttribute(new Attribute("type", "text/css"));
        style.appendChild("body { font-family: sans-serif; background-color: #ffffff; color: #000000; }");
        style.appendChild(" th, td { padding: 0.2em 1em; text-align: left; }");
        style.appendChild(" h2, h3 { background-color: #6060a0; color: #ffffff; padding: 0.2em; }");
        style.appendChild(" .crash { background-color: #ffc0c0; }");
        style.appendChild(" .right { text-align: right; }");
        return style;
    }

    /**
     * Creates the body element including its children.
     *
     * @return the body element
     */
    private Element createBody() {
        Element result = new Element("body", NAMESPACE);

        Element h1 = new Element("h1", NAMESPACE);
        h1.appendChild("ProDisFuzz Results");
        result.appendChild(h1);

        Element h2Summary = new Element("h2", NAMESPACE);
        h2Summary.appendChild("Summary");
        result.appendChild(h2Summary);
        result.appendChild(createSummary());

        Element h2protocol = new Element("h2", NAMESPACE);
        h2protocol.appendChild("Protocol Structure");
        result.appendChild(h2protocol);
        result.appendChild(createStructure());

        Element h2crashes = new Element("h2", NAMESPACE);
        h2crashes.appendChild("Crashes");
        result.appendChild(h2crashes);
        result.appendChild(createCrashes());

        if (saveCommunication == FuzzOptionsProcess.CommunicationSave.ALL) {
            Element h2communication = new Element("h2", NAMESPACE);
            h2communication.appendChild("Complete Communication");
            result.appendChild(h2communication);
            result.appendChild(createCommunication());
        }
        return result;
    }

    /**
     * Creates the communication element including its children.
     *
     * @return the communication element
     */
    private Element createCommunication() {
        int iterations = iterations();
        HtmlTable result = new HtmlTable(iterations + 1, 4);

        result.setText(0, 0, "#");
        result.setText(0, 1, "Bytes sent");
        result.setText(0, 2, "Bytes received");
        result.setText(0, 3, "Time");

        // Fill the table with data for each communication record
        int record = 0;
        for (int i = 0; i < iterations; i++) {
            result.setText(i + 1, 0, String.valueOf(i + 1));
            result.setLink(i + 1, 1, recordings.getRecord(record).getOutputPath().toString(),
                    recordings.getRecord(record).getOutputPath().getFileName().toString());
            if (recordings.getRecord(record).isCrash()) {
                result.setText(i + 1, 2, "CRASHED");
                for (int j = 0; j < 4; j++) {
                    result.setAttribute(i + 1, j, "class", "crash");
                }
            } else {
                record++;
                result.setLink(i + 1, 2, recordings.getRecord(record).getOutputPath().toString(),
                        recordings.getRecord(record).getOutputPath().getFileName().toString());
            }
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            result.setText(i + 1, 3, dateFormat.format(new Date(recordings.getRecord(i).getSavedTime())));
            record++;
        }
        return result.getTable();
    }

    /**
     * Returns the number of fuzzing iterations, that is half the number of recorded files +1 for each recorded crash.
     *
     * @return the number of iterations
     */
    private int iterations() {
        int result = 0;
        for (int i = 0; i < recordings.getSize(); i++) {
            result++;
            if (recordings.getRecord(i).isCrash()) {
                result++;
            }
        }
        return result / 2;
    }

    /**
     * Creates the crash overview element including its children.
     *
     * @return the crashes overview element
     */
    private Element createCrashes() {
        HtmlTable result = new HtmlTable(recordings.getCrashSize() + 1, 3);

        result.setText(0, 0, "# Crash");
        result.setText(0, 1, "Time");
        result.setText(0, 2, "Message");

        // Fill the table with data for each crash
        for (int i = 0; i < recordings.getCrashSize(); i++) {
            result.setText(i + 1, 0, String.valueOf(i + 1));
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            result.setText(i + 1, 1, dateFormat.format(new Date(recordings.getCrashRecord(i).getSavedTime())));
            result.setLink(i + 1, 2, recordings.getCrashRecord(i).getOutputPath().toString(),
                    recordings.getCrashRecord(i).getOutputPath().getFileName().toString());

        }
        return result.getTable();
    }

    /**
     * Creates the protocol structure element including its children.
     *
     * @return the structure element
     */
    private Element createStructure() {
        HtmlTable result = new HtmlTable(injectedProtocolStructure.getSize() + 1, 4);

        result.setText(0, 0, "#");
        result.setText(0, 1, "Type");
        result.setText(0, 2, "Minimum Length");
        result.setText(0, 3, "Maximum Length");

        // For each protocol block fill the td elements with values
        for (int i = 1; i < injectedProtocolStructure.getSize(); i++) {
            result.setText(i, 0, String.valueOf(i));
            result.setText(i, 1, (injectedProtocolStructure.getBlock(i - 1).getType().toString()));
            result.setText(i, 2, String.valueOf(injectedProtocolStructure.getBlock(i - 1).getMinLength()));
            result.setText(i, 3, String.valueOf(injectedProtocolStructure.getBlock(i - 1).getMaxLength()));
        }
        return result.getTable();
    }

    /**
     * Creates the summary elements including its children.
     *
     * @return the summary element
     */
    private Element createSummary() {
        HtmlTable result = new HtmlTable(2, 6);

        result.setText(0, 0, "Destination");
        result.setText(0, 1, "Timeout in ms");
        result.setText(0, 2, "Interval in ms");
        result.setText(0, 3, "# Crashes");
        result.setText(0, 4, "# Iterations");
        result.setText(0, 5, "Duration");

        for (int i = 1; i < 6; i++) {
            result.setAttribute(1, i, "class", "right");
        }

        // Fill the td elements with their values
        result.setText(1, 0, target.getHostString() + ":" + target.getPort());
        result.setText(1, 1, String.valueOf(timeout));
        result.setText(1, 2, String.valueOf(interval));
        result.setText(1, 3, String.valueOf(recordings.getCrashSize()));
        result.setText(1, 4, workProgress + "/" + (workTotal == -1 ? "inf" : workTotal));
        DecimalFormat timeFormat = new DecimalFormat("00");
        result.setText(1, 5, timeFormat.format(duration.getHours()) + ":" + timeFormat.format(duration.getMinutes())
                + ":" + timeFormat.format(duration.getSeconds()));

        return result.getTable();
    }

    /**
     * Finds an output name for the report file and directory, that is not already in use.
     *
     * @param path the output directory
     * @return the name of the file and directory
     */
    private String findOutputName(Path path) {
        String outputName;
        int postfix = 0;
        Path file;
        Path directory;
        do {
            outputName = postfix == 0 ? "results" : "results(" + postfix + ")";
            file = path.resolve(outputName + ".html");
            directory = path.resolve(outputName + DIR_POSTFIX);
            postfix++;
        } while (Files.exists(file) || Files.isDirectory(directory));
        return outputName;
    }

    /**
     * Sets the output path of every written data file.
     *
     * @param path the output path
     * @param name the user-defined output name
     */
    private void setOutputPath(String name, Path path) {
        // Set the file path for each detected crash
        int messageIteration = 0;
        int messageCount = 0;
        for (int i = 0; i < recordings.getSize(); i++) {
            Path outputPath = path.resolve(name + DIR_POSTFIX).resolve("record" + messageIteration + "-" +
                    messageCount + ".bytes");
            recordings.getRecord(i).setOutputPath(outputPath);
            if (recordings.getRecord(i).isCrash() == (messageCount == 0)) {
                messageIteration++;
                messageCount = 0;
            } else {
                messageCount = 1;
            }
        }
    }

    @Override
    public void init() {
        this.recordings = Model.INSTANCE.getFuzzingProcess().getRecordings();
        this.duration = Model.INSTANCE.getFuzzingProcess().getDuration();
        this.target = new InetSocketAddress(Model.INSTANCE.getFuzzOptionsProcess().getTarget().getHostName(),
                Model.INSTANCE.getFuzzOptionsProcess().getTarget().getPort());
        this.interval = Model.INSTANCE.getFuzzOptionsProcess().getInterval();
        this.injectedProtocolStructure = Model.INSTANCE.getFuzzOptionsProcess().getInjectedProtocolStructure();
        this.workProgress = Model.INSTANCE.getFuzzingProcess().getWorkProgress();
        this.workTotal = Model.INSTANCE.getFuzzingProcess().getWorkTotal();
        this.saveCommunication = Model.INSTANCE.getFuzzOptionsProcess().getSaveCommunication();
        this.timeout = Model.INSTANCE.getFuzzOptionsProcess().getTimeout();
    }

    /**
     * Returns whether the report is successfully written.
     *
     * @return true if the report is written
     */
    public boolean isWritten() {
        return written;
    }

}
