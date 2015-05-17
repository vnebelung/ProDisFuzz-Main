package model.process.learn;

import model.ProtocolFile;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class LearnConvertCallableTest {

    @SuppressWarnings("OverlyBroadThrowsClause")
    @Test
    public void testCall() throws Exception {
        ProtocolFile[] protocolFiles = new ProtocolFile[2];
        //noinspection HardCodedStringLiteral
        protocolFiles[0] = new ProtocolFile(Paths.get(getClass().getResource("/capture1.txt").toURI()));
        //noinspection HardCodedStringLiteral
        protocolFiles[1] = new ProtocolFile(Paths.get(getClass().getResource("/capture2.txt").toURI()));
        Callable<List<List<Byte>>> learnConvertCallable = new LearnConvertCallable(protocolFiles);
        //noinspection TypeMayBeWeakened
        List<List<Byte>> reference = new ArrayList<>();
        List<Byte> bytes = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            //noinspection CharUsedInArithmeticContext,NumericCastThatLosesPrecision
            bytes.add((byte) ('0' + i));
        }
        //noinspection HardcodedLineSeparator
        bytes.add((byte) '\n');
        reference.add(bytes);
        bytes = new ArrayList<>(8);
        for (int i = 0; i < 8; i++) {
            //noinspection CharUsedInArithmeticContext,NumericCastThatLosesPrecision
            bytes.add((byte) ('a' + i));
        }
        //noinspection HardcodedLineSeparator
        bytes.add((byte) '\n');
        reference.add(bytes);
        Assert.assertEquals(learnConvertCallable.call(), reference);
    }
}
