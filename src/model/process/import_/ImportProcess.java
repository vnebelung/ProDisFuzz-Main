/*
 * This file is part of ProDisFuzz, modified on 28.03.14 18:39.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.import_;

import model.Model;
import model.ProtocolPart;
import model.helper.Constants;
import model.helper.Hex;
import model.helper.XmlExchange;
import model.process.AbstractProcess;
import model.xml.XmlSchemaValidator;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

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
        if (!XmlSchemaValidator.validateProtocol(file)) {
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
        protocolParts = readXMLParts(document);
        imported = true;
        spreadUpdate();
        Model.INSTANCE.getLogger().info("XML file successfully imported");
    }

    /**
     * Reads the XML part elements.
     *
     * @param document the DOM document
     * @return the protocol parts
     */
    private List<ProtocolPart> readXMLParts(Document document) {
        List<ProtocolPart> result = new ArrayList<>();
        // Create the node list

        Elements elements = document.getRootElement().getChildElements(Constants.XML_PROTOCOL_PARTS).get(0)
                .getChildElements();
        // Create for each node the particular protocol part
        for (int i = 0; i < elements.size(); i++) {
            switch (elements.get(i).getLocalName()) {
                case Constants.XML_PROTOCOL_PART_VAR:
                    result.add(new ProtocolPart(ProtocolPart.Type.VAR, readXMLContent(elements.get(i))));
                    break;
                case Constants.XML_PROTOCOL_PART_FIX:
                    result.add(new ProtocolPart(ProtocolPart.Type.FIX, readXMLContent(elements.get(i))));
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
     * @param element the protocol part XML element
     * @return the byte content
     */
    private List<Byte> readXMLContent(Element element) {
        List<Byte> result = new ArrayList<>();
        switch (element.getLocalName()) {
            case Constants.XML_PROTOCOL_PART_VAR:
                // Add as many null bytes as the maximum length attribute
                int maxLength = Integer.parseInt(element.getAttribute(Constants.XML_PROTOCOL_MAXLENGTH).getValue());
                for (int i = 0; i < maxLength; i++) {
                    result.add(null);
                }
                break;
            case Constants.XML_PROTOCOL_PART_FIX:
                Elements elements = element.getChildElements();
                // Create the content of this part for each content element
                for (int i = 0; i < elements.size(); i++) {
                    if (!elements.get(i).getLocalName().equals(Constants.XML_PROTOCOL_CONTENT)) {
                        continue;
                    }
                    // Create the content out of all byte elements
                    String bytes = elements.get(i).getValue();
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
