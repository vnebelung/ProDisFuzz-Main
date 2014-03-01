/*
 * This file is part of ProDisFuzz, modified on 01.03.14 15:36.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.export;

import model.Model;
import model.ProtocolPart;
import model.helper.Constants;
import model.helper.Hex;
import model.helper.XmlExchange;
import model.process.AbstractProcess;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExportProcess extends AbstractProcess {

    private List<ProtocolPart> protocolParts;
    private boolean exported;

    /**
     * Instantiates a new process responsible for exporting the protocol structure to a XML file.
     */
    public ExportProcess() {
        super();
        protocolParts = new ArrayList<>();
    }

    @Override
    public void reset() {
        exported = false;
        protocolParts.clear();
        spreadUpdate();
    }

    @Override
    public void init() {
        protocolParts = new ArrayList<>(Model.INSTANCE.getLearnProcess().getProtocolParts());
        exported = false;
        spreadUpdate();
    }

    /**
     * Exports the protocol structure to the given XML file.
     *
     * @param path the export file
     */
    public void exportXML(Path path) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            document.appendChild(createXMLRoot(document));
            exported = XmlExchange.exportXML(document, path);
        } catch (ParserConfigurationException e) {
            Model.INSTANCE.getLogger().error(e);
            exported = false;
        }
        spreadUpdate();
    }

    /**
     * Creates the XML root node with all children.
     *
     * @param document the DOM document
     * @return the root node
     */
    private Element createXMLRoot(Document document) {
        Element result = document.createElement(Constants.XML_PROTOCOL_ROOT);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        String date = dateFormat.format(new Date());
        // The time zone is separated with a colon (standardized)
        date = date.substring(0, date.length() - 2) + ":" + date.substring(date.length() - 2);
        result.setAttribute("datetime", date);
        // Append the protocolParts element to the root element
        result.appendChild(createXMLParts(document));
        return result;
    }

    /**
     * Creates the XML parts node with all children.
     *
     * @param document the DOM document
     * @return the parts node
     */
    private Element createXMLParts(Document document) {
        // Create the protocolParts element
        Element result = document.createElement(Constants.XML_PROTOCOL_PARTS);
        // Append individual part elements to the protocolParts element
        for (ProtocolPart each : protocolParts) {
            result.appendChild(createXMLPart(document, each));
        }
        return result;
    }

    /**
     * Creates a XML part node with all children.
     *
     * @param document     the DOM document
     * @param protocolPart the protocol part
     * @return the part node
     */
    private Element createXMLPart(Document document, ProtocolPart protocolPart) {
        Element result = null;
        switch (protocolPart.getType()) {
            case VAR:
                result = document.createElement(Constants.XML_PROTOCOL_PART_VAR);
                break;
            case FIX:
                result = document.createElement(Constants.XML_PROTOCOL_PART_FIX);
                break;
            default:
                // Should not happen
                break;
        }
        result.setAttribute(Constants.XML_PROTOCOL_MINLENGTH, String.valueOf(protocolPart.getMinLength()));
        result.setAttribute(Constants.XML_PROTOCOL_MAXLENGTH, String.valueOf(protocolPart.getMaxLength()));
        // Append content element to the part element
        if (protocolPart.getType() == ProtocolPart.Type.FIX) {
            result.appendChild(createXMLContent(document, protocolPart.getBytes()));
        }
        return result;
    }

    /**
     * Creates a XML content node with all children.
     *
     * @param d     the DOM document
     * @param bytes the byte content of a protocol part
     * @return the content node
     */
    private Element createXMLContent(Document d, List<Byte> bytes) {
        Element result = d.createElement(Constants.XML_PROTOCOL_CONTENT);
        // Append byte elements to the content element
        StringBuilder content = new StringBuilder(bytes.size() * 2);
        for (Byte each : bytes) {
            content.append(Hex.byte2Hex(each));
        }
        result.setTextContent(content.toString());
        return result;
    }

    /**
     * Checks whether the protocol parts are already exported to a XML file.
     *
     * @return true when protocol parts are successfully written to file
     */
    public boolean isExported() {
        return exported;
    }
}
