/*
 * This file is part of ProDisFuzz, modified on 08.02.14 23:31.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.export;

import model.Model;
import model.ProtocolPart;
import model.helper.Hex;
import model.process.AbstractProcess;
import model.xml.XmlNames;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.nio.file.Files;
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
     * @param p the export file
     */
    public void exportXML(Path p) {
        Path savePath = p.toAbsolutePath().normalize();
        if (!Files.isDirectory(savePath.getParent())) {
            Model.INSTANCE.getLogger().error("File path for saving protocol structure invalid");
            exported = false;
            spreadUpdate();
            return;
        }
        if (!Files.isWritable(savePath.getParent())) {
            Model.INSTANCE.getLogger().error("File path for saving protocol structure not writable");
            exported = false;
            spreadUpdate();
            return;
        }
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            document.appendChild(createXMLRoot(document));
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(Files.newOutputStream(savePath));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            // Indent the elements in the XML structure by 2 spaces
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            // Transform the DOM TO XML
            transformer.transform(source, result);
            exported = true;
            spreadUpdate();
            Model.INSTANCE.getLogger().info("XML file saved to '" + savePath.toString() + "'");
        } catch (ParserConfigurationException | TransformerException | IOException e) {
            exported = false;
            spreadUpdate();
            Model.INSTANCE.getLogger().error(e);
        }
    }

    /**
     * Creates the XML root node with all children.
     *
     * @param d the DOM document
     * @return the root node
     */
    private Element createXMLRoot(Document d) {
        Element result = d.createElement(XmlNames.PROTOCOL_ROOT);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        String date = dateFormat.format(new Date());
        // The time zone is separated with a colon (standardized)
        date = date.substring(0, date.length() - 2) + ":" + date.substring(date.length() - 2);
        result.setAttribute("datetime", date);
        // Append the protocolParts element to the root element
        result.appendChild(createXMLParts(d));
        return result;
    }

    /**
     * Creates the XML parts node with all children.
     *
     * @param d the DOM document
     * @return the parts node
     */
    private Element createXMLParts(Document d) {
        // Create the protocolParts element
        Element result = d.createElement(XmlNames.PROTOCOL_PARTS);
        // Append individual part elements to the protocolParts element
        for (ProtocolPart each : protocolParts) {
            result.appendChild(createXMLPart(d, each));
        }
        return result;
    }

    /**
     * Creates a XML part node with all children.
     *
     * @param d the DOM document
     * @param p the protocol part
     * @return the part node
     */
    private Element createXMLPart(Document d, ProtocolPart p) {
        Element result = null;
        switch (p.getType()) {
            case VAR:
                result = d.createElement(XmlNames.PROTOCOL_PART_VAR);
                break;
            case FIX:
                result = d.createElement(XmlNames.PROTOCOL_PART_FIX);
                break;
            default:
                // Should not happen
                break;
        }
        result.setAttribute(XmlNames.PROTOCOL_MINLENGTH, String.valueOf(p.getMinLength()));
        result.setAttribute(XmlNames.PROTOCOL_MAXLENGTH, String.valueOf(p.getMaxLength()));
        // Append content element to the part element
        if (p.getType() == ProtocolPart.Type.FIX) {
            result.appendChild(createXMLContent(d, p.getBytes()));
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
        Element result = d.createElement(XmlNames.PROTOCOL_CONTENT);
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
