/*
 * This file is part of ProDisFuzz, modified on 01.10.13 23:25.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import model.ProtocolPart;
import model.logger.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import xml.XmlNames;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImportProcess extends AbstractProcess {

    private List<ProtocolPart> protocolParts;
    private boolean imported;

    /**
     * Instantiates a new import process.
     */
    public ImportProcess() {
        super();
        protocolParts = new ArrayList<>();
    }

    @Override
    public void reset() {
        imported = false;
        protocolParts.clear();
        spreadUpdate();
    }

    /**
     * Imports a XML file containing the protocol structure.
     *
     * @param path the path to the XML file
     */
    public void importFile(final String path) {
        final Path file = Paths.get(path).toAbsolutePath().normalize();
        if (!Files.isRegularFile(file)) {
            Logger.getInstance().error("File '" + file.toString() + "' is not a regular file");
            imported = false;
            spreadUpdate();
            return;
        }
        if (!Files.isReadable(file)) {
            Logger.getInstance().error("File '" + file.toString() + "' is not readable");
            imported = false;
            spreadUpdate();
            return;
        }
        try {
            if (!validate(file)) {
                imported = false;
                spreadUpdate();
                return;
            }
            final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setNamespaceAware(true);
            final Document document = docBuilderFactory.newDocumentBuilder().parse(Files.newInputStream(file));
            protocolParts = readXMLParts(document);
            imported = true;
            spreadUpdate();
            Logger.getInstance().info("XML file successfully imported");
        } catch (IOException | SAXException | ParserConfigurationException e) {
            Logger.getInstance().error(e);
            imported = false;
            spreadUpdate();
        }
    }

    /**
     * Reads the XML part elements.
     *
     * @param document the DOM document
     * @return the protocol parts
     */
    private List<ProtocolPart> readXMLParts(final Document document) {
        final List<ProtocolPart> parts = new ArrayList<>();
        // Create the node list
        final NodeList nodes = document.getElementsByTagName(XmlNames.PARTS).item(0).getChildNodes();
        // Create for each node the particular protocol part
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (nodes.item(i).getNodeName()) {
                case XmlNames.PART_VAR:
                    parts.add(new ProtocolPart(ProtocolPart.Type.VAR, readXMLContent(nodes.item(i))));
                    break;
                case XmlNames.PART_FIX:
                    parts.add(new ProtocolPart(ProtocolPart.Type.FIX, readXMLContent(nodes.item(i))));
                    break;
                default:
                    break;
            }
        }
        return parts;
    }

    /**
     * Reads the byte content for a given XML protocol part node.
     *
     * @param node the protocol part node
     * @return the byte content
     */
    private List<Byte> readXMLContent(final Node node) {
        final List<Byte> bytes = new ArrayList<>();
        switch (node.getNodeName()) {
            case XmlNames.PART_VAR:
                // Add as many null bytes as the maximum length attribute
                final int maxlength = Integer.parseInt(node.getAttributes().getNamedItem(XmlNames.MAXLENGTH)
                        .getNodeValue());
                for (int i = 0; i < maxlength; i++) {
                    bytes.add(null);
                }
                break;
            case XmlNames.PART_FIX:
                final NodeList contentNodes = node.getChildNodes();
                // Create the content of this part for each content element
                for (int i = 0; i < contentNodes.getLength(); i++) {
                    final Node contentNode = contentNodes.item(i);
                    if (contentNode.getNodeType() != Node.ELEMENT_NODE || !contentNode.getNodeName().equals(XmlNames
                            .CONTENT)) {
                        continue;
                    }
                    // Create the content out of all byte elements
                    for (int j = 0; j < contentNode.getChildNodes().getLength(); j++) {
                        final Node byteNode = contentNode.getChildNodes().item(j);
                        if (byteNode.getNodeType() != Node.ELEMENT_NODE || !byteNode.getNodeName().equals(XmlNames
                                .BYTE)) {
                            continue;
                        }
                        bytes.add(Byte.parseByte(byteNode.getTextContent()));
                    }
                }
                break;
            default:
                break;
        }
        return bytes;
    }

    /**
     * Validates the XML file against the defined schema.
     *
     * @param file the XML file
     * @return true if the XML file is validated without any errors
     * @throws IOException
     * @throws SAXException
     */
    private boolean validate(final Path file) throws IOException, SAXException {
        final boolean[] success = {true};
        // Initializes the error handler
        final ErrorHandler errorHandler = new ErrorHandler() {
            @Override
            public void warning(final SAXParseException e) throws SAXException {
                Logger.getInstance().warning(e.getMessage());
                success[0] = false;
            }

            @Override
            public void fatalError(final SAXParseException e) throws SAXException {
                Logger.getInstance().error(e);
                success[0] = false;
            }

            @Override
            public void error(final SAXParseException e) throws SAXException {
                Logger.getInstance().error(e);
                success[0] = false;
            }
        };
        // Load the XML schema
        final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("xml/schema.xsd")) {
            final Schema schema = schemaFactory.newSchema(new StreamSource(stream));
            // Attach schema to validator
            final Validator validator = schema.newValidator();
            validator.setErrorHandler(errorHandler);
            // Validate the whole XML document
            validator.validate(new StreamSource(Files.newInputStream(file)));
        }
        return success[0];
    }

    /**
     * Checks whether the protocol parts are already imported from a XML file.
     *
     * @return true when protocol parts are successfully written to file
     */
    public boolean isImported() {
        return imported;
    }

    /**
     * Gets the imported protocol parts.
     *
     * @return the protocol parts
     */
    public List<ProtocolPart> getProtocolParts() {
        return Collections.unmodifiableList(protocolParts);
    }
}
