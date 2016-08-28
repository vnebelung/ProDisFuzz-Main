/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.import_;

import model.protocol.ProtocolStructure;
import model.util.Constants;
import model.util.Hex;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class is a callable responsible for creating the protocol structure out of an XML structure.
 */
class ProtocolStructureBuilder implements Callable<ProtocolStructure> {

    private Document document;

    /**
     * Constructs the callable.
     *
     * @param document the XML document to be imported
     */
    public ProtocolStructureBuilder(Document document) {
        this.document = document;
    }

    @Override
    public ProtocolStructure call() {
        ProtocolStructure result = new ProtocolStructure();
        // Create the node list
        Elements elements =
                document.getRootElement().getChildElements(Constants.XML_TAG_NAME_BLOCKS).get(0).getChildElements();
        // Create for each node the particular protocol block
        for (int i = 0; i < elements.size(); i++) {
            result.addBlock(readXMLContent(elements.get(i)));
        }
        return result;
    }

    /**
     * Reads the byte content for a given XML protocol block element.
     *
     * @param element the protocol block XML element
     * @return the byte content
     */
    private static List<Byte> readXMLContent(Element element) {
        List<Byte> result = new ArrayList<>();
        switch (element.getLocalName()) {
            case Constants.XML_TAG_NAME_BLOCK_VAR:
                // Add as many null bytes as the maximum length attribute
                int maxLength = Integer.parseInt(element.getAttribute(Constants.XML_TAG_NAME_MAX_LENGTH).getValue());
                for (int i = 0; i < maxLength; i++) {
                    result.add(null);
                }
                break;
            case Constants.XML_TAG_NAME_BLOCK_FIX:
                Elements elements = element.getChildElements();
                // Create the content of this block for each content element
                for (int i = 0; i < elements.size(); i++) {
                    if (!elements.get(i).getLocalName().equals(Constants.XML_TAG_NAME_CONTENT)) {
                        continue;
                    }
                    // Create the content out of all byte elements
                    String hexbin = elements.get(i).getValue();
                    byte[] bytes = Hex.hexBin2Byte(hexbin);
                    for (byte each : bytes) {
                        result.add(each);
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

}
