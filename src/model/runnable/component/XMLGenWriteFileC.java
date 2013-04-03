/**
 f * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The Class XMLGenWriteFileC implements the functionality to write the DOM
 * structure to a XML file.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class XMLGenWriteFileC extends AbstractC {

    /**
     * The output file path.
     */
    private final Path filePath;

    /**
     * Instantiates a new XML generation write file component.
     *
     * @param runnable the runnable
     * @param filePath the output file path
     */
    public XMLGenWriteFileC(final AbstractR runnable, final Path filePath) {
        super(runnable);
        this.filePath = filePath;
    }

    /**
     * Writes the DOM structure to a XML file.
     *
     * @param document the DOM document
     * @throws TransformerException
     * @throws IOException
     */
    public void writeToFile(final Document document)
            throws TransformerException, IOException {
        runnable.setStateMessage("i:Transforming XML structure to file ...",
                RunnableState.RUNNING);
        final DOMSource source = new DOMSource(document);
        final StreamResult result = new StreamResult(
                Files.newOutputStream(filePath));
        final Transformer transformer = TransformerFactory.newInstance()
                .newTransformer();
        // Indent the elements in the XML structure by 2 spaces
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "2");
        // Transform the DOM TO XML
        transformer.transform(source, result);
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
