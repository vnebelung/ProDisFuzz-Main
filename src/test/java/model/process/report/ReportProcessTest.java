/*
 * This file is part of ProDisFuzz, modified on 28.06.15 01:35.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.report;

import model.process.fuzzOptions.FuzzOptionsProcess.CommunicationSave;
import model.protocol.InjectedProtocolStructure;
import model.record.Recordings;
import model.util.XmlWhiteSpaceEliminator;
import nu.xom.*;
import nu.xom.canonical.Canonicalizer;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import java.util.regex.Pattern;

@SuppressWarnings({"HardCodedStringLiteral", "AnonymousInnerClassMayBeStatic", "NumericCastThatLosesPrecision"})
public class ReportProcessTest {

    private ReportProcess reportProcess;
    private Path path;
    private byte[] bytes;
    private Recordings recordings;
    private Pattern pattern;

    @BeforeClass
    public void setUp() throws IOException {
        reportProcess = new ReportProcess();
        recordings = new Recordings();
        pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{2}:\\d{2}");

        bytes = new byte[3];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (i * 17);
        }
        recordings.addRecording(bytes, false, Instant.parse("2000-01-01T00:11:22Z"));
        recordings.addRecording(bytes, false, Instant.parse("2000-01-01T00:11:22Z"));
        recordings.addRecording(bytes, true, Instant.parse("2000-01-01T00:11:22Z"));
        recordings.addRecording(bytes, true, Instant.parse("2000-01-01T00:11:22Z"));
        Duration duration = Duration.ofSeconds(3723);
        InjectedProtocolStructure injectedProtocolStructure = new InjectedProtocolStructure();
        Byte[] block1 = new Byte[2];
        block1[0] = (byte) 0;
        block1[1] = (byte) 17;
        injectedProtocolStructure.addBlock(block1);
        Byte[] block2 = new Byte[3];
        block2[0] = null;
        block2[1] = null;
        block2[2] = null;
        injectedProtocolStructure.addBlock(block2);
        injectedProtocolStructure.getBlock(1).setLibraryInjection();
        try {
            injectedProtocolStructure.getBlock(1).setLibrary(Paths.get(getClass().getResource("/library.txt").toURI()));
        } catch (URISyntaxException e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
        Byte[] block3 = new Byte[1];
        block3[0] = (byte) 0;
        injectedProtocolStructure.addBlock(block3);
        Byte[] block4 = new Byte[1];
        block4[0] = null;
        injectedProtocolStructure.addBlock(block4);
        Byte[] block5 = new Byte[3];
        block5[0] = (byte) 0;
        block5[1] = (byte) 17;
        block5[2] = (byte) 34;
        injectedProtocolStructure.addBlock(block5);
        InetSocketAddress inetSocketAddress = new InetSocketAddress("example.net", 999);
        reportProcess.init(recordings, duration, inetSocketAddress, 10, injectedProtocolStructure, 100, 101,
                CommunicationSave.ALL, 99);

        path = Files.createTempDirectory("testng_");
    }

    @Test(priority = 1)
    public void testWrite() throws IOException, URISyntaxException {
        reportProcess.write(path);
        Assert.assertEquals(Files.list(path).count(), 2);
        Assert.assertEquals(Files.list(path.resolve("results_records")).count(), 4);

        Assert.assertTrue(Files.exists(path.resolve("results.html")));
        String report = null;
        //noinspection OverlyBroadCatchBlock
        try (OutputStream outputStream = new ByteArrayOutputStream()) {
            Builder builder = new Builder(new XmlWhiteSpaceEliminator());
            Document exportedDoc = builder.build(path.resolve("results.html").toFile());
            replaceDateTimes(exportedDoc.getRootElement());
            Canonicalizer canonicalizer = new Canonicalizer(outputStream, Canonicalizer.EXCLUSIVE_XML_CANONICALIZATION);
            canonicalizer.write(exportedDoc);
            report = outputStream.toString();
        } catch (ParsingException | IOException e) {
            e.printStackTrace();
        }
        String reference = null;
        //noinspection OverlyBroadCatchBlock
        try (OutputStream outputStream = new ByteArrayOutputStream()) {
            Builder parser = new Builder(new XmlWhiteSpaceEliminator());
            Document referenceDoc = parser.build(Paths.get(getClass().getResource("/results.html").toURI()).toFile());
            Canonicalizer canonicalizer = new Canonicalizer(outputStream, Canonicalizer.EXCLUSIVE_XML_CANONICALIZATION);
            canonicalizer.write(referenceDoc);
            reference = outputStream.toString();
        } catch (ParsingException | IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(report, reference);

        Assert.assertTrue(Files.exists(path.resolve("results_records").resolve("record0-0.bytes")));
        Assert.assertTrue(Files.exists(path.resolve("results_records").resolve("record0-1.bytes")));
        Assert.assertTrue(Files.exists(path.resolve("results_records").resolve("record1-0.bytes")));
        Assert.assertTrue(Files.exists(path.resolve("results_records").resolve("record2-0.bytes")));

        Assert.assertEquals(Files.readAllBytes(path.resolve("results_records").resolve("record0-0.bytes")), bytes);
        Assert.assertEquals(Files.readAllBytes(path.resolve("results_records").resolve("record0-1.bytes")), bytes);
        Assert.assertEquals(Files.readAllBytes(path.resolve("results_records").resolve("record1-0.bytes")), bytes);
        Assert.assertEquals(Files.readAllBytes(path.resolve("results_records").resolve("record2-0.bytes")), bytes);

        Files.copy(path.resolve("results_records").resolve("record0-0.bytes"), recordings.getRecord(0).getFilePath());
        Files.copy(path.resolve("results_records").resolve("record0-1.bytes"), recordings.getRecord(1).getFilePath());
        Files.copy(path.resolve("results_records").resolve("record1-0.bytes"), recordings.getRecord(2).getFilePath());
        Files.copy(path.resolve("results_records").resolve("record2-0.bytes"), recordings.getRecord(3).getFilePath());
        reportProcess.write(path);
        Assert.assertEquals(Files.list(path).count(), 4);
        Assert.assertEquals(Files.list(path.resolve("results(1)_records")).count(), 4);
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

    @AfterClass
    public void tearDown() throws IOException {
        //noinspection RefusedBequest
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Test(priority = 2)
    public void testIsWritten() throws Exception {
        reportProcess.reset();
        Assert.assertFalse(reportProcess.isWritten());
        reportProcess.write(path);
        Assert.assertTrue(reportProcess.isWritten());
    }
}
