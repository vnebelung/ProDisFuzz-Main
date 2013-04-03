/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathFactoryConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The Class LoadXMLDocumentC implements the functionality create a DOM
 * document.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class LoadXMLDocumentC extends AbstractC {

    /**
     * The XML input file path.
     */
    private final Path filePath;

    /**
     * Instantiates a new load XML document component.
     *
     * @param runnable the parent runnable
     * @param filePath the input file path
     */
    public LoadXMLDocumentC(final AbstractR runnable, final Path filePath) {
        super(runnable);
        this.filePath = filePath;
    }

    /**
     * Creates the DOM document
     *
     * @return the DOM document
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public Document create() throws XPathFactoryConfigurationException,
            SAXException, IOException, ParserConfigurationException {
        runnable.setStateMessage("i:Creating document ...",
                RunnableState.RUNNING);
        final DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                .newInstance();
        docBuilderFactory.setNamespaceAware(true);
        final Document document = docBuilderFactory.newDocumentBuilder().parse(
                Files.newInputStream(filePath));
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