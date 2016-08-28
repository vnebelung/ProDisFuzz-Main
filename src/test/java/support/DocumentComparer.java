/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package support;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import java.util.ArrayList;
import java.util.List;

public enum DocumentComparer {
    ;

    DocumentComparer() {
    }

    public static boolean areEqual(Document document1, Document document2) {
        return areEqual(document1.getRootElement(), document2.getRootElement());
    }

    private static boolean areEqual(Element element1, Element element2) {
        List<Attribute> attributes1 = getAttributes(element1);
        List<Attribute> attributes2 = getAttributes(element2);
        Elements children1 = element1.getChildElements();
        Elements children2 = element2.getChildElements();
        return areEqual(attributes1, attributes2) && areEqual(element1.getValue(), element2.getValue()) &&
                areEqual(children1, children2);
    }

    private static boolean areEqual(String value1, String value2) {
        return value1.equals(value2);
    }

    private static boolean areEqual(Elements elements1, Elements elements2) {
        if (elements1.size() != elements2.size()) {
            return false;
        }
        for (int i = 0; i < elements1.size(); i++) {
            if (!areEqual(elements1.get(i), elements2.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean areEqual(List<Attribute> attributes1, List<Attribute> attributes2) {
        if (attributes1.size() != attributes2.size()) {
            return false;
        }
        for (int i = 0; i < attributes1.size(); i++) {
            if (!areEqual(attributes1.get(i), attributes2.get(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean areEqual(Attribute attribute1, Attribute attribute2) {
        if (!attribute1.getLocalName().equals(attribute2.getLocalName())) {
            return false;
        }
        //noinspection RedundantIfStatement
        if (!attribute1.getValue().equals(attribute2.getValue())) {
            return false;
        }
        return true;
    }

    private static List<Attribute> getAttributes(Element element) {
        List<Attribute> result = new ArrayList<>(element.getAttributeCount());
        for (int i = 0; i < element.getAttributeCount(); i++) {
            result.add(element.getAttribute(i));
        }
        return result;
    }
}
