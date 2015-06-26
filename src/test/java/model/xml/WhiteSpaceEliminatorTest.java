/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.xml;

import model.util.XmlWhiteSpaceEliminator;
import nu.xom.Attribute;
import nu.xom.Attribute.Type;
import nu.xom.Text;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

@SuppressWarnings({"HardCodedStringLiteral", "HardcodedLineSeparator"})
public class WhiteSpaceEliminatorTest {

    private XmlWhiteSpaceEliminator whiteSpaceEliminator;

    @BeforeClass
    public void setUp() throws IOException {
        whiteSpaceEliminator = new XmlWhiteSpaceEliminator();
    }

    @Test
    public void testMakeText() throws Exception {
        Assert.assertEquals(whiteSpaceEliminator.makeText("a b").size(), 1);
        Assert.assertTrue(whiteSpaceEliminator.makeText("a b").get(0) instanceof Text);
        Assert.assertEquals(whiteSpaceEliminator.makeText("a b").get(0).toString(), "[nu.xom.Text: a b]");

        Assert.assertEquals(whiteSpaceEliminator.makeText("\ta \t \r \n b ").size(), 1);
        Assert.assertTrue(whiteSpaceEliminator.makeText("\ta \t \r \n b ").get(0) instanceof Text);
        Assert.assertEquals(whiteSpaceEliminator.makeText("\ta \t \r \n b ").get(0).getValue(), "a b");

        Assert.assertEquals(whiteSpaceEliminator.makeText(" \t \r \n ").size(), 0);
    }

    @Test
    public void testMakeAttribute() throws Exception {
        Assert.assertEquals(whiteSpaceEliminator.makeAttribute("test", "", "a b", Type.ID).size(), 1);
        Assert.assertTrue(whiteSpaceEliminator.makeAttribute("test", "", "a b", Type.ID).get(0) instanceof Attribute);
        Assert.assertEquals(whiteSpaceEliminator.makeAttribute("test", "", "a b", Type.ID).get(0).toString(), "[nu" +
                ".xom.Attribute: test=\"a b\"]");

        Assert.assertEquals(whiteSpaceEliminator.makeAttribute("test", "", "\ta \t \r \n b ", Type.ID).size(), 1);
        Assert.assertTrue(whiteSpaceEliminator.makeAttribute("test", "", "\ta \t \r \n b ", Type.ID).get(0)
                instanceof Attribute);
        Assert.assertEquals(whiteSpaceEliminator.makeAttribute("test", "", "\ta \t \r \n b ", Type.ID).get(0)
                .getValue(), "a b");

        Assert.assertEquals(whiteSpaceEliminator.makeAttribute("test", "", " \t \r \n ", Type.ID).size(), 1);
        Assert.assertTrue(whiteSpaceEliminator.makeAttribute("test", "", " \t \r \n ", Type.ID).get(0) instanceof
                Attribute);
        Assert.assertEquals(whiteSpaceEliminator.makeAttribute("test", "", "\t \r \n ", Type.ID).get(0).getValue(), "");
    }
}
