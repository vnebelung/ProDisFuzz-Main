/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.ProtocolPart;
import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;
import model.runnable.XMLGenR;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class LoadXMLContentC implements the functionality to load an XML file
 * and creates the parts' content.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class LoadXMLContentC extends AbstractC {

    /**
     * The XML input file path.
     */
    private final Path filePath;

    /**
     * Instantiates a new load XML content component.
     *
     * @param runnable the parent runnable
     * @param filePath the input file path
     */
    public LoadXMLContentC(final AbstractR runnable, final Path filePath) {
        super(runnable);
        this.filePath = filePath;
    }

    /**
     * Creates the content elements for every protocol part
     *
     * @param parts    the protocol parts
     * @param document the DOM document
     */
    public void create(final List<ProtocolPart> parts, final Document document) {
        int count = 0;
        final NodeList partNodes = document
                .getElementsByTagName(XMLGenR.XML_PARTS).item(0)
                .getChildNodes();
        NodeList contentNodes;
        // Create the content for each part element
        for (int i = 0; i < partNodes.getLength() && !runnable.isInterrupted(); i++) {
            // Proceed if current child is an element node
            if (partNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                switch (partNodes.item(i).getNodeName()) {
                    case XMLGenR.XML_PART_VAR:
                        runnable.setStateMessage("i:Creating content element ...",
                                RunnableState.RUNNING);
                        parts.get(count).addContent(
                                createVarContent(partNodes.item(i)));
                        runnable.increaseProgress("s:done.", RunnableState.RUNNING);
                        runnable.sleep(AbstractR.SLEEPING_TIME);
                        break;
                    case XMLGenR.XML_PART_SEMIVAR:
                    case XMLGenR.XML_PART_FIX:
                        contentNodes = partNodes.item(i).getChildNodes();
                        // Create the content of this part for each content element
                        for (int j = 0; j < contentNodes.getLength()
                                && !runnable.isInterrupted(); j++) {
                            if (contentNodes.item(j).getNodeType() == Node.ELEMENT_NODE) {
                                runnable.setStateMessage(
                                        "i:Loading content element ...",
                                        RunnableState.RUNNING);
                                parts.get(count).addContent(
                                        createSemiVarAndFixContent(contentNodes
                                                .item(j)));
                                runnable.increaseProgress("s:done.",
                                        RunnableState.RUNNING);
                                runnable.sleep(AbstractR.SLEEPING_TIME);
                            }
                        }
                        break;
                    default:
                        break;
                }
                count++;
            }
        }
    }

    /**
     * Creates the content for a semi-variable or fixed part by reading out all
     * byte nodes of a content node.
     *
     * @param node the DOM node representing the content element of a
     *             semi-variable or fixed part
     * @return the byte content
     */
    private List<Byte> createSemiVarAndFixContent(final Node node) {
        List<Byte> bytes;
        // Create the content out of all byte elements
        bytes = new ArrayList<Byte>(); // NOPMD
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            if (node.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE) {
                bytes.add(Byte.parseByte(node.getChildNodes().item(i)
                        .getTextContent()));
            }
        }
        return bytes;
    }

    /**
     * Creates the content for a variable part by reading the maxlength
     * attribute.
     *
     * @param node the DOM node representing the variable part
     * @return the byte content
     */
    private List<Byte> createVarContent(final Node node) {
        // Add as many null bytes as the maximum length attribute
        final int maxlength = Integer.parseInt(node.getAttributes()
                .getNamedItem(XMLGenR.XML_MAXLENGTH).getNodeValue());
        final List<Byte> bytes = new ArrayList<Byte>(maxlength);
        for (int j = 0; j < maxlength; j++) {
            bytes.add(null);
        }
        return bytes;
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.component.AbstractComponent#getTotalProgress()
     */
    @Override
    public int getTotalProgress() {
        int steps = 0;
        List<String> lines = new ArrayList<String>();
        try {
            lines = Files.readAllLines(filePath, Charset.forName("UTF-8"));
        } catch (IOException e) { // NOPMD
            // Should not happen
        }
        for (String line : lines) {
            if (line.indexOf("<" + XMLGenR.XML_CONTENT) != -1
                    || line.indexOf("<" + XMLGenR.XML_PART_VAR) != -1) {
                steps++;
            }
        }
        return steps;
    }

}