/*
 * This file is part of ProDisFuzz, modified on 08.02.14 23:13.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.import_;

import model.Model;
import model.ProtocolPart;
import model.helper.Hex;
import model.process.AbstractProcess;
import model.xml.XmlNames;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

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
    public void importXML(Path p) {
        Path file = p.toAbsolutePath().normalize();
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
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilderFactory.setNamespaceAware(true);
            Document document = docBuilderFactory.newDocumentBuilder().parse(Files.newInputStream(file));
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
    private List<ProtocolPart> readXMLParts(Document d) {
        List<ProtocolPart> result = new ArrayList<>();
        // Create the node list
        NodeList nodes = d.getElementsByTagName(XmlNames.PROTOCOL_PARTS).item(0).getChildNodes();
        // Create for each node the particular protocol part
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            switch (nodes.item(i).getNodeName()) {
                case XmlNames.PROTOCOL_PART_VAR:
                    result.add(new ProtocolPart(ProtocolPart.Type.VAR, readXMLContent(nodes.item(i))));
                    break;
                case XmlNames.PROTOCOL_PART_FIX:
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
    private List<Byte> readXMLContent(Node n) {
        List<Byte> result = new ArrayList<>();
        switch (n.getNodeName()) {
            case XmlNames.PROTOCOL_PART_VAR:
                // Add as many null bytes as the maximum length attribute
                int maxLength = Integer.parseInt(n.getAttributes().getNamedItem(XmlNames.PROTOCOL_MAXLENGTH)
                        .getNodeValue());
                for (int i = 0; i < maxLength; i++) {
                    result.add(null);
                }
                break;
            case XmlNames.PROTOCOL_PART_FIX:
                NodeList nodes = n.getChildNodes();
                // Create the content of this part for each content element
                for (int i = 0; i < nodes.getLength(); i++) {
                    Node node = nodes.item(i);
                    if (node.getNodeType() != Node.ELEMENT_NODE || !node.getNodeName().equals(XmlNames
                            .PROTOCOL_CONTENT)) {
                        continue;
                    }
                    // Create the content out of all byte elements
                    String bytes = node.getTextContent();
                    for (int j = 0; j < bytes.length(); j = j + 2) {
                        result.add(Hex.hex2Byte(bytes.substring(j, j + 2)));
                    }
                }
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * Validates the XML file against the defined schema.
     *
     * @param p the path of the XML file
     * @return true if the XML file is validated without any errors
     * @throws IOException
     * @throws SAXException
     */
    private boolean validate(Path p) throws IOException, SAXException {
        final boolean[] result = {true};
        // Initializes the error handler
        ErrorHandler errorHandler = new ErrorHandler() {
            @Override
            public void warning(SAXParseException e) throws SAXException {
                Model.INSTANCE.getLogger().warning(e.getMessage());
                result[0] = false;
            }

            @Override
            public void fatalError(SAXParseException e) throws SAXException {
                Model.INSTANCE.getLogger().error(e);
                result[0] = false;
            }

            @Override
            public void error(SAXParseException e) throws SAXException {
                Model.INSTANCE.getLogger().error(e);
                result[0] = false;
            }
        };
        // Load the XML schema
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("model/xml/protocol.xsd")) {
            Schema schema = schemaFactory.newSchema(new StreamSource(stream));
            // Attach schema to validator
            Validator validator = schema.newValidator();
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
