/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.report;

import model.process.fuzzoptions.Process.RecordingMethod;
import model.protocol.InjectedProtocolStructure;
import model.record.Recordings;
import nu.xom.Attribute;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;

import static model.process.report.Process.NAMESPACE;

/**
 * This class is a callable responsible for creating the XML structure of the HTML report.
 */
class DocumentCreator implements Callable<Document> {

    private final int timeout;
    private final int interval;
    private final Duration duration;
    private final int workProgress;
    private RecordingMethod recordingMethod;
    private Recordings recordings;
    private InjectedProtocolStructure injectedProtocolStructure;
    private InetSocketAddress target;
    private int workTotal;

    /**
     * Constructs the callable.
     *
     * @param recordings                the communication recordings
     * @param duration                  the fuzzing duration
     * @param target                    the tested target
     * @param interval                  the fuzzing interval
     * @param injectedProtocolStructure the injected protocol structure used for fuzzing
     * @param workProgress              the processed fuzzign iterations
     * @param workTotal                 the total amount of fuzzing iterations
     * @param recordingMethod           the method used for recordings
     * @param timeout                   the timeout of a successful crash
     */
    public DocumentCreator(RecordingMethod recordingMethod, Recordings recordings,
                           InjectedProtocolStructure injectedProtocolStructure, InetSocketAddress target, int timeout,
                           int interval, Duration duration, int workProgress, int workTotal) {
        this.recordingMethod = recordingMethod;
        this.recordings = recordings;
        this.injectedProtocolStructure = injectedProtocolStructure;
        this.target = target;
        this.timeout = timeout;
        this.interval = interval;
        this.duration = duration;
        this.workProgress = workProgress;
        this.workTotal = workTotal;
    }

    /**
     * Creates the head element including its children.
     *
     * @return the head element
     */
    private static Element createHead() {
        //noinspection HardCodedStringLiteral
        Element result = new Element("head", NAMESPACE);

        //noinspection HardCodedStringLiteral
        Element title = new Element("title", NAMESPACE);
        title.appendChild("ProDisFuzz Results");
        result.appendChild(title);

        // Create the meta element with the date of the generation
        //noinspection HardCodedStringLiteral
        Element metaDate = new Element("meta", NAMESPACE);
        // noinspection HardCodedStringLiteral
        metaDate.addAttribute(new Attribute("name", "date"));
        ZonedDateTime zonedDateTime = ZonedDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        //noinspection HardCodedStringLiteral
        metaDate.addAttribute(new Attribute("content", zonedDateTime.toOffsetDateTime().toString()));
        result.appendChild(metaDate);

        result.appendChild(createCss());

        return result;
    }

    /**
     * Creates the CSS elements.
     *
     * @return the CSS element
     */
    private static Element createCss() {
        //noinspection HardCodedStringLiteral
        Element style = new Element("style", NAMESPACE);
        //noinspection HardCodedStringLiteral
        style.addAttribute(new Attribute("type", "text/css"));
        //noinspection HardCodedStringLiteral
        style.appendChild("body { font-family: sans-serif; background-color: #ffffff; color: #000000; }");
        //noinspection HardCodedStringLiteral
        style.appendChild(" th, td { padding: 0.2em 1em; text-align: left; }");
        //noinspection HardCodedStringLiteral
        style.appendChild(" h2, h3 { background-color: #6060a0; color: #ffffff; padding: 0.2em; }");
        //noinspection HardCodedStringLiteral
        style.appendChild(" .crash { background-color: #ffc0c0; }");
        //noinspection HardCodedStringLiteral
        style.appendChild(" .right { text-align: right; }");
        return style;
    }

    @Override
    public Document call() {
        Document result = new Document(createHtmlRoot());
        //noinspection HardCodedStringLiteral
        DocType doctype = new DocType("html", "-//W3C//DTD XHTML 1.0 Strict//EN",
                "http://www.w3" + ".org/TR/xhtml1/DTD/xhtml1-strict.dtd");
        result.insertChild(doctype, 0);
        return result;
    }

