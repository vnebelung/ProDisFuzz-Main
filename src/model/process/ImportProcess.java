/*
 * This file is part of ProDisFuzz, modified on 11.10.13 22:35.
 * Copyright (c) 2013 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process;

import model.Model;
import model.ProtocolPart;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImportProcess extends AbstractProcess {

    private List<ProtocolPart> protocolParts;
    private boolean imported;

    /**
     * Instantiates a new process responsible for importing the XML file to generate the protocol structure.
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
     * @param p the path to the XML file
     */
    public void importFile(final Path p) {
        final Path file = p.toAbsolutePath().normalize();
        if (!Files.isRegularFile(file)) {
            Model.INSTANCE.getLogger().error("File '" + file.toString() + "' is not a regular file");
            imported = false;
            spreadUpdate();
            return;
        }
        if (!Files.isReadable(file)) {
            Model.INSTANCE.getLogger().error("File '" + file.toString() + "' is not readable");
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
            Model.INSTANCE.getLogger().info("XML file successfully imported");
        } catch (IOException | SAXException | ParserConfigurationException e) {
            Model.INSTANCE.getLogger().error(e);
            imported = false;
            spreadUpdate();
        }
    }

    /**
     * Reads the XML part elements.
     *
     * @param d the DOM document
     * @return the protocol parts
     */
    private List<ProtocolPart> readXMLParts(final Document d) {
        final List<ProtocolPart> result = new ArrayList<>();
        // Create the node list
        final NodeList nodes = d.getElementsByTagName(XmlNames.PARTS).item(0).getChildNodes();
        // Create for each node the particular protocol part
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (nodes.item(i).getNodeName()) {
                case XmlNames.PART_VAR:
                    result.add(new ProtocolPart(ProtocolPart.Type.VAR, readXMLContent(nodes.item(i))));
                    break;
                case XmlNames.PART_FIX:
                    result.add(new ProtocolPart(ProtocolPart.Type.FIX, readXMLContent(nodes.item(i))));
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    /**
     * Reads the byte content for a given XML protocol part node.
     *
     * @param n the protocol part node
     * @return the byte content
     */
    private List<Byte> readXMLContent(final Node n) {
        final List<Byte> bytes = new ArrayList<>();
        switch (n.getNodeName()) {
            case XmlNames.PART_VAR:
                // Add as many null bytes as the maximum length attribute
                final int maxlength = Integer.parseInt(n.getAttributes().getNamedItem(XmlNames.MAXLENGTH)
                        .getNodeValue());
                for (int i = 0; i < maxlength; i++) {
                    bytes.add(null);
                }
                break;
            case XmlNames.PART_FIX:
                final NodeList contentNodes = n.getChildNodes();
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
     * @param p the path of the XML file
     * @return true if the XML file is validated without any errors
     * @throws IOException
     * @throws SAXException
     */
    private boolean validate(final Path p) throws IOException, SAXException {
        final boolean[] result = {true};
        // Initializes the error handler
        final ErrorHandler errorHandler = new ErrorHandler() {
            @Override
            public void warning(final SAXParseException e) throws SAXException {
                Model.INSTANCE.getLogger().warning(e.getMessage());
                result[0] = false;
            }

            @Override
            public void fatalError(final SAXParseException e) throws SAXException {
                Model.INSTANCE.getLogger().error(e);
                result[0] = false;
            }

            @Override
            public void error(final SAXParseException e) throws SAXException {
                Model.INSTANCE.getLogger().error(e);
                result[0] = false;
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
            validator.validate(new StreamSource(Files.newInputStream(p)));
        }
        return result[0];
    }

    /**
     * Checks whether the protocol parts are already imported from a XML file.
     *
     * @return true when protocol parts are successfully read from the file
     */
    public boolean isImported() {
        return imported;
    }

    /**
     * Returns the protocol parts imported from a XML file.
     *
     * @return the protocol parts
     */
    public List<ProtocolPart> getProtocolParts() {
        return Collections.unmodifiableList(protocolParts);
    }
}
