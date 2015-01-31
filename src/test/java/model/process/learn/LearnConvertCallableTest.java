package model.process.learn;

import model.ProtocolFile;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LearnConvertCallableTest {

    @Test
    public void testCall() throws Exception {
        ProtocolFile[] protocolFiles = new ProtocolFile[2];
        protocolFiles[0] = new ProtocolFile(Paths.get(getClass().getResource("/capture1.txt").toURI()));
        protocolFiles[1] = new ProtocolFile(Paths.get(getClass().getResource("/capture2.txt").toURI()));
        LearnConvertCallable learnConvertCallable = new LearnConvertCallable(protocolFiles);
        List<List<Byte>> reference = new ArrayList<>();
        List<Byte> bytes = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            bytes.add((byte) ('0' + i));
        }
        bytes.add((byte) '\n');
        reference.add(bytes);
        bytes = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            bytes.add((byte) ('a' + i));
        }
        bytes.add((byte) '\n');
        reference.add(bytes);
        Assert.assertEquals(learnConvertCallable.call(), reference);
    }
}
