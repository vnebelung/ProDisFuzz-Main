/*
 * This file is part of ProDisFuzz, modified on 01.03.14 10:47.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.xml;

import model.Model;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public abstract class XmlSchemaValidator {

    /**
     * Validates an XML file containing the update information against the schema.
     *
     * @param path the path to the update information file
     * @return true, if the XML update information file is validated against the schema without any errors
     */
    public static boolean validateUpdateCheck(Path path) {
        Source source = new StreamSource(path.toFile());
        return validate(source, new String[]{"xmldsig-core-schema.xsd", "update.xsd"});
    }

    /**
     * Validates an XML file containing the protocol description against the schema.
     *
     * @param path the path to the XML protocol file
     * @return true, if the XML protocol file is validated against the schema without any errors
     */
    public static boolean validateProtocol(Path path) {
        Source source = new StreamSource(path.toFile());
        return validate(source, new String[]{"protocol.xsd"});
    }

    /**
     * Validates the given XML file against the given schemes.
     *
     * @param xml         the XML source to be validated.
     * @param schemaNames the file names of the schemes
     * @return true, if the XML file is validated against the schema without any errors
     */
    private static boolean validate(Source xml, String[] schemaNames) {
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
        InputStream[] schemaStreams = new InputStream[schemaNames.length];
        try {
            for (int i = 0; i < schemaStreams.length; i++) {
                schemaStreams[i] = XmlSchemaValidator.class.getResourceAsStream(schemaNames[i]);
            }
            Source[] schemaSources = new Source[schemaNames.length];
            for (int i = 0; i < schemaSources.length; i++) {
                schemaSources[i] = new StreamSource(schemaStreams[i]);
            }
            Validator validator = schemaFactory.newSchema(schemaSources).newValidator();
            validator.setErrorHandler(errorHandler);
            // Validate the whole XML document
            validator.validate(xml);
        } catch (IOException | SAXException e) {
            Model.INSTANCE.getLogger().error(e);
            return false;
        } finally {
            for (InputStream each : schemaStreams) {
                try {
                    each.close();
                } catch (IOException e) {
                    // Should not happen
                }
            }
        }
        return result[0];
    }

}
