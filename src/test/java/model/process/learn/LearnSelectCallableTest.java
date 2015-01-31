package model.process.learn;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class LearnSelectCallableTest {

    @Test
    public void testCall() throws Exception {
        List<List<Byte>> sequences = new ArrayList<>();
        List<Byte> bytes = new ArrayList<>();
        bytes.add((byte) 'a');
        bytes.add((byte) 'b');
        bytes.add((byte) 'c');
        sequences.add(bytes);
        bytes = new ArrayList<>();
        bytes.add((byte) 'a');
        bytes.add((byte) 'b');
        bytes.add((byte) 'b');
        bytes.add((byte) 'c');
        sequences.add(bytes);
        bytes = new ArrayList<>();
        bytes.add((byte) 'b');
        bytes.add((byte) 'b');
        bytes.add((byte) 'c');
        sequences.add(bytes);
        bytes = new ArrayList<>();
        bytes.add((byte) 'a');
        bytes.add((byte) 'b');
        bytes.add((byte) 'c');
        bytes.add((byte) 'c');
        sequences.add(bytes);
        bytes = new ArrayList<>();
        bytes.add((byte) 'b');
        bytes.add((byte) 'b');
        bytes.add((byte) 'b');
        bytes.add((byte) 'b');
        sequences.add(bytes);

        LearnSelectCallable learnSelectCallable = new LearnSelectCallable(sequences);
        int[] reference1 = {2, 3};
        Assert.assertEquals(learnSelectCallable.call(), reference1);

        sequences.remove(3);
        learnSelectCallable = new LearnSelectCallable(sequences);
        int[] reference2 = {1, 3};
        Assert.assertEquals(learnSelectCallable.call(), reference2);

        sequences.remove(1);
        learnSelectCallable = new LearnSelectCallable(sequences);
        int[] reference3 = {0, 1};
        Assert.assertEquals(learnSelectCallable.call(), reference3);
    }
}
