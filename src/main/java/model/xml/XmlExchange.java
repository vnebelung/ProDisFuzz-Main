/*
 * This file is part of ProDisFuzz, modified on 30.03.14 17:49.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.xml;

import model.Model;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.Serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class XmlExchange {

    /**
     * Imports a given XML file and returns the parsed XOM document.
     *
     * @param path the path to the XML file
     * @return the parsed document or null in case of an error
     */
    public static Document importXml(Path path) {
        try {
            Builder parser = new Builder();
            return parser.build(path.toFile());
        } catch (ParsingException | IOException e) {
            Model.INSTANCE.getLogger().error(e);
            return null;
        }
    }

    /**
     * Exports the given XOM document to the given XML file path.
     *
     * @param path     the path to the file
     * @param document the XOM document to export
     * @return true, if the XML structure was successfully exported
     */
    public static boolean exportXML(Document document, Path path) {
        Path exportPath = path.toAbsolutePath().normalize();
        if (!Files.isDirectory(exportPath.getParent())) {
            Model.INSTANCE.getLogger().error("Path '" + exportPath.toString() + "' for saving file invalid");
            return false;
        }
        if (!Files.isWritable(exportPath.getParent())) {
            Model.INSTANCE.getLogger().error("Path '" + exportPath.getParent().toString() + "' not writable");
            return false;
        }
        if (Files.isDirectory(exportPath)) {
            Model.INSTANCE.getLogger().error("Path '" + exportPath.getParent().toString() + "' is a directory");
            return false;
        }
        try (OutputStream outputStream = Files.newOutputStream(exportPath)) {
            Serializer serializer = new Serializer(outputStream, "UTF-8");
            serializer.setIndent(4);
            serializer.write(document);
        } catch (IOException e) {
            Model.INSTANCE.getLogger().error(e);
            return false;
        }
        Model.INSTANCE.getLogger().fine("File saved to '" + exportPath.toString() + "'");
        return true;
    }
}
