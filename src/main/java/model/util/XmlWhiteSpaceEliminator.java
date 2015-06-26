/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.util;

import nu.xom.Attribute.Type;
import nu.xom.NodeFactory;
import nu.xom.Nodes;

public class XmlWhiteSpaceEliminator extends NodeFactory {

    /**
     * Trims the given string of white space, line breaks, and tabs.
     *
     * @param data the input string
     * @return the trimmed string
     */
    private static String normalizeSpace(String data) {
        //noinspection HardcodedLineSeparator
        String string = data.replace('\t', ' ').replace('\n', ' ').replace('\r', ' ').trim();

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            if (i == 0 || string.charAt(i - 1) != ' ' || string.charAt(i) != ' ') {
                result.append(string.charAt(i));
            }
        }
        return result.toString();
    }

    @Override
    public Nodes makeText(String data) {
        String string = normalizeSpace(data);
        if (string.isEmpty()) {
            return new Nodes();
        }
        return super.makeText(string);
    }

    @Override
    public Nodes makeAttribute(String name, String URI, String value, Type type) {
        String string = normalizeSpace(value);
        return super.makeAttribute(name, URI, string, type);
    }
}
