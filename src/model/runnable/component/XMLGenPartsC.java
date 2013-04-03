/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;
import model.runnable.XMLGenR;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Class XMLGenPartsC implements the functionality to create the parts
 * element.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class XMLGenPartsC extends AbstractC {

    /**
     * Instantiates a new XML generation parts component.
     *
     * @param runnable the runnable
     */
    public XMLGenPartsC(final AbstractR runnable) {
        super(runnable);
    }

    /**
     * Creates a parts element.
     *
     * @param document the DOM document
     */
    public void create(final Document document) {
        runnable.setStateMessage("i:Creating parts element ...",
                RunnableState.RUNNING);
        // Create the parts element
        final Element parts = document.createElement(XMLGenR.XML_PARTS);
        // Append the parts element to the root element
        document.getElementsByTagName(XMLGenR.XML_ROOT).item(0)
                .appendChild(parts);
        runnable.increaseProgress("s:done.", RunnableState.RUNNING);
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
