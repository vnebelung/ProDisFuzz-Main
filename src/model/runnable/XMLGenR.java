/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable;

import model.ProtocolPart;
import model.RunnableThread.RunnableState;
import model.runnable.component.*;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * The Class XMLGenR implements the runnable which is responsible for read all
 * protocol files in a directory and store them.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class XMLGenR extends AbstractR {

    /**
     * The XML generation document component.
     */
    final private XMLGenDocumentC xmlGenDocumentC;

    /**
     * The XML generation root component.
     */
    final private XMLGenRootC xmlGenRootC;

    /**
     * The XML generation parts component.
     */
    final private XMLGenPartsC xmlGenPartsC;

    /**
     * The XML generation part component.
     */
    final private XMLGenPartC xmlGenPartC;

    /**
     * The XML generation content component.
     */
    final private XMLGenContentC xmlGenContentC;

    /**
     * The XML generation write file component.
     */
    final private XMLGenWriteFileC xmlGenWriteFileC;

    /**
     * The XML name of the root element.
     */
    public final static String XML_ROOT = "prodisfuzz";

    /**
     * The XML name of the parts element.
     */
    public final static String XML_PARTS = "parts";

    /**
     * The XML name of the variable part element.
     */
    public final static String XML_PART_VAR = "partvar";

    /**
     * The XML name of the semi-variable part element.
     */
    public final static String XML_PART_SEMIVAR = "partsemivar";

    /**
     * The XML name of the fixed part element.
     */
    public final static String XML_PART_FIX = "partfix";

    /**
     * The XML name of the content element.
     */
    public final static String XML_CONTENT = "content";

    /**
     * The XML name of the maximum length attribute.
     */
    public final static String XML_MAXLENGTH = "maxlength";

    /**
     * The XML name of the minimum length attribute.
     */
    public final static String XML_MINLENGTH = "minlength";

    /**
     * Instantiates a new collect runnable.
     *
     * @param parts    the protocol parts
     * @param filePath the output file path
     */
    public XMLGenR(final List<ProtocolPart> parts, final Path filePath) {
        super();
        xmlGenDocumentC = new XMLGenDocumentC(this);
        xmlGenRootC = new XMLGenRootC(this);
        xmlGenPartsC = new XMLGenPartsC(this);
        xmlGenPartC = new XMLGenPartC(this, parts);
        xmlGenContentC = new XMLGenContentC(this, parts);
        xmlGenWriteFileC = new XMLGenWriteFileC(this, filePath);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        Document document = null; // NOPMD
        // Create the document
        if (!Thread.currentThread().isInterrupted()) {
            try {
                document = xmlGenDocumentC.create();
                sleep(SLEEPING_TIME);
            } catch (DOMException | ParserConfigurationException e) {
                interrupt("e:" + e.getMessage());
            }
        }
        // Create the XML root element
        if (!Thread.currentThread().isInterrupted()) {
            xmlGenRootC.create(document);
            sleep(SLEEPING_TIME);
        }
        // Create the XML parts element
        if (!Thread.currentThread().isInterrupted()) {
            xmlGenPartsC.create(document);
            sleep(SLEEPING_TIME);
        }
        // Create a part XML element for each protocol part
        if (!Thread.currentThread().isInterrupted()) {
            xmlGenPartC.create(document);
            sleep(SLEEPING_TIME);
        }
        // Create a content XML element for each content of the particular part
        if (!Thread.currentThread().isInterrupted()) {
            xmlGenContentC.create(document);
            sleep(SLEEPING_TIME);
        }
        // Write the XML structure to file
        if (!Thread.currentThread().isInterrupted()) {
            try {
                xmlGenWriteFileC.writeToFile(document);
            } catch (TransformerException | IOException e) {
                interrupt("e:" + e.getMessage());
            }
        }
        spreadUpdate(isInterrupted() ? RunnableState.CANCELED
                : RunnableState.FINISHED);
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.AbstractRunnable#setTotalProgress()
     */
    @Override
    protected void setTotalProgress() {
        totalProgress = xmlGenDocumentC.getTotalProgress();
        totalProgress += xmlGenRootC.getTotalProgress();
        totalProgress += xmlGenPartsC.getTotalProgress();
        totalProgress += xmlGenPartC.getTotalProgress();
        totalProgress += xmlGenContentC.getTotalProgress();
        totalProgress += xmlGenWriteFileC.getTotalProgress();
    }

}