/*
 * This file is part of ProDisFuzz, modified on 30.03.14 17:49.
 * Copyright (c) 2013-2014 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.updater;

import model.Model;
import model.helper.Constants;
import model.helper.Keys;
import model.xml.XmlExchange;
import model.xml.XmlSchemaValidator;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;
import nu.xom.converters.DOMConverter;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpdateCheck {

    private ReleaseInformation[] releaseInformation;

    /**
     * Checks whether the remote server prodisfuzz.net has a newer version available for download.
     *
     * @return true, if there is a newer version available for download
     */
    public boolean hasUpdate() {
        URL url;
        try {
            url = new URL("http://prodisfuzz.net/updater/releases.xml");
        } catch (MalformedURLException e) {
            // Should not happen
            Model.INSTANCE.getLogger().error(e);
            return false;
        }
        Path xmlPath = downloadXML(url);
        if (xmlPath == null) {
            Model.INSTANCE.getLogger().error("ProDisFuzz could not receive the update information from '" + url
                    .toString() + "'. Please check manually for an update.");
            return false;
        }
        if (!XmlSchemaValidator.validateUpdateCheck(xmlPath)) {
            Model.INSTANCE.getLogger().error("ProDisFuzz could validate the format of the XML file containing the " +
                    "update information. Please check manually for an update.");
            return false;
        }
        Document document = XmlExchange.importXml(xmlPath);
        if (document == null) {
            return false;
        }
        if (!verifyIntegrity(document)) {
            Model.INSTANCE.getLogger().error("ProDisFuzz could not verify the integrity of the update information at " +
                    "'" + url.toString() + "'. This is strange. Please check manually for an update.");
            return false;
        }
        releaseInformation = readNewReleases(document);
        Arrays.sort(releaseInformation);
        boolean result = releaseInformation.length > 0;
        if (result) {
            Model.INSTANCE.getLogger().warning("ProDisFuzz update available. Please go to 'http://prodisfuzz.net'");
        } else {
            Model.INSTANCE.getLogger().fine("ProDisFuzz is up to date.");
        }
        return result;
    }

    /**
     * Returns information about all releases found in the XML document that are newer than the current release of
     * ProDisFuzz.
     *
     * @param document the XOM document
     * @return the information about all newer releases
     */
    private ReleaseInformation[] readNewReleases(Document document) {
        List<ReleaseInformation> result = new ArrayList<>();
        Elements elements = document.getRootElement().getChildElements("release", Constants.XML_NAMESPACE_PRODISFUZZ);
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            int number = Integer.parseInt(element.getFirstChildElement("number", Constants.XML_NAMESPACE_PRODISFUZZ)
                    .getValue());
            if (number <= Constants.RELEASE_NUMBER) {
                continue;
            }
            String name = element.getFirstChildElement("name", Constants.XML_NAMESPACE_PRODISFUZZ).getValue();
            String date = element.getFirstChildElement("date", Constants.XML_NAMESPACE_PRODISFUZZ).getValue();
            String requirements = element.getFirstChildElement("requirements", Constants.XML_NAMESPACE_PRODISFUZZ)
                    .getValue();
            Elements items = element.getFirstChildElement("information", Constants.XML_NAMESPACE_PRODISFUZZ)
                    .getChildElements("item", Constants.XML_NAMESPACE_PRODISFUZZ);
            String[] information = new String[items.size()];
            for (int j = 0; j < items.size(); j++) {
                information[j] = items.get(j).getValue();
            }
            result.add(new ReleaseInformation(number, name, date, requirements, information));
        }
        return result.toArray(new ReleaseInformation[result.size()]);
    }

    /**
     * Verifies the integrity of the XML document by checking its XML signature.
     *
     * @param document the DOM document
     * @return true, if the integrity could be verified
     */
    private boolean verifyIntegrity(Document document) {
        PublicKey publicKey = Keys.getUpdatePublicKey();
        if (publicKey == null) {
            return false;
        }
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            DOMImplementation domImplementation = documentBuilder.getDOMImplementation();
            org.w3c.dom.Document domDocument = DOMConverter.convert(document, domImplementation);
            Node signatureNode = domDocument.getElementsByTagName("Signature").item(0);
            DOMValidateContext valContext = new DOMValidateContext(publicKey, signatureNode);
            XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");
            XMLSignature signature = xmlSignatureFactory.unmarshalXMLSignature(valContext);
            return signature.validate(valContext);
        } catch (MarshalException | XMLSignatureException | ParserConfigurationException e) {
            Model.INSTANCE.getLogger().error(e);
            return false;
        }
    }

    /**
     * Downloads a file located at the given URL and writes it to a temporary file.
     *
     * @param url the URL the remote file is located at
     * @return the path the file was downloaded to, or null in case of an error
     */
    private Path downloadXML(URL url) {
        Path result;
        try {
            result = Files.createTempFile(Constants.FILE_PREFIX, null);
            try (ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream()); FileOutputStream
                    fileOutputStream = new FileOutputStream(result.toFile())) {
                fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            }
        } catch (IOException e) {
            return null;
        }
        return result;
    }

    /**
     * Returns the update information containing all releases newer than the current version of ProDisFuzz.
     *
     * @return the information about all newer available releases
     */
    public ReleaseInformation[] getReleaseInformation() {
        if (releaseInformation == null) {
            return null;
        }
        return releaseInformation.clone();
    }
}
