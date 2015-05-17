package model.process.learn;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LearnHirschbergCallableTest {

    @Test
    public void testCall() throws Exception {
        List<Byte> sequence1 = new ArrayList<>();
        sequence1.add(null);
        sequence1.add(null);
        sequence1.add(null);
        sequence1.add(null);
        List<Byte> sequence2 = new ArrayList<>();
        sequence2.add((byte) 'a');
        sequence2.add((byte) 'b');
        sequence2.add((byte) 'b');
        sequence2.add((byte) 'b');
        sequence2.add((byte) 'c');
        sequence2.add((byte) 'c');
        sequence2.add((byte) 'c');
        List<Byte> sequence3 = new ArrayList<>();
        sequence3.add((byte) 'b');
        sequence3.add((byte) 'b');
        sequence3.add((byte) 'c');
        List<Byte> sequence4 = new ArrayList<>();
        sequence4.add((byte) 'a');
        sequence4.add((byte) 'b');
        sequence4.add((byte) 'c');
        sequence4.add((byte) 'c');
        List<Byte> sequence5 = new ArrayList<>();
        sequence5.add((byte) 'b');
        sequence5.add(null);
        sequence5.add(null);
        sequence5.add((byte) 'b');
        sequence5.add((byte) 'c');
        sequence5.add(null);

        Collection<Byte> reference = new ArrayList<>();
        for (int i = 0; i < Math.max(sequence1.size(), sequence2.size()); i++) {
            reference.add(null);
        }
        LearnHirschbergCallable learnHirschbergCallable = new LearnHirschbergCallable(sequence1, sequence2);
        Assert.assertEquals(learnHirschbergCallable.call(), reference);

        reference = new ArrayList<>();
        for (int i = 0; i < Math.max(sequence1.size(), sequence3.size()); i++) {
            reference.add(null);
        }
        learnHirschbergCallable = new LearnHirschbergCallable(sequence1, sequence3);
        Assert.assertEquals(learnHirschbergCallable.call(), reference);

        reference = new ArrayList<>();
        reference.add(null);
        reference.add(null);
        reference.add((byte) 'b');
        reference.add((byte) 'b');
        reference.add(null);
        reference.add(null);
        reference.add((byte) 'c');
        learnHirschbergCallable = new LearnHirschbergCallable(sequence2, sequence3);
        Assert.assertEquals(learnHirschbergCallable.call(), reference);

        reference = new ArrayList<>();
        reference.add((byte) 'a');
        reference.add(null);
        reference.add(null);
        reference.add((byte) 'b');
        reference.add(null);
        reference.add((byte) 'c');
        reference.add((byte) 'c');
        learnHirschbergCallable = new LearnHirschbergCallable(sequence2, sequence4);
        Assert.assertEquals(learnHirschbergCallable.call(), reference);

        reference = new ArrayList<>();
        reference.add(null);
        reference.add((byte) 'b');
        reference.add(null);
        reference.add(null);
        reference.add(null);
        reference.add((byte) 'c');
        reference.add(null);
        learnHirschbergCallable = new LearnHirschbergCallable(sequence2, sequence5);
        Assert.assertEquals(learnHirschbergCallable.call(), reference);

        reference = new ArrayList<>();
        reference.add(null);
        reference.add((byte) 'b');
        reference.add(null);
        reference.add((byte) 'c');
        learnHirschbergCallable = new LearnHirschbergCallable(sequence3, sequence4);
        Assert.assertEquals(learnHirschbergCallable.call(), reference);

        reference = new ArrayList<>();
        reference.add((byte) 'b');
        reference.add(null);
        reference.add(null);
        reference.add((byte) 'b');
        reference.add((byte) 'c');
        reference.add(null);
        learnHirschbergCallable = new LearnHirschbergCallable(sequence3, sequence5);
        Assert.assertEquals(learnHirschbergCallable.call(), reference);

        reference = new ArrayList<>();
        reference.add(null);
        reference.add(null);
        reference.add(null);
        reference.add((byte) 'b');
        reference.add((byte) 'c');
        reference.add(null);
        learnHirschbergCallable = new LearnHirschbergCallable(sequence4, sequence5);
        Assert.assertEquals(learnHirschbergCallable.call(), reference);
    }
}
