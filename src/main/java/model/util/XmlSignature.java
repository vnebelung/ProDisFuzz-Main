/*
 * This file is part of ProDisFuzz, modified on 6/26/15 9:26 PM.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.util;

import model.Model;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.ParsingException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

public enum XmlSignature {
    ;

    /**
     * Validates the signature of the given XML document.
     *
     * @param path the path to the XML document which signature shall be validated. The document must contain an element
     *             signature at its last element that contains all necessary information. Every XML that validates
     *             against update.xsd can be used as an input.
     * @return true, if the XML file's signature is valid without any errors
     */
    public static boolean validate(Path path) {
        try {
            Document document = normalize(path);
            String xml = getSignedRoot(document);
            //noinspection HardCodedStringLiteral
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(Keys.getUpdatePublicKey());
            signature.update(xml.getBytes(StandardCharsets.UTF_8));
            String signatureElement = document.getRootElement().getFirstChildElement(Constants.XML_SIGNATURE)
                    .getValue();
            return signature.verify(Hex.hexBin2Byte(signatureElement));
        } catch (ParsingException | IOException | NoSuchAlgorithmException | SignatureException | InvalidKeyException
                e) {
            Model.INSTANCE.getLogger().error(e);
            return false;
        }
    }

    /**
     * Returns the element of the given XML document which signature shall be verified. This element itself and all of
     * its children are returned as a string retaining their XML structure. The XML string does not contain any indents
     * or white space but only the payload data. This element is defined as the first child element of the document's
     * root element.
     *
     * @param document the XML document
     * @return the resulting XML string
     */
    private static String getSignedRoot(Document document) {
        Element root = document.getRootElement().getChildElements().get(0);
        return root.toXML();
    }

    /**
     * Normalizes an XML file specified through the given path. Normalization means that the XML structure will be
     * stripped of unnecessary white space.
     *
     * @param path the path to the XML file
     * @return the normailzed XML document
     * @throws ParsingException
     * @throws IOException
     */
    @SuppressWarnings("OverlyBroadThrowsClause")
    private static Document normalize(Path path) throws ParsingException, IOException {
        Builder parser = new Builder(new XmlWhiteSpaceEliminator());
        return parser.build(path.toFile());
    }

}
