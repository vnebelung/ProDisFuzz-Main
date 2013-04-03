/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.runnable.component;

import model.RunnableThread.RunnableState;
import model.runnable.AbstractR;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidParameterException;

/**
 * The Class LoadXMLValidateC implements the functionality to validate a XML
 * file.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class LoadXMLValidateC extends AbstractC {

    /**
     * The XML input file path.
     */
    private final Path filePath;
    /**
     * The validaton error flag.
     */
    private boolean validationSuccess;

    /**
     * Instantiates a new load XML validate component.
     *
     * @param runnable the parent runnable
     * @param filePath the input file path
     */
    public LoadXMLValidateC(final AbstractR runnable, final Path filePath) {
        super(runnable);
        this.filePath = filePath;
    }

    /**
     * Validates a XML document over a schema.
     *
     * @throws SAXException              the sAX exception
     * @throws IOException               Signals that an I/O exception has occurred.
     * @throws InvalidParameterException the invalid parameter exception
     */
    public void validate() throws SAXException, IOException,
            InvalidParameterException {
        // Initializes the error handler
        final ErrorHandler errorHandler = new ErrorHandler() {
            @Override
            public void warning(final SAXParseException e) throws SAXException { // NOPMD
                runnable.setStateMessage(
                        "(Line " + e.getLineNumber() + ","
                                + e.getColumnNumber() + ")" + e.getMessage(),
                        RunnableState.RUNNING);
            }

            @Override
            public void fatalError(final SAXParseException e) // NOPMD
                    throws SAXException {
                validationSuccess = false;
                runnable.setStateMessage(
                        "(Line " + e.getLineNumber() + ","
                                + e.getColumnNumber() + ")" + e.getMessage(),
                        RunnableState.RUNNING);
            }

            @Override
            public void error(final SAXParseException e) throws SAXException { // NOPMD
                validationSuccess = false;
                runnable.setStateMessage(
                        "(Line " + e.getLineNumber() + ","
                                + e.getColumnNumber() + ")" + e.getMessage(),
                        RunnableState.RUNNING);
            }
        };
        runnable.setStateMessage("i:Validate against schema ...",
                RunnableState.RUNNING);
        // Load the XML schema
        final SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        final Schema schema = schemaFactory.newSchema(new StreamSource(getClass().getClassLoader()
                .getResourceAsStream("xml/schema.xsd")));
        // Attach schema to validator
        final Validator validator = schema.newValidator();
        validator.setErrorHandler(errorHandler);
        // Validate the whole XML document
        validationSuccess = true;
        validator.validate(new StreamSource(Files.newInputStream(filePath)));
        if (validationSuccess) {
            runnable.increaseProgress("s:done.", RunnableState.RUNNING);
        } else {
            throw new InvalidParameterException("Invalid XML file.");
        }
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