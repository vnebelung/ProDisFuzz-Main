/*
 * This file is part of ProDisFuzz, modified on 03.10.13 22:25.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import model.InjectedProtocolPart;
import model.Model;
import model.SavedDataFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.datatype.Duration;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportProcess extends AbstractProcess {

    private final static String DIR_POSTFIX = "_records";
    private List<SavedDataFile> savedDataFiles;
    private InetSocketAddress target;
    private int interval;
    private List<InjectedProtocolPart> protocolParts;
    private Duration duration;
    private int workTotal;
    private int workProgress;
    private FuzzOptionsProcess.CommunicationSave saveCommunication;
    private int timeout;
    private boolean written;

    /**
     * Instantiates a new report process.
     */
    public ReportProcess() {
        super();
        savedDataFiles = new ArrayList<>();
    }

    @Override
    public void reset() {
        savedDataFiles.clear();
    }

    /**
     * Writes the final report to the given path.
     *
     * @param path the output path
     */
    public void write(final Path path) {
        final Path outputPath = path.toAbsolutePath().normalize();
        if (!Files.isDirectory(outputPath)) {
            Model.INSTANCE.getLogger().error("File path for saving final report invalid");
            written = false;
            spreadUpdate();
            return;
        }
        if (!Files.isWritable(outputPath)) {
            Model.INSTANCE.getLogger().error("File path for saving final report not writable");
            written = false;
            spreadUpdate();
            return;
        }
        // Determine the output name used for the file and folder name of the result
        final String outputName = findOutputName(outputPath);
        setOutputPath(outputName, outputPath);
        // Create the HTML report
        try {
            final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            document.appendChild(createHtml(document));
            // Write the DOM structure into the specified file
            final DOMSource source = new DOMSource(document);
            final StreamResult streamResult = new StreamResult(Files.newOutputStream(outputPath.resolve(outputName +
                    ".html")));
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            // Indent the elements in the XML structure by 2 spaces
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            // Add a doctype
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "-//W3C//DTD XHTML 1.0 Transitional//EN");
            transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
            // Transform the DOM TO XHTML
            transformer.transform(source, streamResult);
            // Write all crashes into their files
            final Path directory = outputPath.resolve(outputName + DIR_POSTFIX);
            // Create the directory
            Files.createDirectory(directory);
            // Write every recorded file in a file in the specified directory
            for (final SavedDataFile savedDataFile : savedDataFiles) {
                // Move the temporary file to the output directory
                Files.move(savedDataFile.getFilePath(), savedDataFile.getOutputPath());
            }
            written = true;
            spreadUpdate();
            Model.INSTANCE.getLogger().info("Report successfully generated and written to '" + outputPath.toString()
                    + "'");
        } catch (ParserConfigurationException | IOException |
                TransformerException e) {
            written = false;
            spreadUpdate();
            Model.INSTANCE.getLogger().error(e);
        }
    }

    /**
     * Creates the html node with all children.
     *
     * @param document the DOM document
     * @return the html node
     */
    private Element createHtml(final Document document) {
        final Element html = document.createElement("html");
        html.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
        html.setAttribute("xml:lang", "en");
        html.appendChild(createHead(document));
        html.appendChild(createBody(document));
        return html;
    }

    /**
     * Creates the head node with all children.
     *
     * @param document the DOM document
     * @return the head node
     */
    private Element createHead(final Document document) {
        final Element head = document.createElement("head");
        final Element title = document.createElement("title");
        title.appendChild(document.createTextNode("ProDisFuzz Results"));
        head.appendChild(title);
        // Create the meta element with the date of the generation
        final Element metaDate = document.createElement("meta");
        metaDate.setAttribute("name", "date");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        String date = dateFormat.format(new Date());
        // The time zone has to be separated by a colon
        date = date.substring(0, date.length() - 2) + ":" + date.substring(date.length() - 2);
        metaDate.setAttribute("content", date);
        head.appendChild(metaDate);
        head.appendChild(createCSS(document));
        return head;
    }

    /**
     * Creates the CSS elements.
     *
     * @param document the DOM document
     * @return the CSS node
     */
    private Element createCSS(final Document document) {
        final Element style = document.createElement("style");
        style.setAttribute("type", "text/css");
        style.appendChild(document.createTextNode("body { font-family: sans-serif; background-color: #ffffff; color: " +
                "" + "#000000; }"));
        style.appendChild(document.createTextNode(" th, td { padding: 0.2em 1em; text-align: left; }"));
        style.appendChild(document.createTextNode(" h1 span { color: #4526ae; }"));
        style.appendChild(document.createTextNode(" h2, h3 { background-color: #4526ae; color: #ffffff; padding: 0" +
                ".2em; }"));
        style.appendChild(document.createTextNode(" .crash { background-color: #ffc0c0; }"));
        style.appendChild(document.createTextNode(" .right { text-align: right; }"));
        return style;
    }

    /**
     * Creates the body element with all children.
     *
     * @param document the DOM document
     * @return the body node
     */
    private Element createBody(final Document document) {
        final Element body = document.createElement("body");
        final Element heading = document.createElement("h1");
        heading.appendChild(document.createTextNode("Pro"));
        final Element span = document.createElement("span");
        span.appendChild(document.createTextNode("Dis"));
        heading.appendChild(span);
        heading.appendChild(document.createTextNode("Fuzz Results"));
        body.appendChild(heading);
        final Element summaryHeading = document.createElement("h2");
        summaryHeading.appendChild(document.createTextNode("Summary"));
        body.appendChild(summaryHeading);
        body.appendChild(createSummary(document));
        final Element structureHeading = document.createElement("h2");
        structureHeading.appendChild(document.createTextNode("Protocol Structure"));
        body.appendChild(structureHeading);
        body.appendChild(createStructure(document));
        final Element crashesHeading = document.createElement("h2");
        crashesHeading.appendChild(document.createTextNode("Crashes"));
        body.appendChild(crashesHeading);
        body.appendChild(createCrashes(document));
        if (saveCommunication == FuzzOptionsProcess.CommunicationSave.ALL) {
            final Element communicationHeading = document.createElement("h2");
            communicationHeading.appendChild(document.createTextNode("Complete Communication"));
            body.appendChild(communicationHeading);
            body.appendChild(createCommunication(document));
        }
        return body;
    }

    /**
     * Creates the communication elements with all children.
     *
     * @param document the DOM document
     * @return the communication elements
     */
    private Element createCommunication(final Document document) {
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
        for (final Element th : ths) {
            trs[0].appendChild(th);
        }
        Element[][] tds = new Element[numOfIterations()][ths.length];
        // Create all td elements
        for (int i = 0; i < tds.length; i++) {
            for (int j = 0; j < tds[i].length; j++) {
                tds[i][j] = document.createElement("td");
            }
        }
        final Element[] as = new Element[savedDataFiles.size()];
        // Create all a elements
        for (int i = 0; i < as.length; i++) {
            as[i] = document.createElement("a");
        }
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        int count = 0;
        // For each crash fill the particular td and a elements with values
        for (int i = 0; i < savedDataFiles.size(); i++) {
            tds[count][0].appendChild(document.createTextNode(String.valueOf(count + 1)));
            // This is the link to the sent bytes file
            as[i].setAttribute("href", savedDataFiles.get(i).getOutputPath().toString());
            as[i].appendChild(document.createTextNode(savedDataFiles.get(i).getOutputPath().getFileName().toString()));
            // Append the a element to the first td element
            tds[count][1].appendChild(as[i]);
            if (savedDataFiles.get(i).isCrash()) {
                for (int j = 0; j < tds[count].length; j++) {
                    tds[count][j].setAttribute("class", "crash");
                }
                tds[count][2].appendChild(document.createTextNode("CRASHED"));
            } else {
                i++;
                as[i].setAttribute("href", savedDataFiles.get(i).getOutputPath().toString());
                as[i].appendChild(document.createTextNode(savedDataFiles.get(i).getOutputPath().getFileName()
                        .toString()));
                tds[count][2].appendChild(as[i]);
            }
            tds[count][3].appendChild(document.createTextNode(dateFormat.format(new Date(savedDataFiles.get(i)
                    .getSavedTime()))));
            count++;
        }
        // Append all td elements to the tr elements
        for (int i = 0; i < tds.length; i++) {
            for (int j = 0; j < tds[i].length; j++) {
                trs[i + 1].appendChild(tds[i][j]);
            }
        }
        // Append all tr elements to the table element
        for (final Element tr : trs) {
            table.appendChild(tr);
        }
        return table;
    }

    /**
     * Gets the number of iterations, that is half the number of recorded files +1 for each recorded crash.
     *
     * @return the number of iterations
     */
    private int numOfIterations() {
        int num = 0;
        for (final SavedDataFile file : savedDataFiles) {
            num++;
            if (file.isCrash()) {
                num++;
            }
        }
        return num / 2;
    }

    /**
     * Creates the crash overview elements with all children.
     *
     * @param document the DOM document
     * @return the crashes overview elements
     */
    private Element createCrashes(final Document document) {
        final List<SavedDataFile> crashes = filterCrashes(savedDataFiles);
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
        for (final Element th : ths) {
            trs[0].appendChild(th);
        }
        Element[][] tds = new Element[crashes.size()][ths.length];
        // Create all td elements
        for (int i = 0; i < tds.length; i++) {
            for (int j = 0; j < tds[i].length; j++) {
                tds[i][j] = document.createElement("td");
            }
        }
        final Element[] as = new Element[crashes.size()];
        // Create all a elements
        for (int i = 0; i < as.length; i++) {
            as[i] = document.createElement("a");
        }
        final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        // For each crash fill the particular td and a elements with values
        for (int i = 0; i < crashes.size(); i++) {
            tds[i][0].appendChild(document.createTextNode(String.valueOf(i + 1)));
            tds[i][1].appendChild(document.createTextNode(dateFormat.format(new Date(crashes.get(i).getSavedTime()))));
            // This is the link to the crash file
            as[i].setAttribute("href", crashes.get(i).getOutputPath().toString());
            as[i].appendChild(document.createTextNode(crashes.get(i).getOutputPath().getFileName().toString()));
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
        for (final Element tr : trs) {
            table.appendChild(tr);
        }
        // Append the table element to the body element
        return table;
    }

    /**
     * Filters the written data files for crashes.
     *
     * @param savedDataFiles the written data files
     * @return the data files that are crashes
     */
    private List<SavedDataFile> filterCrashes(final List<SavedDataFile> savedDataFiles) {
        final List<SavedDataFile> crashes = new ArrayList<>();
        for (final SavedDataFile savedDataFile : savedDataFiles) {
            if (savedDataFile.isCrash()) {
                crashes.add(savedDataFile);
            }
        }
        return crashes;
    }

    /**
     * Creates the protocol structure elements with all children.
     *
     * @param document the DOM document
     * @return the structure elements
     */
    private Element createStructure(final Document document) {
        final Element table = document.createElement("table");
        final Element[] trs = new Element[protocolParts.size() + 1];
        // Create all tr elements
        for (int i = 0; i < trs.length; i++) {
            trs[i] = document.createElement("tr");
        }
        final Element[] ths = new Element[4];
        // Create all th elements
        for (int i = 0; i < ths.length; i++) {
            ths[i] = document.createElement("th");
        }
        ths[0].appendChild(document.createTextNode("#"));
        ths[1].appendChild(document.createTextNode("Type"));
        ths[2].appendChild(document.createTextNode("Minimum Length"));
        ths[3].appendChild(document.createTextNode("Maximum Length"));
        // Append the th elements to the first tr element
        for (final Element th : ths) {
            trs[0].appendChild(th);
        }
        final Element[][] tds = new Element[protocolParts.size()][ths.length];
        // Create all td elements
        for (int i = 0; i < tds.length; i++) {
            for (int j = 0; j < tds[i].length; j++) {
                tds[i][j] = document.createElement("td");
            }
        }
        // For each part fill the td elements with values
        for (int i = 0; i < protocolParts.size(); i++) {
            tds[i][0].appendChild(document.createTextNode(String.valueOf(i + 1)));
            tds[i][1].appendChild(document.createTextNode(protocolParts.get(i).getProtocolPart().getType().toString()));
            tds[i][2].appendChild(document.createTextNode(String.valueOf(protocolParts.get(i).getProtocolPart()
                    .getMinLength())));
            tds[i][3].appendChild(document.createTextNode(String.valueOf(protocolParts.get(i).getProtocolPart()
                    .getMaxLength())));
        }
        // Append all td elements to the particular tr element
        for (int i = 0; i < tds.length; i++) {
            for (int j = 0; j < tds[i].length; j++) {
                trs[i + 1].appendChild(tds[i][j]);
            }
        }
        // Append all tr elements to the table element
        for (final Element tr : trs) {
            table.appendChild(tr);
        }
        return table;
    }

    /**
     * Creates the summary elements with all children.
     *
     * @param document the DOM document
     * @return the summary node
     */
    private Element createSummary(final Document document) {
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
        for (final Element th : ths) {
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
        tds[0].appendChild(document.createTextNode(target.getHostString() + ":" + target.getPort()));
        tds[1].appendChild(document.createTextNode(String.valueOf(timeout)));
        tds[2].appendChild(document.createTextNode(String.valueOf(interval)));
        tds[3].appendChild(document.createTextNode(String.valueOf(numOfCrashes())));
        tds[4].appendChild(document.createTextNode(workProgress + "/" + (workTotal == -1 ? "inf" : workTotal)));
        final DecimalFormat timeFormat = new DecimalFormat("00");
        tds[5].appendChild(document.createTextNode(timeFormat.format(duration.getHours()) + ":" + timeFormat.format
                (duration.getMinutes()) + ":" + timeFormat.format(duration.getSeconds())));
        // Append the td elements to the second tr element
        for (final Element td : tds) {
            trs[1].appendChild(td);
        }
        // Append the both tr elements to the table element
        for (final Element tr : trs) {
            table.appendChild(tr);
        }
        return table;
    }

    /**
     * Returns the number of written crashes.
     *
     * @return the number of crashes
     */
    private int numOfCrashes() {
        int crashes = 0;
        for (final SavedDataFile savedDataFile : savedDataFiles) {
            if (savedDataFile.isCrash()) {
                crashes++;
            }
        }
        return crashes;
    }

    /**
     * Finds an output name for the file and directory, that is not already in use.
     *
     * @return the name of the file and directory
     */
    private String findOutputName(final Path outputPath) {
        String outputName;
        int postfix = 0;
        Path file;
        Path directory;
        do {
            outputName = postfix == 0 ? "results" : "results(" + postfix + ")";
            file = outputPath.resolve(outputName + ".html");
            directory = outputPath.resolve(outputName + DIR_POSTFIX);
            postfix++;
        } while (Files.exists(file) || Files.isDirectory(directory));
        return outputName;
    }

    /**
     * Sets the output path of every written data file.
     *
     * @param outputPath the output path
     */
    private void setOutputPath(final String outputName, final Path outputPath) {
        // Set the file path for each detected crash
        int messageIteration = 0;
        int messageCount = 0;
        for (final SavedDataFile savedDataFile : savedDataFiles) {
            final Path path = outputPath.resolve(outputName + DIR_POSTFIX).resolve("record" + messageIteration + "-" +
                    messageCount + ".bytes");
            savedDataFile.setOutputPath(path.toString());
            if (savedDataFile.isCrash() == (messageCount == 0)) {
                messageIteration++;
                messageCount = 0;
            } else {
                messageCount = 1;
            }
        }
    }

    @Override
    public void init() {
        this.savedDataFiles = new ArrayList<>(Model.INSTANCE.getFuzzingProcess().getSavedDataFiles());
        this.duration = Model.INSTANCE.getFuzzingProcess().getDuration();
        this.target = new InetSocketAddress(Model.INSTANCE.getFuzzOptionsProcess().getTarget().getHostName(),
                Model.INSTANCE.getFuzzOptionsProcess().getTarget().getPort());
        this.interval = Model.INSTANCE.getFuzzOptionsProcess().getInterval();
        this.protocolParts = new ArrayList<>(Model.INSTANCE.getFuzzOptionsProcess().getInjectedProtocolParts());
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
