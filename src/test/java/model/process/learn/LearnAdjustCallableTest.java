package model.process.learn;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

public class LearnAdjustCallableTest {

    @Test
    public void testCall() throws Exception {
        List<Byte> reference = new ArrayList<>(12);
        reference.add((byte) 'A');
        reference.add((byte) 'A');
        reference.add((byte) 'A');
        reference.add((byte) 'a');
        reference.add((byte) 'b');
        reference.add((byte) 'c');
        reference.add((byte) 'd');
        reference.add((byte) 'e');
        reference.add((byte) 'f');
        reference.add((byte) 'g');
        reference.add((byte) 'A');
        reference.add((byte) 'A');
        reference.add((byte) 'A');

        LearnAdjustCallable learnAdjustCallable = new LearnAdjustCallable(reference);
        Assert.assertEquals(learnAdjustCallable.call(), reference);

        reference.set(4, null);
        learnAdjustCallable = new LearnAdjustCallable(reference);
        Assert.assertEquals(learnAdjustCallable.call(), reference);

        reference.set(7, null);
        learnAdjustCallable = new LearnAdjustCallable(reference);
        Assert.assertEquals(learnAdjustCallable.call(), reference);

        reference.set(9, null);
        learnAdjustCallable = new LearnAdjustCallable(reference);
        Assert.assertEquals(learnAdjustCallable.call(), reference);

        reference.set(3, null);
        List<Byte> sequence = new ArrayList<>(reference);
        sequence.set(5, null);
        sequence.set(6, null);
        learnAdjustCallable = new LearnAdjustCallable(reference);
        Assert.assertEquals(learnAdjustCallable.call(), sequence);

        reference.set(8, null);
        sequence.set(8, null);
        learnAdjustCallable = new LearnAdjustCallable(reference);
        Assert.assertEquals(learnAdjustCallable.call(), sequence);


    }
}
