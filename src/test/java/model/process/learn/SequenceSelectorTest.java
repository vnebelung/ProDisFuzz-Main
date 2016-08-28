/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.learn;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SequenceSelectorTest {

    @Test
    public void testCall() throws Exception {
        List<Byte> bytes = new ArrayList<>(3);
        bytes.add((byte) 'a');
        bytes.add((byte) 'b');
        bytes.add((byte) 'c');
        LearnSequence ref1 = new LearnSequence(bytes, new NGramCreator(bytes, 3).call());
        bytes = new ArrayList<>(5);
        bytes.add((byte) 'a');
        bytes.add((byte) 'a');
        bytes.add((byte) 'b');
        bytes.add((byte) 'b');
        bytes.add((byte) 'c');
        LearnSequence ref2 = new LearnSequence(bytes, new NGramCreator(bytes, 3).call());
        bytes = new ArrayList<>(3);
        bytes.add((byte) 'b');
        bytes.add((byte) 'b');
        bytes.add((byte) 'c');
        LearnSequence ref3 = new LearnSequence(bytes, new NGramCreator(bytes, 3).call());
        bytes = new ArrayList<>(2);
        bytes.add((byte) 'a');
        bytes.add((byte) 'b');
        LearnSequence ref4 = new LearnSequence(bytes, new NGramCreator(bytes, 3).call());
        bytes = new ArrayList<>(4);
        bytes.add((byte) 'b');
        bytes.add((byte) 'b');
        bytes.add((byte) 'c');
        bytes.add((byte) 'c');
        LearnSequence ref5 = new LearnSequence(bytes, new NGramCreator(bytes, 3).call());

        ref1.addDistanceTo(ref2, new DiceValueCalculator(ref1, ref2).call());
        ref1.addDistanceTo(ref3, new DiceValueCalculator(ref1, ref3).call());
        ref1.addDistanceTo(ref4, new DiceValueCalculator(ref1, ref4).call());
        ref1.addDistanceTo(ref5, new DiceValueCalculator(ref1, ref5).call());
        ref2.addDistanceTo(ref3, new DiceValueCalculator(ref2, ref3).call());
        ref2.addDistanceTo(ref4, new DiceValueCalculator(ref2, ref4).call());
        ref2.addDistanceTo(ref5, new DiceValueCalculator(ref2, ref5).call());
        ref3.addDistanceTo(ref4, new DiceValueCalculator(ref3, ref4).call());
        ref3.addDistanceTo(ref5, new DiceValueCalculator(ref3, ref5).call());
        ref4.addDistanceTo(ref5, new DiceValueCalculator(ref4, ref5).call());
        Set<LearnSequence> sequences = new HashSet<>();
        sequences.add(ref1);
        sequences.add(ref2);
        sequences.add(ref3);
        sequences.add(ref4);
        sequences.add(ref5);

        //        1	abc	--a	-ab	abc	bc-	c--
        //        2	aabbc	--a	-aa	aab	abb	bbc	bc-	c--
        //        3	bcc	--b	-bc	bcc	cc-	c--
        //        4	ab	--a	-ab	ab-	b--
        //        5	bbcc	--b	-bb	bbc	bcc	cc-	c--
        //
        //        Dice-Value			Distance
        //        1	2	0,5	0,5
        //        1	3	0,2	0,8
        //        1	4	0,4444444444	0,5555555556
        //        1	5	0,1818181818	0,8181818182
        //        2	3	0,1666666667	0,8333333333
        //        2	4	0,1818181818	0,8181818182
        //        2	5	0,3076923077	0,6923076923
        //        3	4	0	1
        //        3	5	0,7272727273	0,2727272727
        //        4	5	0	1
        //
        //        Average Distance
        //        1	0,8912457912
        //        2	0,9479409479
        //        3	0,9686868687
        //        4	1,1245791246
        //        5	0,9277389277
        //
        //        Combined Distance
        //        1	2	-1,3391867392
        //        1	3	-1,0599326599
        //        1	4	-1,4602693603
        //        1	5	-1,0008029008
        //        2	3	-1,0832944833
        //        2	4	-1,2543382543
        //        2	5	-1,1833721834
        //        3	4	-1,0932659933
        //        3	5	-1,6236985237
        //        4	5	-1,0523180523

        SequenceSelector sequenceSelector = new SequenceSelector(sequences);
        Set<LearnSequence> reference1 = new HashSet<>(2);
        reference1.add(ref3);
        reference1.add(ref5);
        Assert.assertEquals(sequenceSelector.call(), reference1);

        sequences.remove(ref3);

        //        1	abc	--a	-ab	abc	bc-	c--
        //        2	aabbc	--a	-aa	aab	abb	bbc	bc-	c--
        //        4	ab	--a	-ab	ab-	b--
        //        5	bbcc	--b	-bb	bbc	bcc	cc-	c--
        //
        //        Dice-Value			Distance
        //        1	2	0,5	0,5
        //        1	4	0,4444444444	0,5555555556
        //        1	5	0,1818181818	0,8181818182
        //        2	4	0,1818181818	0,8181818182
        //        2	5	0,3076923077	0,6923076923
        //        4	5	0	1
        //
        //        Average Distance
        //        1	0,9368686869
        //        2	1,0052447552
        //        4	1,1868686869
        //        5	1,2552447552
        //
        //        Combined Distance
        //        1	2	-1,4421134421
        //        1	4	-1,5681818182
        //        1	5	-1,3739316239
        //        2	4	-1,3739316239
        //        2	5	-1,5681818182
        //        4	5	-1,4421134421

        Set<LearnSequence> reference2 = new HashSet<>(2);
        reference2.add(ref1);
        reference2.add(ref4);
        Assert.assertEquals(sequenceSelector.call(), reference2);

        sequences.remove(ref1);

        //        2	aabbc	--a	-aa	aab	abb	bbc	bc-	c--
        //        4	ab	--a	-ab	ab-	b--
        //        5	bbcc	--b	-bb	bbc	bcc	cc-	c--
        //
        //        Dice-Value			Distance
        //        2	4	0,1818181818	0,8181818182
        //        2	5	0,3076923077	0,6923076923
        //        4	5	0	1
        //
        //        Average Distance
        //        2	1,5104895105
        //        4	1,8181818182
        //        5	1,6923076923
        //
        //        Combined Distance
        //        2	4	-2,5104895105
        //        2	5	-2,5104895105
        //        4	5	-2,5104895105

        sequenceSelector = new SequenceSelector(sequences);
        Set<LearnSequence> reference3 = new HashSet<>(2);
        reference3.add(ref2);
        reference3.add(ref4);
        Assert.assertEquals(sequenceSelector.call(), reference3);
    }
}
