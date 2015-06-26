/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.import_;

import model.Model;
import model.process.AbstractProcess;
import model.protocol.ProtocolStructure;
import model.util.Constants;
import model.util.Hex;
import model.util.XmlExchange;
import model.util.XmlSchema;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ImportProcess extends AbstractProcess {

    private ProtocolStructure protocolStructure;
    private boolean imported;

    /**
     * Instantiates a new process responsible for importing the XML file to generate the protocol structure.
     */
    public ImportProcess() {
        super();
        protocolStructure = new ProtocolStructure();
    }

    @Override
    public void reset() {
        imported = false;
        protocolStructure.clear();
        spreadUpdate();
    }

    /**
     * Imports a XML file containing the protocol structure.
     *
     * @param path the path to the XML file
     */
    public void importXML(Path path) {
        Path file = path.toAbsolutePath().normalize();
        if (!Files.isRegularFile(file)) {
            Model.INSTANCE.getLogger().error("File '" + file + "' is not a regular file");
            imported = false;
            spreadUpdate();
            return;
        }
        if (!Files.isReadable(file)) {
            Model.INSTANCE.getLogger().error("File '" + file + "' is not readable");
            imported = false;
            spreadUpdate();
            return;
        }
        if (!XmlSchema.validateProtocol(file)) {
            imported = false;
            spreadUpdate();
            return;
        }
        Document document = XmlExchange.importXml(file);
        if (document == null) {
            imported = false;
            spreadUpdate();
            return;
        }
        protocolStructure = readXMLBlocks(document);
        imported = true;
        spreadUpdate();
        Model.INSTANCE.getLogger().info("XML file successfully imported");
    }

    /**
     * Returns the XML block elements.
     *
     * @param document the XOM document
     * @return the protocol structure
     */
    private static ProtocolStructure readXMLBlocks(Document document) {
        ProtocolStructure result = new ProtocolStructure();
        // Create the node list
        Elements elements = document.getRootElement().getChildElements(Constants.XML_PROTOCOL_BLOCKS).get(0)
                .getChildElements();
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
            case Constants.XML_PROTOCOL_BLOCK_VAR:
                // Add as many null bytes as the maximum length attribute
                int maxLength = Integer.parseInt(element.getAttribute(Constants.XML_PROTOCOL_MAXLENGTH).getValue());
                for (int i = 0; i < maxLength; i++) {
                    result.add(null);
                }
                break;
            case Constants.XML_PROTOCOL_BLOCK_FIX:
                Elements elements = element.getChildElements();
                // Create the content of this block for each content element
                for (int i = 0; i < elements.size(); i++) {
                    if (!elements.get(i).getLocalName().equals(Constants.XML_PROTOCOL_CONTENT)) {
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

    /**
     * Checks whether the protocol blocks are already imported from a XML file.
     *
     * @return true when protocol blocks are successfully read from the file
     */
    public boolean isImported() {
        return imported;
    }

    /**
     * Returns the protocol structure imported from an XML file.
     *
     * @return the protocol structure
     */
    public ProtocolStructure getProtocolStructure() {
        return protocolStructure;
    }
}
