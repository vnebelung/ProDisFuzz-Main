/*
 * This file is part of ProDisFuzz, modified on 01.03.14 12:21.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.helper;

import model.Model;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
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

public abstract class XmlExchange {

    /**
     * Imports a given XML file and returns the parsed document.
     *
     * @param path the path to the XML file
     * @return the parsed document or null in case of an error
     */
    public static Document importXml(Path path) {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            return documentBuilder.parse(path.toFile());

        } catch (ParserConfigurationException | IOException | SAXException e) {
            Model.INSTANCE.getLogger().error(e);
            return null;
        }
    }

    /**
     * Exports the given DOM document to the given XML file path.
     *
     * @param path     the path to the file
     * @param document the DOM document to export
     * @return true, if the XML structure was successfully exported
     */
    public static boolean exportXML(Document document, Path path) {
        Path exportPath = path.toAbsolutePath().normalize();
        if (!Files.isDirectory(exportPath.getParent())) {
            Model.INSTANCE.getLogger().error("Path '" + exportPath.toString() + "' for saving XML file invalid");
            return false;
        }
        if (!Files.isWritable(exportPath.getParent())) {
            Model.INSTANCE.getLogger().error("File path for saving protocol structure not writable");
            return false;
        }
        try {
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(Files.newOutputStream(exportPath));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            // Indent the elements in the XML structure by 2 spaces
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            // Transform the DOM TO XML
            transformer.transform(source, result);
            Model.INSTANCE.getLogger().info("XML file saved to '" + exportPath.toString() + "'");
            return true;
        } catch (TransformerException | IOException e) {
            Model.INSTANCE.getLogger().error(e);
            return false;
        }
    }
}
