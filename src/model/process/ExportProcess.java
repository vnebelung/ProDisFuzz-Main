/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:43.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import model.Model;
import model.ProtocolPart;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import xml.XmlNames;

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
    public void export(final Path p) {
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
        if (!savePath.toString().endsWith(".xml")) {
            savePath = savePath.getParent().resolve(savePath.getFileName().toString() + ".xml");
        }
        try {
            final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            document.appendChild(createXMLRoot(document));
            final DOMSource source = new DOMSource(document);
            final StreamResult result = new StreamResult(Files.newOutputStream(savePath));
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
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
    private Element createXMLRoot(final Document d) {
        final Element root = d.createElement(XmlNames.ROOT);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        String date = dateFormat.format(new Date());
        // The time zone is separated with a colon (standardized)
        date = date.substring(0, date.length() - 2) + ":" + date.substring(date.length() - 2);
        root.setAttribute("datetime", date);
        // Append the protocolParts element to the root element
        root.appendChild(createXMLParts(d));
        return root;
    }

    /**
     * Creates the XML parts node with all children.
     *
     * @param d the DOM document
     * @return the parts node
     */
    private Element createXMLParts(final Document d) {
        // Create the protocolParts element
        final Element parts = d.createElement(XmlNames.PARTS);
        // Append individual part elements to the protocolParts element
        for (final ProtocolPart each : protocolParts) {
            parts.appendChild(createXMLPart(d, each));
        }
        return parts;
    }

    /**
     * Creates a XML part node with all children.
     *
     * @param d the DOM document
     * @param p the protocol part
     * @return the part node
     */
    private Element createXMLPart(final Document d, final ProtocolPart p) {
        Element part = null;
        switch (p.getType()) {
            case VAR:
                part = d.createElement(XmlNames.PART_VAR);
                break;
            case FIX:
                part = d.createElement(XmlNames.PART_FIX);
                break;
            default:
                break;
        }
        part.setAttribute(XmlNames.MINLENGTH, String.valueOf(p.getMinLength()));
        part.setAttribute(XmlNames.MAXLENGTH, String.valueOf(p.getMaxLength()));
        // Append content element to the part element
        if (p.getType() == ProtocolPart.Type.FIX) {
            part.appendChild(createXMLContent(d, p.getBytes()));
        }
        return part;
    }

    /**
     * Creates a XML content node with all children.
     *
     * @param d     the DOM document
     * @param bytes the byte content of a protocol part
     * @return the content node
     */
    private Element createXMLContent(final Document d, final List<Byte> bytes) {
        final Element content = d.createElement(XmlNames.CONTENT);
        // Append byte elements to the content element
        for (final Byte each : bytes) {
            content.appendChild(createXMLByte(d, each));
        }
        return content;
    }

    /**
     * Creates a XML byte node.
     *
     * @param d the DOM document
     * @param b a byte value (can be null)
     * @return the byte node
     */
    private Element createXMLByte(final Document d, final Byte b) {
        final Element byteElement = d.createElement(XmlNames.BYTE);
        byteElement.setTextContent(b.toString());
        return byteElement;
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
