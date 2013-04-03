/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

/**
 * The Class LoadXMLXPathC implements the functionality to load an XML file with
 * its XPath.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class LoadXMLXPathC extends AbstractC {

    /**
     * Instantiates a new load XML XPath component.
     *
     * @param runnable the parent runnable
     */
    public LoadXMLXPathC(final AbstractR runnable) {
        super(runnable);
    }

    /**
     * Creates the XPath
     *
     * @return the XPath
     * @throws XPathFactoryConfigurationException
     *
     */
    public XPath create() throws XPathFactoryConfigurationException {
        runnable.setStateMessage("i:Creating XPath ...", RunnableState.RUNNING);
        final XPath xpath = XPathFactory.newInstance(
                XPathConstants.DOM_OBJECT_MODEL).newXPath();
        runnable.increaseProgress("s:done.", RunnableState.RUNNING);
        return xpath;
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