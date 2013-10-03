/*
 * This file is part of ProDisFuzz, modified on 03.10.13 22:24.
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
     * Instantiates a new export process.
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
     * Exports the protocol structure to the given XML export path.
     *
     * @param path the export path
     */
    public void export(final Path path) {
        Path savePath = path.toAbsolutePath().normalize();
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
     * Creates the root node with all children.
     *
     * @param document the DOM document
     * @return the root node
     */
    private Element createXMLRoot(final Document document) {
        final Element root = document.createElement(XmlNames.ROOT);
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
        String date = dateFormat.format(new Date());
        // The time zone is separated with a colon (standardized)
        date = date.substring(0, date.length() - 2) + ":" + date.substring(date.length() - 2);
        root.setAttribute("datetime", date);
        // Append the protocolParts element to the root element
        root.appendChild(createXMLParts(document));
        return root;
    }

    /**
     * Creates the parts node with all children.
     *
     * @param document the DOM document
     * @return the parts node
     */
    private Element createXMLParts(final Document document) {
        // Create the protocolParts element
        final Element parts = document.createElement(XmlNames.PARTS);
        // Append individual part elements to the protocolParts element
        for (final ProtocolPart protocolPart : protocolParts) {
            parts.appendChild(createXMLPart(document, protocolPart));
        }
        return parts;
    }

    /**
     * Creates a part node with all children.
     *
     * @param document     the DOM document
     * @param protocolPart the protocol part
     * @return the part node
     */
    private Element createXMLPart(final Document document, final ProtocolPart protocolPart) {
        Element part = null;
        switch (protocolPart.getType()) {
            case VAR:
                part = document.createElement(XmlNames.PART_VAR);
                break;
            case FIX:
                part = document.createElement(XmlNames.PART_FIX);
                break;
            default:
                break;
        }
        part.setAttribute(XmlNames.MINLENGTH, String.valueOf(protocolPart.getMinLength()));
        part.setAttribute(XmlNames.MAXLENGTH, String.valueOf(protocolPart.getMaxLength()));
        // Append content element to the part element
        if (protocolPart.getType() == ProtocolPart.Type.FIX) {
            part.appendChild(createXMLContent(document, protocolPart.getBytes()));
        }
        return part;
    }

    /**
     * Creates a content node with all children.
     *
     * @param document the DOM document
     * @param bytes    the byte content of a protocol part
     * @return the content node
     */
    private Element createXMLContent(final Document document, final List<Byte> bytes) {
        final Element content = document.createElement(XmlNames.CONTENT);
        // Append byte elements to the content element
        for (final Byte aByte : bytes) {
            content.appendChild(createXMLByte(document, aByte));
        }
        return content;
    }

    /**
     * Creates a byte node.
     *
     * @param document the DOM document
     * @param aByte    a byte value (can be null)
     * @return the byte node
     */
    private Element createXMLByte(final Document document, final Byte aByte) {
        final Element byteElement = document.createElement(XmlNames.BYTE);
        byteElement.setTextContent(aByte.toString());
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
