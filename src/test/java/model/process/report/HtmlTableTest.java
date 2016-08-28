/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.report;

import org.testng.Assert;
import org.testng.annotations.Test;

@SuppressWarnings("HardCodedStringLiteral")
public class HtmlTableTest {

    @Test
    public void testSetText() throws Exception {
        HtmlTable htmlTable = new HtmlTable(2, 3);
        htmlTable.setText(1, 2, "testvalue");
        Assert.assertEquals(htmlTable.getTable().toXML(), "<table xmlns=\"http://www.w3.org/1999/xhtml\"><tr><th " +
                "/><th /><th /></tr><tr><td /><td /><td>testvalue</td></tr></table>");
    }

    @Test
    public void testSetLink() throws Exception {
        HtmlTable htmlTable = new HtmlTable(2, 3);
        htmlTable.setLink(0, 1, "testHref", "testText");
        Assert.assertEquals(htmlTable.getTable().toXML(), "<table xmlns=\"http://www.w3.org/1999/xhtml\"><tr><th " +
                "/><th><a href=\"testHref\">testText</a></th><th /></tr><tr><td /><td /><td /></tr></table>");
    }

    @Test
    public void testSetAttribute() throws Exception {
        HtmlTable htmlTable = new HtmlTable(2, 3);
        htmlTable.setAttribute(1, 0, "testName", "testValue");
        Assert.assertEquals(htmlTable.getTable().toXML(), "<table xmlns=\"http://www.w3.org/1999/xhtml\"><tr><th " +
                "/><th /><th /></tr><tr><td testName=\"testValue\" /><td /><td /></tr></table>");
    }
}
