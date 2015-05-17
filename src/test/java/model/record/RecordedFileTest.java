package model.record;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

public class RecordedFileTest {

    @Test
    public void testGetFilePath() throws Exception {
        byte[] bytes = {48, 49, 50};
        RecordedFile recordedFile = new RecordedFile(bytes, true, Instant.now());
        //noinspection AccessOfSystemProperties
        Assert.assertTrue(recordedFile.getFilePath().startsWith(System.getProperty("java.io.tmpdir")));
    }

    @Test
    public void testIsCrash() throws Exception {
        byte[] bytes = {48, 49, 50};
        RecordedFile recordedFile = new RecordedFile(bytes, true, Instant.now());
        Assert.assertTrue(recordedFile.isCrash());
        recordedFile = new RecordedFile(bytes, false, Instant.now());
        Assert.assertFalse(recordedFile.isCrash());
    }

    @Test
    public void testGetSavedTime() throws Exception {
        byte[] bytes = {48, 49, 50};
        Instant instant = Instant.now();
        RecordedFile recordedFile = new RecordedFile(bytes, true, instant);
        Assert.assertEquals(recordedFile.getSavedTime(), instant);
    }

    @Test
    public void testSetOutputPath() throws IOException {
        byte[] bytes = {48, 49, 50};
        RecordedFile recordedFile = new RecordedFile(bytes, true, Instant.now());
        Path path = Files.createTempFile(null, null);
        recordedFile.setOutputPath(path);
        Assert.assertEquals(recordedFile.getOutputPath(), path);
        Files.delete(path);
    }

    @Test
    public void testDelete() throws Exception {
        byte[] bytes = {48, 49, 50};
        RecordedFile recordedFile = new RecordedFile(bytes, true, Instant.now());
        Assert.assertTrue(Files.exists(recordedFile.getFilePath()));
        recordedFile.delete();
        Assert.assertFalse(Files.exists(recordedFile.getFilePath()));
    }
}
