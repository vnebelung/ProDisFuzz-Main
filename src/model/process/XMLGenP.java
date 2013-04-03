/**
 * This file is part of the ProDisFuzz program.
 * (c) by Volker Nebelung, 2012
 */
package model.process;

import model.ProtocolPart;
import model.runnable.XMLGenR;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * The Class XMLGenP encapsulates the process of XML structure generation.
 *
 * @author Volker Nebelung
 * @version 1.0
 */
public class XMLGenP extends AbstractThreadP {

    /**
     * The list of protocol parts.
     */
    private List<ProtocolPart> parts;

    /**
     * The output file path.
     */
    private Path filePath;

    /**
     * Instantiates a new XML generation process.
     */
    public XMLGenP() {
        super();
        parts = new ArrayList<ProtocolPart>();
        filePath = null; // NOPMD
    }

    /**
     * Sets the XML file path.
     *
     * @param path the output file
     */
    public void setOutputPath(final String path) {
        final Path newPath = Paths.get(path).toAbsolutePath().normalize();
        if (!newPath.equals(filePath)) {
            if (newPath.getParent() != null
                    && Files.isDirectory(newPath.getParent())
                    // TODO: Use Files.probeContentType here. At the moment this
                    // function is not working under Mac OS 10.8
                    && newPath.getFileName().toString().endsWith(".xml")
                    && Files.isWritable(newPath.getParent())) {
                filePath = newPath;
            } else {
                filePath = null; // NOPMD
            }
            spreadUpdate(false);
        }
    }

    /**
     * Resets all variables to the default value and notifies all observers.
     */
    public void reset() {
        parts.clear();
        filePath = null; // NOPMD
        super.reset();
    }

    /**
     * Initiates the protocol parts.
     *
     * @param parts the protocol parts
     */
    public void initParts(final List<ProtocolPart> parts) {
        this.parts = parts;
    }

    /**
     * Gets the output file path.
     *
     * @return the output file path
     */
    public Path getFilePath() {
        return filePath;
    }

    /**
     * Starts the thread with the XML generation runnable.
     */
    public void start() {
        super.start(new XMLGenR(parts, filePath));
    }

    /*
     * (non-Javadoc)
     *
     * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    @Override
    public void update(final Observable observable, final Object arg) {
        super.update(observable, arg);
    }

}