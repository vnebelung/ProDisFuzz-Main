/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;
import org.w3c.dom.Document;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The Class ReportGenWriteDOMC implements the functionality to write the DOM
 * structure to the HTML output file.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class ReportGenWriteDOMC extends AbstractC { // NOPMD

    /**
     * The output path.
     */
    private final Path outputPath;

    /**
     * Instantiates a new report generation write DOM component.
     *
     * @param runnable   the runnable
     * @param outputPath the output path
     */
    public ReportGenWriteDOMC(final AbstractR runnable, final Path outputPath) {
        super(runnable);
        this.outputPath = outputPath;
    }

    /**
     * Writes the DOM document to a HTML file.
     *
     * @param document the DOM document
     * @param output   the output name
     * @throws TransformerFactoryConfigurationError
     *
     * @throws TransformerException
     * @throws IOException
     */
    public void write(final Document document, final String output)
            throws TransformerFactoryConfigurationError, TransformerException,
            IOException {
        runnable.setStateMessage("i:Transforming report structure to file ...",
                RunnableState.RUNNING);
        final DOMSource source = new DOMSource(document);
        final StreamResult streamResult = new StreamResult(
                Files.newOutputStream(outputPath.resolve(output + ".html")));
        final Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
        // Indent the elements in the XML structure by 2 spaces
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "2");
        // Add a doctype
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
                "-//W3C//DTD XHTML 1.0 Transitional//EN");
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,
                "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
        // Transform the DOM TO XHTML
        transformer.transform(source, streamResult);
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
