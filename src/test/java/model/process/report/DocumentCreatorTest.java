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
import model.protocol.ProtocolStructure;
import model.record.Recordings;
import model.util.XmlWhiteSpaceEliminator;
import nu.xom.*;
import nu.xom.canonical.Canonicalizer;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("HardCodedStringLiteral")
public class DocumentCreatorTest {

    private Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{2}:\\d{2}");
    private Recordings recordings;
    private InjectedProtocolStructure injectedProtocolStructure;
    private InetSocketAddress target = new InetSocketAddress("example.net", 999);

    @BeforeClass
    public void setUp() throws URISyntaxException, IOException {
        recordings = new Recordings();
        byte[] bytes = {0, 17, 34};
        recordings.addRecording(bytes, false, Instant.parse("2000-01-01T00:11:22Z"));
        recordings.addRecording(bytes, false, Instant.parse("2000-01-01T00:11:22Z"));
        recordings.addRecording(bytes, true, Instant.parse("2000-01-01T00:11:22Z"));
        recordings.addRecording(bytes, true, Instant.parse("2000-01-01T00:11:22Z"));

        recordings.getRecord(0).setOutputPath(Paths.get("results_records/record0-0.bytes"));
        recordings.getRecord(1).setOutputPath(Paths.get("results_records/record0-1.bytes"));
        recordings.getRecord(2).setOutputPath(Paths.get("results_records/record1-0.bytes"));
        recordings.getRecord(3).setOutputPath(Paths.get("results_records/record2-0.bytes"));

        ProtocolStructure protocolStructure = new ProtocolStructure();
        List<Byte> bytes1 = new ArrayList<>(2);
        bytes1.add((byte) 0);
        bytes1.add((byte) 17);
        protocolStructure.addBlock(bytes1);
        List<Byte> bytes2 = new ArrayList<>(3);
        bytes2.add(null);
        bytes2.add(null);
        bytes2.add(null);
        protocolStructure.addBlock(bytes2);
        List<Byte> bytes3 = new ArrayList<>(1);
        bytes3.add((byte) 0);
        protocolStructure.addBlock(bytes3);
        List<Byte> bytes4 = new ArrayList<>(1);
        bytes4.add(null);
        protocolStructure.addBlock(bytes4);
        List<Byte> bytes5 = new ArrayList<>(3);
        bytes5.add((byte) 0);
        bytes5.add((byte) 17);
        bytes5.add((byte) 34);
        protocolStructure.addBlock(bytes5);
        injectedProtocolStructure = new InjectedProtocolStructure(protocolStructure);
        injectedProtocolStructure.getVarBlock(0).setLibraryInjection();
        injectedProtocolStructure.getVarBlock(0).setLibrary(Paths.get(getClass().getResource("/library1.txt").toURI()));
    }

    @AfterMethod
    public void tearDown() throws IOException {
        for (int i = 0; i < recordings.getSize(); i++) {
            Files.deleteIfExists(recordings.getRecord(i).getFilePath());
        }
    }

    @Test
    public void testCall() throws IOException, URISyntaxException {
        DocumentCreator documentCreator =
                new DocumentCreator(RecordingMethod.ALL, recordings, injectedProtocolStructure, target, 10, 20,
                        Duration.ofSeconds(3723), 2, 3);
        Document document = documentCreator.call();
        replaceDateTimes(document.getRootElement());
        String actual;
        try (OutputStream outputStream = new ByteArrayOutputStream()) {
            Canonicalizer canonicalizer = new Canonicalizer(outputStream, Canonicalizer.EXCLUSIVE_XML_CANONICALIZATION);
            canonicalizer.write(document);
            actual = outputStream.toString();
        }

        String reference = null;
        //noinspection OverlyBroadCatchBlock
        try (OutputStream outputStream = new ByteArrayOutputStream()) {
            Builder parser = new Builder(new XmlWhiteSpaceEliminator());
            Document referenceDoc = parser.build(Paths.get(getClass().getResource("/results.html").toURI()).toFile());
            // TODO: Ugly, but it seems the SAX parser adds attributes to XML nodes during parsing
            cleanAttributes(referenceDoc.getRootElement());
            Canonicalizer canonicalizer = new Canonicalizer(outputStream, Canonicalizer.EXCLUSIVE_XML_CANONICALIZATION);
            canonicalizer.write(referenceDoc);
            reference = outputStream.toString();
        } catch (ParsingException | IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(actual, reference);
    }

    private static void cleanAttributes(Element element) {
        if (element.getAttribute("space", "http://www.w3.org/XML/1998/namespace") != null) {
            element.removeAttribute(element.getAttribute("space", "http://www.w3.org/XML/1998/namespace"));
        }
        if (element.getAttribute("colspan") != null) {
            element.removeAttribute(element.getAttribute("colspan"));
        }
        if (element.getAttribute("rowspan") != null) {
            element.removeAttribute(element.getAttribute("rowspan"));
        }
        if (element.getAttribute("shape") != null) {
            element.removeAttribute(element.getAttribute("shape"));
        }
        for (int i = 0; i < element.getChildElements().size(); i++) {
            cleanAttributes(element.getChildElements().get(i));
        }
    }

    private void replaceDateTimes(Node node) {
        //noinspection ChainOfInstanceofChecks
        if (node instanceof Text) {
            if (pattern.matcher(node.getValue()).matches()) {
                ((Text) node).setValue("2000-01-01T00:11:22+02:00");
            }
            return;
        }
        if (node instanceof Attribute) {
            if (pattern.matcher(node.getValue()).matches()) {
                ((Attribute) node).setValue("2000-01-01T00:11:22+02:00");
            }
            return;
        }
        if (node instanceof Element) {
            for (int i = 0; i < ((Element) node).getAttributeCount(); i++) {
                replaceDateTimes(((Element) node).getAttribute(i));
            }
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            replaceDateTimes(node.getChild(i));
        }
    }

}
