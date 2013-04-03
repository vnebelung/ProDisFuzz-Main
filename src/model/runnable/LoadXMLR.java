/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable;

import model.ProtocolPart;
import model.RunnableThread.RunnableState;
import model.runnable.component.LoadXMLContentC;
import model.runnable.component.LoadXMLDocumentC;
import model.runnable.component.LoadXMLPartC;
import model.runnable.component.LoadXMLValidateC;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class LoadXMLR implements the runnable which is responsible for read all
 * protocol files in a directory and store them.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class LoadXMLR extends AbstractR {

    /**
     * The list of protocol parts.
     */
    private List<ProtocolPart> parts;

    /**
     * The load XML validate component.
     */
    final private LoadXMLValidateC loadXMLValidateC;

    /**
     * The load XML document component.
     */
    final private LoadXMLDocumentC loadXMLDocumentC;

    /**
     * The load XML part component.
     */
    final private LoadXMLPartC loadXMLPartC;

    /**
     * The load XML content component.
     */
    final private LoadXMLContentC loadXMLContentC;

    /**
     * Instantiates a new load XML runnable.
     *
     * @param filePath the XML file path
     */
    public LoadXMLR(final Path filePath) {
        super();
        parts = new ArrayList<ProtocolPart>();
        loadXMLValidateC = new LoadXMLValidateC(this, filePath);
        loadXMLDocumentC = new LoadXMLDocumentC(this, filePath);
        loadXMLPartC = new LoadXMLPartC(this, filePath);
        loadXMLContentC = new LoadXMLContentC(this, filePath);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        // Validate the whole XML document
        if (!isInterrupted()) {
            try {
                loadXMLValidateC.validate();
            } catch (SAXException | IOException | InvalidParameterException e) {
                interrupt("e:" + e.getMessage());
            }
            sleep(SLEEPING_TIME);
        }
        // Create the document
        Document document = null;
        if (!isInterrupted()) {
            try {
                document = loadXMLDocumentC.create();
            } catch (XPathFactoryConfigurationException | SAXException
                    | IOException | ParserConfigurationException e) {
                interrupt("e:" + e.getMessage());
            }
            sleep(SLEEPING_TIME);
        }
        // Create the parts
        if (!isInterrupted()) {
            try {
                parts = loadXMLPartC.create(document);
            } catch (XPathExpressionException e) {
                interrupt("e:" + e.getMessage());
            }
            sleep(SLEEPING_TIME);
        }
        // Create the contents
        if (!isInterrupted()) {
            loadXMLContentC.create(parts, document);
            sleep(SLEEPING_TIME);
        }
        spreadUpdate(isInterrupted() ? RunnableState.CANCELED
                : RunnableState.FINISHED);
    }

    /**
     * Returns the protocol parts loaded from the XML file.
     *
     * @return the protocol parts
     */
    public List<ProtocolPart> getParts() {
        return parts;
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.AbstractRunnable#setTotalProgress()
     */
    @Override
    protected void setTotalProgress() {
        totalProgress = loadXMLValidateC.getTotalProgress();
        totalProgress += loadXMLDocumentC.getTotalProgress();
        totalProgress += loadXMLPartC.getTotalProgress();
        totalProgress += loadXMLContentC.getTotalProgress();
    }
}
