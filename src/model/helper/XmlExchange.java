/*
 * This file is part of ProDisFuzz, modified on 01.03.14 10:47.
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
import java.io.IOException;
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
}
