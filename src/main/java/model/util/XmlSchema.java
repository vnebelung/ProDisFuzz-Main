/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.util;

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

@SuppressWarnings("ClassIndependentOfModule")
public enum XmlSchema {
    ;

    /**
     * Validates an XML file containing the update information against the schema.
     *
     * @param path the path to the update information file
     * @return true, if the XML update information file is validated against the schema without any errors
     */
    public static boolean validateUpdateInformation(Path path) {
        Source source = new StreamSource(path.toFile());
        // noinspection HardCodedStringLiteral
        return validate(source, "/xml/update.xsd");
    }

    /**
     * Validates an XML file containing the protocol description against the schema.
     *
     * @param path the path to the XML protocol file
     * @return true, if the XML protocol file is validated against the schema without any errors
     */
    public static boolean validateProtocol(Path path) {
        Source source = new StreamSource(path.toFile());
        //noinspection HardCodedStringLiteral
        return validate(source, "/xml/protocol.xsd");
    }

    /**
     * Validates the given XML file against the given schema.
     *
     * @param xml        the XML source to be validated.
     * @param schemaName the file name of the schema
     * @return true, if the XML file is validated against the schema without any errors
     */
    private static boolean validate(Source xml, String schemaName) {
        boolean[] result = {true};
        // Initializes the error handler
        ErrorHandler errorHandler = new ErrorHandler() {
            @Override
            public void warning(SAXParseException exception) {
                Model.INSTANCE.getLogger().warning(exception.getMessage());
                result[0] = false;
            }

            @Override
            public void fatalError(SAXParseException exception) {
                Model.INSTANCE.getLogger().error(exception);
                result[0] = false;
            }

            @Override
            public void error(SAXParseException exception) {
                Model.INSTANCE.getLogger().error(exception);
                result[0] = false;
            }
        };
        // Load the XML schema
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try (InputStream inputStream = XmlSchema.class.getResourceAsStream(schemaName)) {
            Source source = new StreamSource(inputStream);
            Validator validator = schemaFactory.newSchema(source).newValidator();
            validator.setErrorHandler(errorHandler);
            // Validate the whole XML document
            validator.validate(xml);
        } catch (IOException | SAXException e) {
            Model.INSTANCE.getLogger().error(e);
            return false;
        }
        return result[0];
    }

}
