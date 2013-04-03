/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * The Class ReportGenDocumentC implements the functionality to generate the DOM
 * document for the HTML output of the report.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ReportGenDocumentC extends AbstractC {

    /**
     * Instantiates a new report generation document component.
     *
     * @param runnable the runnable
     */
    public ReportGenDocumentC(final AbstractR runnable) {
        super(runnable);
    }

    /**
     * Creates a document object by building a DOM tree
     *
     * @return the DOM document
     * @throws ParserConfigurationException
     * @throws DOMException
     */
    public Document create() throws DOMException, ParserConfigurationException {
        runnable.setStateMessage("i:Creating document ...",
                RunnableState.RUNNING);
        final Document document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder().getDOMImplementation()
                .createDocument(null, null, null);
        runnable.increaseProgress("s:done.", RunnableState.RUNNING);
        return document;
    }

    /*
     * (non-Javadoc)
     *
     * @see model.runnable.component.AbstractComponent#getTotalProgress()
     */
    @Override
    public int getTotalProgress() {
        return 1;
    }

}
