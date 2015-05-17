package model;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

public class RandomPoolTest {

    @Test
    public void testNextBloatBytes() throws Exception {
        for (int i = 0; i < 100; i++) {
            List<Byte> bytes = RandomPool.getInstance().nextBloatBytes(i);
            Assert.assertTrue(bytes.size() < ((i * 10000) + 1));
        }
    }
}
