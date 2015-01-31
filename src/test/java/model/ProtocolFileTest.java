package model;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProtocolFileTest {

    private Path path;
    private ProtocolFile protocolFile;

    @BeforeMethod
    public void setUp() throws Exception {
        path = Files.createTempFile("b_", null);
        Files.write(path, "123".getBytes());
        protocolFile = new ProtocolFile(path);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        Files.delete(path);
    }

    @Test
    public void testGetName() throws Exception {
        Pattern pattern = Pattern.compile("[^\\.]+\\.[^\\.]+");
        Matcher matcher = pattern.matcher(protocolFile.getName());
        Assert.assertTrue(matcher.matches());
    }

    @Test
    public void testGetSha256() throws Exception {
        Assert.assertEquals("a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3", protocolFile
                .getSha256());
    }

    @Test
    public void testGetSize() throws Exception {
        Assert.assertEquals(3, protocolFile.getSize());
    }

    @Test
    public void testGetLastModified() throws Exception {
        Assert.assertEquals(Files.getLastModifiedTime(path).toMillis(), protocolFile.getLastModified());
    }

    @Test
    public void testGetContent() throws Exception {
        org.testng.Assert.assertEquals("123".getBytes(), protocolFile.getContent());
    }

    @Test
    public void testCompareTo() throws Exception {
        Path path1 = Files.createTempFile("a_", null);
        Path path3 = Files.createTempFile("c_", null);
        ProtocolFile protocolFile1 = new ProtocolFile(path1);
        ProtocolFile protocolFile3 = new ProtocolFile(path3);
        Assert.assertTrue(protocolFile.compareTo(protocolFile1) > 0);
        Assert.assertTrue(protocolFile.compareTo(protocolFile3) < 0);
        Files.delete(path1);
        Files.delete(path3);
    }
}
