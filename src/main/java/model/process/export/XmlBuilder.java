/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.export;

import model.protocol.ProtocolBlock;
import model.protocol.ProtocolBlock.Type;
import model.protocol.ProtocolStructure;
import model.util.Constants;
import model.util.Hex;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Callable;

/**
 * This class is a callable responsible for creating the XML structure out of the protocol structure.
 */
class XmlBuilder implements Callable<Document> {

    private ProtocolStructure protocolStructure;

    /**
     * Constructs the callable.
     *
     * @param protocolStructure the protocol structure to be exported
     */
    public XmlBuilder(ProtocolStructure protocolStructure) {
        this.protocolStructure = protocolStructure;
    }

    @Override
    public Document call() {
        Element result = new Element(Constants.XML_TAG_NAME_ROOT);
        ZonedDateTime zonedDateTime =
                ZonedDateTime.from(Instant.now().atZone(ZoneId.systemDefault())).truncatedTo(ChronoUnit.SECONDS);
        //noinspection HardCodedStringLiteral
        result.addAttribute(new Attribute("datetime", zonedDateTime.toOffsetDateTime().toString()));
        // Append the protocolParts element to the root element
        result.appendChild(createXMLBlocks());
        return new Document(result);
    }

    /**
     * Creates the XML blocks element including its children.
     *
     * @return the blocks element
     */
    private Element createXMLBlocks() {
        // Create the protocolParts element
        Element result = new Element(Constants.XML_TAG_NAME_BLOCKS);
        // Append individual part elements to the protocolParts element
        for (int i = 0; i < protocolStructure.getSize(); i++) {
            result.appendChild(createXMLBlock(protocolStructure.getBlock(i)));
        }
        return result;
    }

    /**
     * Creates a XML block element including its children.
     *
     * @param protocolBlock the protocol block
     * @return the block element
     */
    private static Element createXMLBlock(ProtocolBlock protocolBlock) {
        Element result = null;
        switch (protocolBlock.getType()) {
            case VAR:
                result = new Element(Constants.XML_TAG_NAME_BLOCK_VAR);
                break;
            case FIX:
                result = new Element(Constants.XML_TAG_NAME_BLOCK_FIX);
                break;
        }
        result.addAttribute(
                new Attribute(Constants.XML_TAG_NAME_MIN_LENGTH, String.valueOf(protocolBlock.getMinLength())));
        result.addAttribute(
                new Attribute(Constants.XML_TAG_NAME_MAX_LENGTH, String.valueOf(protocolBlock.getMaxLength())));
        // Append content element to the part element
        if (protocolBlock.getType() == Type.FIX) {
            result.appendChild(createXMLContent(protocolBlock.getBytes()));
        }
        return result;
    }

    /**
     * Creates a XML content element including its children.
     *
     * @param bytes the byte content of a protocol block
     * @return the content element
     */
    private static Element createXMLContent(Byte... bytes) {
        Element result = new Element(Constants.XML_TAG_NAME_CONTENT);
        // Append byte elements to the content element
        StringBuilder content = new StringBuilder(bytes.length * 2);
        for (Byte each : bytes) {
            content.append(Hex.byte2HexBin(each));
        }
        result.appendChild(content.toString());
        return result;
    }

}
