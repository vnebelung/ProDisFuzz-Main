package model.updater;

import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("HardCodedStringLiteral")
public class ReleaseInformationTest {

    @Test
    public void testGetNumber() throws Exception {
        ReleaseInformation releaseInformation = new ReleaseInformation(12, "test", "date", "requ", "1", "2");
        Assert.assertEquals(releaseInformation.getNumber(), 12);
    }

    @Test
    public void testGetName() throws Exception {
        ReleaseInformation releaseInformation = new ReleaseInformation(12, "test", "date", "requ", "1", "2");
        Assert.assertEquals(releaseInformation.getName(), "test");
    }

    @Test
    public void testGetDate() throws Exception {
        ReleaseInformation releaseInformation = new ReleaseInformation(12, "test", "date", "requ", "1", "2");
        Assert.assertEquals(releaseInformation.getDate(), "date");
    }

    @Test
    public void testGetRequirements() throws Exception {
        ReleaseInformation releaseInformation = new ReleaseInformation(12, "test", "date", "requ", "1", "2");
        Assert.assertEquals(releaseInformation.getRequirements(), "requ");
    }

    @Test
    public void testGetInformation() throws Exception {
        ReleaseInformation releaseInformation = new ReleaseInformation(12, "test", "date", "requ", "1", "2");
        Assert.assertEquals(releaseInformation.getInformation(), new String[]{"1", "2"});
    }
}
