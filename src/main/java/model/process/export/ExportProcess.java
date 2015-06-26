/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.export;

import model.process.AbstractProcess;
import model.protocol.ProtocolBlock;
import model.protocol.ProtocolBlock.Type;
import model.protocol.ProtocolStructure;
import model.util.Constants;
import model.util.Hex;
import model.util.XmlExchange;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class ExportProcess extends AbstractProcess {

    private ProtocolStructure protocolStructure;
    private boolean exported;

    /**
     * Instantiates a new process responsible for exporting the protocol structure to a XML file.
     */
    public ExportProcess() {
        super();
        protocolStructure = new ProtocolStructure();
    }

    @Override
    public void reset() {
        exported = false;
        protocolStructure.clear();
        spreadUpdate();
    }

    /**
     * Initiates the export process.
     *
     * @param protocolStructure the protocol structure
     */
    public void init(ProtocolStructure protocolStructure) {
        this.protocolStructure = protocolStructure;
        exported = false;
        spreadUpdate();
    }

    /**
     * Exports the protocol structure to the given XML file.
     *
     * @param path the export file
     */
    public void exportXML(Path path) {
        Document document = new Document(createXMLRoot());
        exported = XmlExchange.exportXML(document, path);
        spreadUpdate();
    }

    /**
     * Creates the XML root element including its children.
     *
     * @return the root element
     */
    private Element createXMLRoot() {
        Element result = new Element(Constants.XML_PROTOCOL_ROOT);
        ZonedDateTime zonedDateTime = ZonedDateTime.from(Instant.now().atZone(ZoneId.systemDefault())).truncatedTo
                (ChronoUnit.SECONDS);
        //noinspection HardCodedStringLiteral
        result.addAttribute(new Attribute("datetime", zonedDateTime.toOffsetDateTime().toString()));
        // Append the protocolParts element to the root element
        result.appendChild(createXMLBlocks());
        return result;
    }

    /**
     * Creates the XML blocks element including its children.
     *
     * @return the blocks element
     */
    private Element createXMLBlocks() {
        // Create the protocolParts element
        Element result = new Element(Constants.XML_PROTOCOL_BLOCKS);
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
                result = new Element(Constants.XML_PROTOCOL_BLOCK_VAR);
                break;
            case FIX:
                result = new Element(Constants.XML_PROTOCOL_BLOCK_FIX);
                break;
        }
        result.addAttribute(new Attribute(Constants.XML_PROTOCOL_MINLENGTH, String.valueOf(protocolBlock.getMinLength
                ())));
        result.addAttribute(new Attribute(Constants.XML_PROTOCOL_MAXLENGTH, String.valueOf(protocolBlock.getMaxLength
                ())));
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
        Element result = new Element(Constants.XML_PROTOCOL_CONTENT);
        // Append byte elements to the content element
        StringBuilder content = new StringBuilder(bytes.length * 2);
        for (Byte each : bytes) {
            content.append(Hex.byte2HexBin(each));
        }
        result.appendChild(content.toString());
        return result;
    }

    /**
     * Checks whether the protocol blocks are already exported to a XML file.
     *
     * @return true when protocol blocks are successfully written to file
     */
    public boolean isExported() {
        return exported;
    }
}
