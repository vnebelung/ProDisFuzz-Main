package model.process.report;

import model.process.fuzzoptions.FuzzOptionsProcess.CommunicationSave;
import model.protocol.InjectedProtocolStructure;
import model.record.Recordings;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;

@SuppressWarnings({"HardCodedStringLiteral", "AnonymousInnerClassMayBeStatic", "NumericCastThatLosesPrecision",
        "DynamicRegexReplaceableByCompiledPattern"})
public class ReportProcessTest {

    private ReportProcess reportProcess;
    private Path path;
    private byte[] bytes;
    private Recordings recordings;

    @BeforeClass
    public void setUp() throws IOException {
        reportProcess = new ReportProcess();
        recordings = new Recordings();
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

        Assert.assertEquals(Files.exists(path.resolve("results.html")), true);
        String reportHtml = new String(Files.readAllBytes(path.resolve("results.html")), StandardCharsets.UTF_8);
        reportHtml = reportHtml.replaceAll("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\+\\d{2}:\\d{2}",
                "2000-01-01T00:11:22+02:00");
        String referenceHtml = new String(Files.readAllBytes(Paths.get(getClass().getResource("/results.html").toURI
                ())), StandardCharsets.UTF_8);
        Assert.assertEquals(reportHtml, referenceHtml);

        Assert.assertEquals(Files.exists(path.resolve("results_records").resolve("record0-0.bytes")), true);
        Assert.assertEquals(Files.exists(path.resolve("results_records").resolve("record0-1.bytes")), true);
        Assert.assertEquals(Files.exists(path.resolve("results_records").resolve("record1-0.bytes")), true);
        Assert.assertEquals(Files.exists(path.resolve("results_records").resolve("record2-0.bytes")), true);

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
