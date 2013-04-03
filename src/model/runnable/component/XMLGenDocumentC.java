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
 * The Class XMLGenDocumentC implements the functionality to create a DOM
 * document.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class XMLGenDocumentC extends AbstractC {

    /**
     * Instantiates a new XML generation document component.
     *
     * @param runnable the runnable
     */
    public XMLGenDocumentC(final AbstractR runnable) {
        super(runnable);
    }

    /**
     * Creates a document object by building a DOM tree
     *
     * @return the DOM document
     */
    public Document create() throws DOMException, ParserConfigurationException {
        runnable.setStateMessage("i:Creating document ...",
                RunnableState.RUNNING);
        // Create the document
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