    /**
     * Creates the HTML element including its children.
     *
     * @return the HTML element
     */
    private Element createHtmlRoot() {
        //noinspection HardCodedStringLiteral
        Element result = new Element("html", NAMESPACE);
        result.appendChild(createHead());
        result.appendChild(createBody());
        return result;
    }

    /**
     * Creates the body element including its children.
     *
     * @return the body element
     */
    private Element createBody() {
        //noinspection HardCodedStringLiteral
        Element result = new Element("body", NAMESPACE);

        //noinspection HardCodedStringLiteral
        Element h1 = new Element("h1", NAMESPACE);
        h1.appendChild("ProDisFuzz Results");
        result.appendChild(h1);

        //noinspection HardCodedStringLiteral
        Element h2Summary = new Element("h2", NAMESPACE);
        h2Summary.appendChild("Summary");
        result.appendChild(h2Summary);
        result.appendChild(createSummary());

        //noinspection HardCodedStringLiteral
        Element h2protocol = new Element("h2", NAMESPACE);
        h2protocol.appendChild("Protocol Structure");
        result.appendChild(h2protocol);
        result.appendChild(createStructure());

        //noinspection HardCodedStringLiteral
        Element h2crashes = new Element("h2", NAMESPACE);
        h2crashes.appendChild("Crashes");
        result.appendChild(h2crashes);
        result.appendChild(createCrashes());

        if (recordingMethod == RecordingMethod.ALL) {
            //noinspection HardCodedStringLiteral
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
            Path path = recordings.getRecord(record).getOutputPath();
            result.setLink(i + 1, 1, path.subpath(path.getNameCount() - 2, path.getNameCount()).toString(),
                    path.getFileName().toString());
            if (recordings.getRecord(record).isCrash()) {
                result.setText(i + 1, 2, "CRASHED");
                for (int j = 0; j < 4; j++) {
                    //noinspection HardCodedStringLiteral
                    result.setAttribute(i + 1, j, "class", "crash");
                }
            } else {
                record++;
                path = recordings.getRecord(record).getOutputPath();
                result.setLink(i + 1, 2, path.subpath(path.getNameCount() - 2, path.getNameCount()).toString(),
                        path.getFileName().toString());
            }
            ZonedDateTime zonedDateTime =
                    ZonedDateTime.from(recordings.getRecord(i).getSavedTime().atZone(ZoneId.systemDefault()))
                            .truncatedTo(ChronoUnit.SECONDS);
            result.setText(i + 1, 3, zonedDateTime.toOffsetDateTime().toString());
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
            ZonedDateTime zonedDateTime =
                    ZonedDateTime.from(recordings.getCrashRecord(i).getSavedTime().atZone(ZoneId.systemDefault()))
                            .truncatedTo(ChronoUnit.SECONDS);
            result.setText(i + 1, 1, zonedDateTime.toOffsetDateTime().toString());
            Path path = recordings.getCrashRecord(i).getOutputPath();
            result.setLink(i + 1, 2, path.subpath(path.getNameCount() - 2, path.getNameCount()).toString(),
                    path.getFileName().toString());

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
            result.setText(i, 1, injectedProtocolStructure.getBlock(i - 1).getType().toString());
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
            //noinspection HardCodedStringLiteral
            result.setAttribute(1, i, "class", "right");
        }

        // Fill the td elements with their values
        result.setText(1, 0, target.getHostString() + ':' + target.getPort());
        result.setText(1, 1, String.valueOf(timeout));
        result.setText(1, 2, String.valueOf(interval));
        result.setText(1, 3, String.valueOf(recordings.getCrashSize()));
        result.setText(1, 4, workProgress + "/" + (workTotal == -1 ? "inf" : workTotal));
        DecimalFormat hourFormat = new DecimalFormat("000");
        DecimalFormat minutesSecondsFormat = new DecimalFormat("00");
        result.setText(1, 5,
                hourFormat.format(duration.toHours()) + ':' + minutesSecondsFormat.format(duration.toMinutes() % 60) +
                        ':' + minutesSecondsFormat.format(duration.getSeconds() % 60));

        return result.getTable();
    }

}
