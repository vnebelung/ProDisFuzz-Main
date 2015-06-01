package model.process.learn;

import model.protocol.ProtocolStructure;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class LearnStructureCallableTest {

    @Test
    public void testCall() throws Exception {
        List<Byte> block1 = new ArrayList<>();
        block1.add(null);
        block1.add(null);

        List<Byte> block2 = new ArrayList<>();
        block2.add((byte) 48);
        block2.add((byte) 48);

        List<Byte> block3 = new ArrayList<>();
        block3.add(null);

        List<Byte> block4 = new ArrayList<>();
        block4.add((byte) 48);

        ProtocolStructure reference = new ProtocolStructure();
        reference.addBlock(block1);
        reference.addBlock(block2);
        reference.addBlock(block3);
        reference.addBlock(block4);

        List<Byte> bytes = new ArrayList<>(6);
        bytes.addAll(block1);
        bytes.addAll(block2);
        bytes.addAll(block3);
        bytes.addAll(block4);

        LearnStructureCallable learnStructureCallable = new LearnStructureCallable(bytes);
        ProtocolStructure protocolStructure = learnStructureCallable.call();

        Assert.assertEquals(protocolStructure.getSize(), reference.getSize());
        for (int i = 0; i < reference.getSize(); i++) {
            Assert.assertEquals(protocolStructure.getBlock(i).getBytes(), reference.getBlock(i).getBytes());
            Assert.assertEquals(protocolStructure.getBlock(i).getMaxLength(), reference.getBlock(i).getMaxLength());
            Assert.assertEquals(protocolStructure.getBlock(i).getMinLength(), reference.getBlock(i).getMinLength());
            Assert.assertEquals(protocolStructure.getBlock(i).getType(), reference.getBlock(i).getType());
        }
    }
}
