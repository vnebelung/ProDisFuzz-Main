/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.ProtocolPart;
import model.ProtocolPart.Type;
import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;
import model.runnable.XMLGenR;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class LoadXMLPartC implements the functionality to load an XML file and
 * creating the protocol parts.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class LoadXMLPartC extends AbstractC {

    /**
     * The XML input file path.
     */
    private final Path filePath;

    /**
     * Instantiates a new load XML part component.
     *
     * @param runnable the parent runnable
     * @param filePath the input file path
     */
    public LoadXMLPartC(final AbstractR runnable, final Path filePath) {
        super(runnable);
        this.filePath = filePath;
    }

    /**
     * Creates the protocol parts
     *
     * @param document the DOM document
     * @return the protocol parts
     * @throws XPathExpressionException
     */
    public List<ProtocolPart> create(final Document document)
            throws XPathExpressionException {
        final List<ProtocolPart> parts = new ArrayList<ProtocolPart>();
        // Create the node list
        final NodeList nodes = document.getElementsByTagName(XMLGenR.XML_PARTS)
                .item(0).getChildNodes();
        // Create for each node the particular protocol part
        int count = 0;
        for (int i = 0; i < nodes.getLength() && !runnable.isInterrupted(); i++) {
            if (nodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                runnable.setStateMessage("i:Loading part element #" + count
                        + " ...", RunnableState.RUNNING);
                switch (nodes.item(i).getNodeName()) {
                    case XMLGenR.XML_PART_VAR:
                        parts.add(new ProtocolPart(Type.VAR)); // NOPMD
                        break;
                    case XMLGenR.XML_PART_FIX:
                        parts.add(new ProtocolPart(Type.FIXED)); // NOPMD
                        break;
                    default:
                        break;
                }
                runnable.increaseProgress("s:done.", RunnableState.RUNNING);
                runnable.sleep(AbstractR.SLEEPING_TIME);
                count++;
            }
        }
        return parts;
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
            if (line.indexOf("<" + XMLGenR.XML_PART_FIX) != -1
                    || line.indexOf("<" + XMLGenR.XML_PART_SEMIVAR) != -1
                    || line.indexOf("<" + XMLGenR.XML_PART_VAR) != -1) {
                steps++;
            }
        }
        return steps;
    }

}